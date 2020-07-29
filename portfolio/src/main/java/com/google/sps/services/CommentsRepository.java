package com.google.sps.services;

import java.util.List;

import javax.naming.AuthenticationException;

import com.google.sps.data.Comment;
import com.googlecode.objectify.NotFoundException;

public interface CommentsRepository {
    List<Comment> getAllComments();

    void addComment(Comment comment);

    void deleteComment(long id) throws AuthenticationException, NotFoundException;
}
