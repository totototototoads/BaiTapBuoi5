package com.example.coursemanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coursemanagement.databinding.ItemCourseBinding;
import com.example.coursemanagement.model.Category;
import com.example.coursemanagement.model.Course;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    private final Context context;
    private List<Course> courseList = new ArrayList<>();
    private final Map<String, String> categoryMap = new HashMap<>();
    private final OnCourseClickListener listener;

    public CourseAdapter(Context context, OnCourseClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setCourses(List<Course> courses) {
        this.courseList = courses != null ? courses : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCategories(List<Category> categories) {
        categoryMap.clear();
        if (categories != null) {
            for (Category cat : categories) {
                categoryMap.put(cat.getId(), cat.getName());
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseBinding binding = ItemCourseBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ItemCourseBinding binding;

        public CourseViewHolder(@NonNull ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Course course) {
            binding.tvCourseTitle.setText(course.getTitle());
            binding.tvInstructor.setText("GV: " + (course.getInstructor() != null ? course.getInstructor() : "N/A"));

            // Get Category Name dynamically from map
            String categoryName = categoryMap.get(course.getCategoryId());
            if (categoryName == null || categoryName.trim().isEmpty()) {
                categoryName = "Danh mục #" + course.getCategoryId();
            }
            binding.tvCategoryName.setText(categoryName);

            // Format price in VNĐ
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            String formattedPrice = formatter.format(course.getPrice()) + " VNĐ";
            binding.tvPrice.setText(formattedPrice);

            // Format duration
            String durationText = ((long) course.getDuration() == course.getDuration()) ?
                    (long) course.getDuration() + " giờ" : course.getDuration() + " giờ";
            binding.tvDuration.setText(durationText);

            // Load Thumbnail image
            Glide.with(context)
                    .load(course.getThumbnail())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_dialog_alert)
                    .centerCrop()
                    .into(binding.imgThumbnail);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCourseClick(course);
                }
            });
        }
    }
}
