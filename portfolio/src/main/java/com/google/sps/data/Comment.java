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
    public transient String deleteKey;

    public Comment(String author, String text, String deleteKey) {
        this.timestamp = System.currentTimeMillis();
        this.author = author;
        this.text = text;
        this.deleteKey = deleteKey;
    }

    public Comment() {}
}
