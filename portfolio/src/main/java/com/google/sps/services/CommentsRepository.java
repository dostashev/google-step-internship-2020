package com.google.sps.services;

import java.util.List;

import com.google.sps.data.Comment;

public interface CommentsRepository {
    List<Comment> getAllComments();
    void addComment(Comment comment);
}
