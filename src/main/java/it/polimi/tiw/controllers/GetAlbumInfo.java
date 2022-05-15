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
		
		int albumId = Integer.parseInt(request.getParameter("id"));
		
		try {
			album = albumDAO.getAlbumById(albumId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		if (album != null) {
			WebContext wctx = new WebContext(request, response, context, request.getLocale());
			wctx.setVariable("album", album);
		
			String source_path = "/WEB-INF/albumpage.html";
			this.templateEngine.process(source_path, wctx, response.getWriter());
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested album does not exist.");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}