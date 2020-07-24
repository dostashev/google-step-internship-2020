package com.google.sps.services;

import com.google.sps.data.Comment;

public interface CommentsValidator {
    boolean isValid(Comment comment);
}