package com.hospital.model;

import java.sql.Timestamp;

public class Article {
    private int id;
    private String title;
    private String summary;
    private String content;
    private String image;
    private String category;
    private int views;
    private Timestamp createdAt;

    public Article() {
    }

    public Article(int id, String title, String summary, String content, String image, String category, int views, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.image = image;
        this.category = category;
        this.views = views;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}