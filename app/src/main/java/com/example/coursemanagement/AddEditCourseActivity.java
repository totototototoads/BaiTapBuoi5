package com.example.coursemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.coursemanagement.api.ApiClient;
import com.example.coursemanagement.api.ApiService;
import com.example.coursemanagement.databinding.ActivityAddEditCourseBinding;
import com.example.coursemanagement.model.Category;
import com.example.coursemanagement.model.Course;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditCourseActivity extends AppCompatActivity {

    private ActivityAddEditCourseBinding binding;
    private Course editingCourse = null;
    private final List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent().hasExtra("COURSE")) {
            editingCourse = (Course) getIntent().getSerializableExtra("COURSE");
            binding.toolbar.setTitle("Sửa Khóa học");
            binding.btnSave.setText("CẬP NHẬT KHÓA HỌC");
        } else {
            binding.toolbar.setTitle("Thêm Khóa học Mới");
            binding.btnSave.setText("THÊM KHÓA HỌC");
        }

        loadCategories();

        binding.btnPreviewImage.setOnClickListener(v -> {
            String url = getTextInput(binding.etThumbnail);
            if (!url.isEmpty()) {
                binding.imgPreview.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(url)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_dialog_alert)
                        .into(binding.imgPreview);
            } else {
                Toast.makeText(this, "Vui lòng nhập URL ảnh bìa trước", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void loadCategories() {
        ApiService apiService = ApiClient.getApiService(this);
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    setupCategorySpinner();
                } else {
                    Toast.makeText(AddEditCourseActivity.this, "Không thể tải danh mục khóa học", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                Toast.makeText(AddEditCourseActivity.this, "Lỗi kết nối khi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategorySpinner() {
        List<String> categoryNames = new ArrayList<>();
        int selectedIndex = 0;

        for (int i = 0; i < categoryList.size(); i++) {
            Category cat = categoryList.get(i);
            categoryNames.add(cat.getName());
            if (editingCourse != null && cat.getId() != null && cat.getId().equals(editingCourse.getCategoryId())) {
                selectedIndex = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        if (editingCourse != null) {
            binding.spinnerCategory.setSelection(selectedIndex);
            prefillCourseData();
        }
    }

    private void prefillCourseData() {
        if (editingCourse == null) return;

        binding.etTitle.setText(editingCourse.getTitle());
        binding.etInstructor.setText(editingCourse.getInstructor());
        binding.etPrice.setText(String.valueOf(editingCourse.getPrice()));
        binding.etDuration.setText(String.valueOf(editingCourse.getDuration()));
        binding.etThumbnail.setText(editingCourse.getThumbnail());
        binding.etTrailerUrl.setText(editingCourse.getTrailerUrl());
        binding.etDescription.setText(editingCourse.getDescription());

        if (editingCourse.getThumbnail() != null && !editingCourse.getThumbnail().isEmpty()) {
            binding.imgPreview.setVisibility(View.VISIBLE);
            Glide.with(this).load(editingCourse.getThumbnail()).into(binding.imgPreview);
        }
    }

    private void validateAndSave() {
        String title = getTextInput(binding.etTitle);
        String instructor = getTextInput(binding.etInstructor);
        String priceStr = getTextInput(binding.etPrice);
        String durationStr = getTextInput(binding.etDuration);
        String thumbnail = getTextInput(binding.etThumbnail);
        String trailerUrl = getTextInput(binding.etTrailerUrl);
        String description = getTextInput(binding.etDescription);

        if (title.isEmpty()) {
            binding.etTitle.setError("Vui lòng nhập tên khóa học!");
            binding.etTitle.requestFocus();
            return;
        }

        if (categoryList.isEmpty()) {
            Toast.makeText(this, "Chưa có danh mục nào. Vui lòng tạo danh mục trước!", Toast.LENGTH_LONG).show();
            return;
        }

        int selectedPos = binding.spinnerCategory.getSelectedItemPosition();
        if (selectedPos < 0 || selectedPos >= categoryList.size()) {
            Toast.makeText(this, "Vui lòng chọn danh mục hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryId = categoryList.get(selectedPos).getId();

        if (instructor.isEmpty()) {
            binding.etInstructor.setError("Vui lòng nhập tên giảng viên!");
            binding.etInstructor.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                binding.etPrice.setError("Học phí không được là số âm!");
                binding.etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            binding.etPrice.setError("Học phí không hợp lệ!");
            binding.etPrice.requestFocus();
            return;
        }

        double duration;
        try {
            duration = Double.parseDouble(durationStr);
            if (duration < 0) {
                binding.etDuration.setError("Thời lượng không được là số âm!");
                binding.etDuration.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            binding.etDuration.setError("Thời lượng không hợp lệ!");
            binding.etDuration.requestFocus();
            return;
        }

        if (thumbnail.isEmpty()) {
            thumbnail = "https://picsum.photos/300/200";
        }

        Course course = editingCourse != null ? editingCourse : new Course();
        course.setTitle(title);
        course.setCategoryId(categoryId);
        course.setInstructor(instructor);
        course.setPrice(price);
        course.setDuration(duration);
        course.setThumbnail(thumbnail);
        course.setTrailerUrl(trailerUrl);
        course.setDescription(description);

        if (editingCourse == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            course.setCreatedAt(sdf.format(new Date()));
        }

        saveCourseToApi(course);
    }

    private void saveCourseToApi(Course course) {
        binding.btnSave.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getApiService(this);
        Call<Course> call;

        if (editingCourse == null) {
            call = apiService.createCourse(course);
        } else {
            call = apiService.updateCourse(editingCourse.getId(), course);
        }

        call.enqueue(new Callback<Course>() {
            @Override
            public void onResponse(@NonNull Call<Course> call, @NonNull Response<Course> response) {
                binding.btnSave.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AddEditCourseActivity.this, "Lưu khóa học thành công!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("COURSE", response.body());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(AddEditCourseActivity.this, "Lỗi khi lưu khóa học: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Course> call, @NonNull Throwable t) {
                binding.btnSave.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AddEditCourseActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTextInput(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
