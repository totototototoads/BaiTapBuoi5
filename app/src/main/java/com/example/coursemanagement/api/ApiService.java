package com.example.coursemanagement.api;

import com.example.coursemanagement.model.Category;
import com.example.coursemanagement.model.Course;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Category Endpoints
    @GET("categories")
    Call<List<Category>> getCategories();

    @POST("categories")
    Call<Category> createCategory(@Body Category category);

    @PUT("categories/{id}")
    Call<Category> updateCategory(@Path("id") String id, @Body Category category);

    @DELETE("categories/{id}")
    Call<Category> deleteCategory(@Path("id") String id);

    // Course Endpoints
    @GET("courses")
    Call<List<Course>> getCourses();

    @GET("courses/{id}")
    Call<Course> getCourseById(@Path("id") String id);

    @POST("courses")
    Call<Course> createCourse(@Body Course course);

    @PUT("courses/{id}")
    Call<Course> updateCourse(@Path("id") String id, @Body Course course);

    @DELETE("courses/{id}")
    Call<Course> deleteCourse(@Path("id") String id);
}
