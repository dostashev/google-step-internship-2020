package com.google.sps.services;

import com.google.sps.data.Comment;

public class CommentsValidatorImpl implements CommentsValidator {

    @Override
    public boolean isValid(Comment comment) {
        return comment.author.length() <= 30 && comment.text.length() <= 1500;
    }
    
}