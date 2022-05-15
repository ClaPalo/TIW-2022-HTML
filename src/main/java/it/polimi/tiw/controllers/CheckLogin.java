package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import it.polimi.tiw.utils.ConnectionHandler;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLogin() {
        super();
    }
    
    public void init() throws ServletException {
		
    	ServletContext servletContext = getServletContext();
    	
    	this.connection = ConnectionHandler.getConnection(servletContext);
    	
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String password = null;
		
		username = request.getParameter("nickname");
		password = request.getParameter("password");
		
		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credentials");
			return;
		}
		
		UserDAO userDao = new UserDAO(connection);
		User user = null;
		
		try {
			user = userDao.checkCredentials(username, password);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		String path;
		if (user == null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("ErrorMsg", "Nome utente o password errati");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			path = getServletContext().getContextPath() + "/Home";
			
			request.getSession().setAttribute("user", user);
			response.sendRedirect(path);
		}
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
