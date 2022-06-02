package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class AlbumManager
 */
@WebServlet("/AlbumManager")
public class AlbumManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AlbumManager() {
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
	 * Create a new empty album
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String albumName = null;
		User user = (User) request.getSession().getAttribute("user");
		
		albumName = request.getParameter("albumName");
		if (albumName == null || albumName.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credentials");
			return;
		}
		
		AlbumDAO albumDAO = new AlbumDAO(this.connection);
		
		try {
			albumDAO.createEmptyAlbum(albumName, user.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/Home");
	}

	/**
	 * Add images to an album
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] imageIDs = null;
		int albumId;
		AlbumDAO albumDAO = new AlbumDAO(this.connection);
		User user = (User) request.getSession().getAttribute("user");
		List<Integer> imagesAllowed = new ArrayList<>();
		
		try {
			albumId = Integer.parseInt(request.getParameter("albumId"));
			//Controllo che l'album appartenga all'utente
			if (albumDAO.getIdOwnerOfAlbum(albumId) != user.getId()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You can't edit an album of someone else");
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing album ID");
			return;
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		imageIDs = request.getParameterValues("imageId");
		int imageId;
		ImageDAO imageDAO = new ImageDAO(this.connection);
		
		//Ottengo la lista di tutte le immagini che questo utente può aggiungere al suo album
		try {
			imagesAllowed = imageDAO.getImagesIDByUserNotInAlbum(user.getId(), albumId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		for (String imageIdString : imageIDs) {
			try {
				imageId = Integer.parseInt(imageIdString);
				
				//Controllo che l'immagine sia tra quelle autorizzate e nel caso la aggiungo all'album
				if (imagesAllowed.remove((Object) imageId))
					imageDAO.addImageToAlbumById(albumId, imageId);
			} catch (NumberFormatException | NullPointerException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sorry, something went wrong");
				return;
			} catch (SQLException s) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/AlbumInfo?id=" + albumId + "&page=0");
		
		
	}

}
