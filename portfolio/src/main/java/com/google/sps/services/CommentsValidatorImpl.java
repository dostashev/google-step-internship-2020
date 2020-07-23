package com.google.sps.services;

import com.google.sps.data.Comment;

import org.apache.commons.lang3.Range;

public class CommentsValidatorImpl implements CommentsValidator {

    @Override
    public boolean isValid(Comment comment) {
        return Range.between(1, 30).contains(comment.author.length())
            && Range.between(1, 1500).contains(comment.text.length());
    }
    
}