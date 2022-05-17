package it.polimi.tiw.controllers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import it.polimi.tiw.utils.ConnectionHandler;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.beans.Album;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.beans.Comment;

import java.util.List;

@WebServlet("/AlbumInfo")
public class GetAlbumInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public void init() throws ServletException {
		this.connection = ConnectionHandler.getConnection(getServletContext());
		
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
		templateResolver.setTemplateMode(TemplateMode.HTML);
		
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		ServletContext context = getServletContext();
		
		AlbumDAO albumDAO = new AlbumDAO(this.connection);
		Album album = null;
		ImageDAO imageDAO = new ImageDAO(this.connection);
		Image mainImage = null;
		List<Image> images = null;
		CommentDAO commentDAO = new CommentDAO(this.connection);
		List<Comment> comments = null;
		
		Integer albumId = null, imageId = null, page = null;
		
		try {
			albumId = Integer.parseInt(request.getParameter("id"));
			page = Integer.parseInt(request.getParameter("page"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing album and/or page parameters.");
			return;
		}
		
		try {
			album = albumDAO.getAlbumById(albumId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		if (album == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested album does not exist.");
			return;
		}
		
		try {
			String imgId_str = request.getParameter("imgId");
			if (imgId_str != null) 
				imageId = Integer.parseInt(request.getParameter("imgId"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		if (imageId != null) {
			
			boolean isInAlbum = false;
			
			try {
				mainImage = imageDAO.getImageById(imageId);
				comments = commentDAO.getCommentsByImage(imageId);
				isInAlbum = imageDAO.imageIsInAlbum(imageId, albumId);
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			if (mainImage == null || !isInAlbum) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested image either does not exist or does not belong to this album.");
				return;
			}
		}
		
		try {
			images = imageDAO.getImagesByAlbum(albumId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not load images for this album.");
			return;
		}
		
		if (images.size() / 5 < page) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Album page does not exist.");
		}
		
		WebContext wctx = new WebContext(request, response, context, request.getLocale());
		wctx.setVariable("album", album);
		wctx.setVariable("images", images);
		wctx.setVariable("mainImage", mainImage);
		wctx.setVariable("comments", comments);
		wctx.setVariable("page", page);
		
		String source_path = "/WEB-INF/albumpage.html";
		this.templateEngine.process(source_path, wctx, response.getWriter());
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}