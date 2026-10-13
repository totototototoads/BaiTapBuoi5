package com.example.coursemanagement.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Course implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("categoryId")
    private String categoryId;

    @SerializedName("instructor")
    private String instructor;

    @SerializedName("price")
    private double price;

    @SerializedName("duration")
    private double duration;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("trailerUrl")
    private String trailerUrl;

    @SerializedName("createdAt")
    private String createdAt;

    public Course() {
    }

    public Course(String title, String description, String categoryId, String instructor, double price, double duration, String thumbnail, String trailerUrl, String createdAt) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.instructor = instructor;
        this.price = price;
        this.duration = duration;
        this.thumbnail = thumbnail;
        this.trailerUrl = trailerUrl;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
