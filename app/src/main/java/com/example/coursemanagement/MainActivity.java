package com.example.coursemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coursemanagement.adapter.CourseAdapter;
import com.example.coursemanagement.api.ApiClient;
import com.example.coursemanagement.api.ApiService;
import com.example.coursemanagement.databinding.ActivityMainBinding;
import com.example.coursemanagement.model.Category;
import com.example.coursemanagement.model.Course;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CourseAdapter adapter;

    private final List<Course> allCourses = new ArrayList<>();
    private final List<Category> allCategories = new ArrayList<>();
    private final List<Course> filteredCourses = new ArrayList<>();

    private String currentSearchQuery = "";
    private String selectedCategoryId = "ALL";

    private static final int REQUEST_CODE_REFRESH = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setupRecyclerView();
        setupSearchView();
        setupSwipeRefresh();

        binding.fabAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditCourseActivity.class);
            startActivityForResult(intent, REQUEST_CODE_REFRESH);
        });

        binding.btnRetry.setOnClickListener(v -> loadData());

        loadData();
    }

    private void setupRecyclerView() {
        adapter = new CourseAdapter(this, course -> {
            Intent intent = new Intent(MainActivity.this, CourseDetailActivity.class);
            intent.putExtra("COURSE", course);
            startActivityForResult(intent, REQUEST_CODE_REFRESH);
        });
        binding.rvCourses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCourses.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query != null ? query : "";
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText != null ? newText : "";
                applyFilters();
                return true;
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getApiService(this);

        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategories.clear();
                    allCategories.addAll(response.body());
                    adapter.setCategories(allCategories);
                    setupCategorySpinner();
                }
                fetchCourses();
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                fetchCourses();
            }
        });
    }

    private void fetchCourses() {
        ApiService apiService = ApiClient.getApiService(this);
        apiService.getCourses().enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    allCourses.clear();
                    allCourses.addAll(response.body());
                    applyFilters();
                } else {
                    showError("Không thể tải danh sách khóa học (mã lỗi: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
                showError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }

    private void setupCategorySpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Tất cả danh mục");

        for (Category cat : allCategories) {
            spinnerItems.add(cat.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerItems
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilterCategory.setAdapter(spinnerAdapter);

        binding.spinnerFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedCategoryId = "ALL";
                } else {
                    selectedCategoryId = allCategories.get(position - 1).getId();
                }
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = "ALL";
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        filteredCourses.clear();
        String query = currentSearchQuery.trim().toLowerCase();

        for (Course course : allCourses) {
            boolean matchesSearch = query.isEmpty() ||
                    (course.getTitle() != null && course.getTitle().toLowerCase().contains(query)) ||
                    (course.getInstructor() != null && course.getInstructor().toLowerCase().contains(query));

            boolean matchesCategory = selectedCategoryId.equals("ALL") ||
                    (course.getCategoryId() != null && course.getCategoryId().equals(selectedCategoryId));

            if (matchesSearch && matchesCategory) {
                filteredCourses.add(course);
            }
        }

        adapter.setCourses(filteredCourses);

        if (filteredCourses.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            if (allCourses.isEmpty()) {
                binding.tvEmpty.setText("Chưa có khóa học nào. Bấm nút '+' để thêm mới!");
            } else {
                binding.tvEmpty.setText("Không tìm thấy khóa học phù hợp với bộ lọc.");
            }
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (allCourses.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.tvEmpty.setText(message);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_categories) {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivityForResult(intent, REQUEST_CODE_REFRESH);
            return true;
        } else if (id == R.id.action_settings) {
            showConfigApiDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showConfigApiDialog() {
        String currentUrl = ApiClient.getBaseUrl(this);
        EditText input = new EditText(this);
        input.setPadding(32, 24, 32, 24);
        input.setText(currentUrl);

        new AlertDialog.Builder(this)
                .setTitle("Cấu hình URL MockAPI")
                .setMessage("Nhập Base URL MockAPI của bạn (Ví dụ: https://...mockapi.io/api/v1/):")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newUrl = input.getText().toString().trim();
                    if (!newUrl.isEmpty()) {
                        ApiClient.setBaseUrl(MainActivity.this, newUrl);
                        Toast.makeText(MainActivity.this, "Đã cập nhật URL API!", Toast.LENGTH_SHORT).show();
                        loadData();
                    }
                })
                .setNeutralButton("Mặc định", (dialog, which) -> {
                    ApiClient.setBaseUrl(MainActivity.this, ApiClient.DEFAULT_BASE_URL);
                    Toast.makeText(MainActivity.this, "Khôi phục URL mặc định!", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REFRESH && resultCode == RESULT_OK) {
            loadData();
        }
    }
}
