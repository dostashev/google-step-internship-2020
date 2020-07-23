package com.google.sps.services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;

public class DatastoreCommentsRepository implements CommentsRepository {

    private static final String ENTITY_NAME = "Comment";

    @Override
    public List<Comment> getAllComments() {
        Query query = new Query(ENTITY_NAME).addSort("timestamp", SortDirection.DESCENDING);

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
    public String addComment(Comment comment, String deleteKey) {

        if (deleteKey == null) {
            deleteKey = generateDeleteKey();
        }

        Entity commentEntity = new Entity(ENTITY_NAME);

        long timestamp = System.currentTimeMillis();

        commentEntity.setProperty("timestamp", timestamp);
        commentEntity.setProperty("author", comment.author);
        commentEntity.setProperty("text", comment.text);
        commentEntity.setProperty("deleteKey", deleteKey);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);

        return deleteKey;
    }

    @Override
    public void deleteComment(long id, String deleteKey) throws AuthenticationException, EntityNotFoundException {
        Key entityKey = KeyFactory.createKey(ENTITY_NAME, id);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        Entity entity = datastore.get(entityKey);

        String entityDeleteKey = (String) entity.getProperty("deleteKey");

        if (entityDeleteKey.equals(deleteKey)) {
            datastore.delete(entityKey);
        } else {
            throw new AuthenticationException("Incorrect deleteKey was provided");
        }

    }
    
    private String generateDeleteKey() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            builder.append((char) (65 + rng.nextInt(26)));
        }

        return builder.toString();
    }

    private SecureRandom rng = new SecureRandom();
}