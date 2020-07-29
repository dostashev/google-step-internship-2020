package com.google.sps.services;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
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
    public void addComment(Comment comment) {

        try {
            comment.sentimentScore = getSentimentScore(comment.text);
        } catch (IOException e) {
            comment.sentimentScore = 0;
        }

        ofy().save().entity(comment).now();
    }

    @Override
    public void deleteComment(long id) throws AuthenticationException, NotFoundException {

        Comment comment = ofy().load().type(Comment.class).id(id).safe();

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if (comment.authorEmail.matches(user.getEmail()) || userService.isUserAdmin()) {
            ofy().delete().entity(comment).now();
        } else {
            throw new AuthenticationException("This user doesn't have rights to delete this comment");
        }
    }

    private float getSentimentScore(String text) throws IOException {
        LanguageServiceClient languageService = LanguageServiceClient.create();
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        languageService.close();
        return sentiment.getScore();
    }
}
