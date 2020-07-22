package com.google.sps.data;

public final class Comment {
    public long id;
    public long timestamp;
    public String author;
    public String text;

    public Comment(long id, long timestamp, String author, String text) {
        this.id = id;
        this.timestamp = timestamp;
        this.author = author;
        this.text = text;
	}
}