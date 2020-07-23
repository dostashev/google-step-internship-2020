package com.google.sps.services;

import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.sps.data.Comment;

public interface CommentsRepository {
    List<Comment> getAllComments();

    /**
     * @return A key to delete the comment
     */
    String addComment(Comment comment, Optional<String> deleteKey);

    void deleteComment(long id, String deleteKey) throws AuthenticationException, EntityNotFoundException;
}
