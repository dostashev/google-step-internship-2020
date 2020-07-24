package com.google.sps.services;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import com.google.sps.data.Comment;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class ObjectifyCommentsRepository implements PaginationCommentsRepository {

    static {
        ObjectifyService.register(Comment.class);
    }

    @Override
	public int getCommentsCount() {
        return ofy().load().type(Comment.class).count();
	}

	@Override
	public List<Comment> getCommentsPage(int page, int pageSize) {
        // TODO: Use Cursor instead of offset to improve performance
        return ofy().load()
            .type(Comment.class)
            .order("-timestamp")
            .offset((page - 1) * pageSize)
            .limit(pageSize)
            .list();
	}

    @Override
    public String addComment(Comment comment, Optional<String> deleteKey) {

        comment.deleteKey = deleteKey.orElseGet(this::generateDeleteKey);

        ofy().save().entity(comment).now();

        return comment.deleteKey;
    }

    @Override
    public void deleteComment(long id, String deleteKey) throws AuthenticationException, NotFoundException {

        Comment comment = ofy().load().type(Comment.class).id(id).safe();

        if (comment.deleteKey.equals(deleteKey)) {
            ofy().delete().entity(comment).now();
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
