package com.example.coursemanagement;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.coursemanagement.adapter.CategoryAdapter;
import com.example.coursemanagement.api.ApiClient;
import com.example.coursemanagement.api.ApiService;
import com.example.coursemanagement.databinding.ActivityCategoryBinding;
import com.example.coursemanagement.model.Category;
import com.example.coursemanagement.model.Course;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;
    private CategoryAdapter adapter;
    private final List<Category> categoriesList = new ArrayList<>();
    private final List<Course> allCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();

        binding.swipeRefreshLayout.setOnRefreshListener(this::loadData);
        binding.fabAddCategory.setOnClickListener(v -> showCategoryDialog(null));

        loadData();
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(this, new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showCategoryDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                checkConstraintAndDeleteCategory(category);
            }
        });
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCategories.setAdapter(adapter);
    }

    private void loadData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getApiService(this);

        apiService.getCourses().enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(@NonNull Call<List<Course>> call, @NonNull Response<List<Course>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCourses.clear();
                    allCourses.addAll(response.body());
                }
                fetchCategories();
            }

            @Override
            public void onFailure(@NonNull Call<List<Course>> call, @NonNull Throwable t) {
                fetchCategories();
            }
        });
    }

    private void fetchCategories() {
        ApiService apiService = ApiClient.getApiService(this);
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    categoriesList.clear();
                    categoriesList.addAll(response.body());
                    adapter.setCategories(categoriesList);

                    if (categoriesList.isEmpty()) {
                        binding.layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutEmpty.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, "Không thể tải danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(CategoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCategoryDialog(Category editingCategory) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_category, null);
        EditText etName = dialogView.findViewById(R.id.etCategoryName);
        EditText etDescription = dialogView.findViewById(R.id.etCategoryDescription);

        if (editingCategory != null) {
            etName.setText(editingCategory.getName());
            etDescription.setText(editingCategory.getDescription());
        }

        String title = editingCategory == null ? "Thêm danh mục mới" : "Sửa danh mục";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(CategoryActivity.this, "Tên danh mục không được để trống!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (editingCategory == null) {
                        createCategory(new Category(name, description));
                    } else {
                        editingCategory.setName(name);
                        editingCategory.setDescription(description);
                        updateCategory(editingCategory);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createCategory(Category category) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getApiService(this);
        apiService.createCategory(category).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(CategoryActivity.this, "Đã thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    loadData();
                } else {
                    Toast.makeText(CategoryActivity.this, "Lỗi thêm danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(CategoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCategory(Category category) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getApiService(this);
        apiService.updateCategory(category.getId(), category).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(CategoryActivity.this, "Cập nhật danh mục thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    loadData();
                } else {
                    Toast.makeText(CategoryActivity.this, "Lỗi cập nhật danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(CategoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkConstraintAndDeleteCategory(Category category) {
        int linkedCourseCount = 0;
        for (Course c : allCourses) {
            if (c.getCategoryId() != null && c.getCategoryId().equals(category.getId())) {
                linkedCourseCount++;
            }
        }

        if (linkedCourseCount > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Không thể xóa danh mục!")
                    .setMessage("Danh mục '" + category.getName() + "' hiện đang có " + linkedCourseCount +
                            " khóa học liên kết. Vui lòng xóa hoặc chuyển các khóa học đó sang danh mục khác trước khi xóa danh mục này.")
                    .setPositiveButton("Đã hiểu", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa danh mục '" + category.getName() + "' không?")
                    .setPositiveButton("Xóa", (dialog, which) -> deleteCategory(category.getId()))
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }

    private void deleteCategory(String categoryId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getApiService(this);
        apiService.deleteCategory(categoryId).enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(CategoryActivity.this, "Xóa danh mục thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    loadData();
                } else {
                    Toast.makeText(CategoryActivity.this, "Lỗi xóa danh mục: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(CategoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
