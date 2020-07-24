package com.google.sps.services;

import java.util.List;

import com.google.sps.data.Comment;

public interface PaginationCommentsRepository extends CommentsRepository {

    int getCommentsCount();

    List<Comment> getCommentsPage(int page, int pageSize);

    @Override
    default List<Comment> getAllComments() {
        return getCommentsPage(1, Integer.MAX_VALUE);
    }
}
