package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Album;
import it.polimi.tiw.dao.AlbumDAO;

import java.io.IOException;

import java.sql.Connection;
import it.polimi.tiw.utils.ConnectionHandler;
import java.sql.SQLException;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.context.WebContext;


@WebServlet("/Home")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
	
	public GoToHome() {
		super();
	}
	
	public void init() throws ServletException {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
		templateResolver.setTemplateMode(TemplateMode.HTML);
		
		this.connection = ConnectionHandler.getConnection(getServletContext());
		
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
		
		ServletContext context = getServletContext();
		HttpSession session = request.getSession();
		
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(context.getContextPath());
			return;
		}
		
		User user = (User) session.getAttribute("user");
		
		AlbumDAO albumDAO = new AlbumDAO(this.connection);
		List<Album> albums = null;
		
		try {
			albums = albumDAO.getAlbumsByUser(user.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		String home_source_path = "/WEB-INF/homepage.html";
		
		WebContext wctx = new WebContext(request, response, context, request.getLocale());
		wctx.setVariable("user", user);
		wctx.setVariable("user_albums", albums);
		this.templateEngine.process(home_source_path, wctx, response.getWriter());
		
	}
	
}