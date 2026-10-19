package com.example.coursemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.coursemanagement.api.ApiClient;
import com.example.coursemanagement.api.ApiService;
import com.example.coursemanagement.databinding.ActivityCourseDetailBinding;
import com.example.coursemanagement.model.Category;
import com.example.coursemanagement.model.Course;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseDetailActivity extends AppCompatActivity {

    private ActivityCourseDetailBinding binding;
    private Course course;

    private static final int REQUEST_CODE_EDIT = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        if (getIntent().hasExtra("COURSE")) {
            course = (Course) getIntent().getSerializableExtra("COURSE");
        }

        if (course == null) {
            Toast.makeText(this, "Không tìm thấy thông tin khóa học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayCourseDetails();
        fetchCategoryName();

        binding.btnWatchTrailer.setOnClickListener(v -> {
            if (course.getTrailerUrl() != null && !course.getTrailerUrl().trim().isEmpty()) {
                Intent intent = new Intent(CourseDetailActivity.this, TrailerPlayerActivity.class);
                intent.putExtra("TRAILER_URL", course.getTrailerUrl());
                intent.putExtra("TITLE", course.getTitle());
                startActivity(intent);
            } else {
                Toast.makeText(CourseDetailActivity.this, "Khóa học này chưa có link trailer!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnEditCourse.setOnClickListener(v -> {
            Intent intent = new Intent(CourseDetailActivity.this, AddEditCourseActivity.class);
            intent.putExtra("COURSE", course);
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        });

        binding.btnDeleteCourse.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void displayCourseDetails() {
        binding.tvDetailTitle.setText(course.getTitle());
        binding.tvDetailInstructor.setText("Giảng viên: " + (course.getInstructor() != null ? course.getInstructor() : "Chưa cập nhật"));

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        binding.tvDetailPrice.setText(formatter.format(course.getPrice()) + " VNĐ");

        String durationText = ((long) course.getDuration() == course.getDuration()) ?
                (long) course.getDuration() + " giờ" : course.getDuration() + " giờ";
        binding.tvDetailDuration.setText("Thời lượng: " + durationText);

        binding.tvDetailCreatedAt.setText("Ngày tạo: " + (course.getCreatedAt() != null ? course.getCreatedAt() : "N/A"));

        binding.tvDetailDescription.setText(
                course.getDescription() != null && !course.getDescription().trim().isEmpty()
                        ? course.getDescription()
                        : "Chưa có mô tả cho khóa học này."
        );

        Glide.with(this)
                .load(course.getThumbnail())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert)
                .into(binding.imgDetailThumbnail);
    }

    private void fetchCategoryName() {
        if (course.getCategoryId() == null || course.getCategoryId().trim().isEmpty()) {
            binding.tvDetailCategory.setText("Không có danh mục");
            return;
        }

        ApiService apiService = ApiClient.getApiService(this);
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Category cat : response.body()) {
                        if (cat.getId() != null && cat.getId().equals(course.getCategoryId())) {
                            binding.tvDetailCategory.setText(cat.getName());
                            return;
                        }
                    }
                }
                binding.tvDetailCategory.setText("Danh mục #" + course.getCategoryId());
            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                binding.tvDetailCategory.setText("Danh mục #" + course.getCategoryId());
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa khóa học '" + course.getTitle() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCourse())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCourse() {
        ApiService apiService = ApiClient.getApiService(this);
        apiService.deleteCourse(course.getId()).enqueue(new Callback<Course>() {
            @Override
            public void onResponse(@NonNull Call<Course> call, @NonNull Response<Course> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseDetailActivity.this, "Đã xóa khóa học thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CourseDetailActivity.this, "Lỗi xóa khóa học: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Course> call, @NonNull Throwable t) {
                Toast.makeText(CourseDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            if (data != null && data.hasExtra("COURSE")) {
                course = (Course) data.getSerializableExtra("COURSE");
                displayCourseDetails();
                fetchCategoryName();
            } else {
                finish();
            }
        }
    }
}
