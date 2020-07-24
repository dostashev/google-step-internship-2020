package com.google.sps.services;

import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import com.google.sps.data.Comment;
import com.googlecode.objectify.NotFoundException;

public interface CommentsRepository {
    List<Comment> getAllComments();

    /**
     * @return A key to delete the comment
     */
    String addComment(Comment comment, Optional<String> deleteKey);

    void deleteComment(long id, String deleteKey) throws AuthenticationException, NotFoundException;
}
