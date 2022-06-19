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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/SubmitComment")
public class SubmitComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitComment() {
        super();
    }
    
    public void init() throws ServletException {
		
    	ServletContext servletContext = getServletContext();
    	
    	this.connection = ConnectionHandler.getConnection(servletContext);
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
		Integer page = null;
		User user = (User) request.getSession().getAttribute("user");
		text = request.getParameter("text");
		
		
		if (user == null || text == null || text.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user or text.");
			return;
		}
		
		if (text.length() > 180) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Text is too long.");
			return;
		}
		
		try {
			imageID = Integer.parseInt(request.getParameter("imageId"));
			albumID = Integer.parseInt(request.getParameter("albumId"));
			page = Integer.parseInt(request.getParameter("page"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing information for posting a comment.");
			return;
		}
		
		ImageDAO imageDAO = new ImageDAO(this.connection);
		Image img = null;
		CommentDAO commentDAO = new CommentDAO(connection);
		
		try {
			
			img = imageDAO.getImageById(imageID);
			
			if (img == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "The image you want to post a comment on does not exist.");
				return;
			}
			
			commentDAO.addComment(user.getId(), text, imageID);
			
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/AlbumInfo?id=" + albumID.toString() + "&page=" + page.toString() + "&imgId=" + imageID.toString());
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(this.connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
