package com.artsovalov.windenergyrss.model;

import java.io.Serializable;

public class RssFeedModel implements Serializable{

    private String title;
    private String link;
    private String description;
    private String date;

    public RssFeedModel(String title, String link, String description, String date) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {

        return "title: " + title + "\n"
                + "link: " + link + "\n"
                + "description: " + description + "\n"
                + "date: " + date + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

