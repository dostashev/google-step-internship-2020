package com.google.sps.services;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;

public class DatastoreCommentsRepository implements CommentsRepository {

    @Override
    public List<Comment> getAllComments() {
        Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        List<Comment> comments = new ArrayList<>();
        for (Entity entity : datastore.prepare(query).asIterable()) {
            long id = entity.getKey().getId();
            long timestamp = (long) entity.getProperty("timestamp");
            String author = (String) entity.getProperty("author");
            String text = (String) entity.getProperty("text");

            comments.add(new Comment(id, timestamp, author, text));
        }

        return comments;
    }

    @Override
    public void addComment(Comment comment) {
        Entity commentEntity = new Entity("Comment");

        long timestamp = System.currentTimeMillis();

        commentEntity.setProperty("timestamp", timestamp);
        commentEntity.setProperty("author", comment.author);
        commentEntity.setProperty("text", comment.text);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
    }
    
}