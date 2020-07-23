package com.google.sps.servlets;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import javax.naming.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.services.CommentsRepository;
import com.google.sps.services.CommentsValidator;
import com.google.sps.services.CommentsValidatorImpl;
import com.google.sps.services.DatastoreCommentsRepository;

import org.apache.commons.text.StringEscapeUtils;

/** Servlet that handles GET/POST requests for comments */
@WebServlet("/comments")
public final class CommentsServlet extends HttpServlet {

  public static final String CONTENT_TYPE = "application/json;";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(CONTENT_TYPE);
    response.getWriter().println(getSerializedComments());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Reader bodyReader = request.getReader();
    Comment comment = parseComment(bodyReader);

    if (commentsValidator.isValid(comment)) {
      String deleteKey = commentsRepository.addComment(comment, null);

      response.getWriter().write(deleteKey);
    } else {
      response.setStatus(400);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id = Long.parseLong(request.getParameter("id"));
    String deleteKey = request.getParameter("deleteKey");

    try {
      commentsRepository.deleteComment(id, deleteKey);
      response.setStatus(204);
    } catch (AuthenticationException e) {
      response.setStatus(401);
    } catch (EntityNotFoundException e) {
      response.setStatus(404);
    }
  }

  private String getSerializedComments() {
    List<Comment> comments = commentsRepository.getAllComments();

    comments.forEach((comment) -> { 
      comment.author = StringEscapeUtils.escapeHtml4(comment.author);
      comment.text = StringEscapeUtils.escapeHtml4(comment.text);
    });

    return gson.toJson(comments);
  }

  private Comment parseComment(Reader jsonReader) {
    return gson.fromJson(jsonReader, Comment.class);
  }

  private CommentsRepository commentsRepository = new DatastoreCommentsRepository();
  private CommentsValidator commentsValidator = new CommentsValidatorImpl();

  private Gson gson = new Gson();
}