package com.google.sps.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public final class Comment {
    @Id public Long id;
    @Index public long timestamp;
    public String author;
    public String text;
    public float sentimentScore;
    public transient String authorEmail;

    public Comment(String author, String text, String authorEmail) {
        this.timestamp = System.currentTimeMillis();
        this.author = author;
        this.text = text;
        this.authorEmail = authorEmail;
    }

    public Comment() {
        this.timestamp = System.currentTimeMillis();
    }
}
