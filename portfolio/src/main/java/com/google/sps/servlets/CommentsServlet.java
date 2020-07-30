package com.google.sps.servlets;

import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.data.Encrypted;
import com.google.sps.services.CommentsValidator;
import com.google.sps.services.CommentsValidatorImpl;
import com.google.sps.services.ObjectifyCommentsRepository;
import com.google.sps.services.PaginationCommentsRepository;
import com.googlecode.objectify.NotFoundException;

/** Servlet that handles GET/POST requests for comments */
@WebServlet("/comments")
public final class CommentsServlet extends HttpServlet {

  public static final String CONTENT_TYPE = "application/json;";
  public static final int DEFAULT_PAGE_SIZE = 10;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    int page;
    try {
      page = Integer.parseInt(request.getParameter("page"));
    } catch (Exception e) {
      page = 1;
    }

    int pageSize;
    try {
      pageSize = Integer.parseInt(request.getParameter("pageSize"));
    } catch (Exception e) {
      pageSize = DEFAULT_PAGE_SIZE;
    }

    response.setHeader("Link", getPaginationHeader(page, pageSize));

    response.setContentType(CONTENT_TYPE);
    response.getWriter().println(getSerializedComments(page, pageSize));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Reader bodyReader = request.getReader();
    Comment comment = parseComment(bodyReader);

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.setStatus(401);
      return;
    }

    comment.authorEmail = new Encrypted<String>(userService.getCurrentUser().getEmail());

    if (commentsValidator.isValid(comment)) {
      commentsRepository.addComment(comment);

      response.setStatus(201);
    } else {
      response.setStatus(400);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long id = Long.parseLong(request.getParameter("id"));

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.setStatus(401);
      return;
    }

    try {
      commentsRepository.deleteComment(id);

      response.setStatus(204);
    } catch (AuthenticationException e) {
      response.setStatus(403);
    } catch (NotFoundException e) {
      response.setStatus(404);
    }
  }

  private String getPaginationHeader(int page, int pageSize) {
    StringBuilder linkHeader = new StringBuilder();

    linkHeader.append(MessageFormat.format("/comments?page=1&pageSize={0}; rel=\"first\"", pageSize));

    if (page > 1) {
      linkHeader.append(", ");
      linkHeader.append(MessageFormat.format("/comments?page={0}&pageSize={1}; rel=\"prev\"", page - 1, pageSize));
    }

    int commentsCount = commentsRepository.getCommentsCount();

    if (commentsCount > page * pageSize) {
      linkHeader.append(", ");
      linkHeader.append(MessageFormat.format("/comments?page={0}&pageSize={1}; rel=\"next\"", page + 1, pageSize));
    }

    linkHeader.append(", ");
    int lastPageNumber = (commentsCount + pageSize - 1) / pageSize;
    linkHeader.append(MessageFormat.format("/comments?page={0}&pageSize={1}; rel=\"last\"", lastPageNumber, pageSize));

    return linkHeader.toString();
  }

  private String getSerializedComments(int page, int pageSize) {
    List<Comment> comments = commentsRepository.getCommentsPage(page, pageSize);
    return gson.toJson(comments);
  }

  private Comment parseComment(Reader jsonReader) {
    return gson.fromJson(jsonReader, Comment.class);
  }

  private PaginationCommentsRepository commentsRepository = new ObjectifyCommentsRepository();
  private CommentsValidator commentsValidator = new CommentsValidatorImpl();

  private Gson gson = new Gson();
}
