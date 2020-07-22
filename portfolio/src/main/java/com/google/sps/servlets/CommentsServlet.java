package com.google.sps.servlets;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.services.CommentsService;
import com.google.sps.services.DatastoreCommentsService;

/** Servlet that handles GET/POST requests for comments */
@WebServlet("/comments")
public final class CommentsServlet extends HttpServlet {

  public static final String CONTENT_TYPE = "application/json;";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(CONTENT_TYPE);
    response.getWriter()
      .println(getSerializedComments());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    Reader bodyReader = request.getReader();
    Comment comment = parseComment(bodyReader);

    commentsService.addComment(comment);
  }

  private String getSerializedComments() {
    return gson.toJson(
      commentsService.getAllComments()
    );
  }

  private Comment parseComment(Reader jsonReader) {
    return gson.fromJson(jsonReader, Comment.class);
  }

  private CommentsService commentsService = new DatastoreCommentsService();

  private Gson gson = new Gson();
}