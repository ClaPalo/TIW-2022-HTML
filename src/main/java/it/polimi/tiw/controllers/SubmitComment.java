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
import it.polimi.tiw.dao.CommentDAO;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/SubmitComment")
public class SubmitComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitComment() {
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
		
		String text = null;
		Integer imageID = null;
		Integer albumID = null;
		User user = (User) request.getSession().getAttribute("user");
		text = request.getParameter("text");
		
		
		if (user == null || text == null || text.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing information for posting a comment."); //TODO Manda solo un messaggino a schermo
			return;
		}
		
		try {
			imageID = Integer.parseInt(request.getParameter("imageId"));
			albumID = Integer.parseInt(request.getParameter("albumId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing information for posting a comment.");
			return;
		}
		
		//TODO Controlla che imageID esista nel DB
		//TODO Text non troppo lungo
		
		
		CommentDAO commentDAO = new CommentDAO(connection);
		
		try {
			commentDAO.addComment(user.getId(), text, imageID);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/AlbumInfo?id=" + albumID.toString() + "&imgId=" + imageID.toString());
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
