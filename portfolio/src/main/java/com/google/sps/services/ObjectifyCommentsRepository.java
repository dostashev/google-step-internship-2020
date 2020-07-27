package com.google.sps.services;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

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
    public String addComment(Comment comment, Optional<String> deleteKey) {

        comment.deleteKey = deleteKey.orElseGet(this::generateDeleteKey);

        try {
            comment.sentimentScore = getSentimentScore(comment.text);
        } catch (IOException e) {
            comment.sentimentScore = 0;
        }

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

    private float getSentimentScore(String text) throws IOException {
        LanguageServiceClient languageService = LanguageServiceClient.create();
        Document doc = Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        languageService.close();
        return sentiment.getScore();
    }

    private SecureRandom rng = new SecureRandom();
}
