package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToEditAlbum
 */
@WebServlet("/EditAlbum")
public class GoToEditAlbum extends HttpServlet {
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
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToEditAlbum() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext context = getServletContext();

		User user = (User) request.getSession().getAttribute("user");
		int albumId;
		List<Image> images = null;
		ImageDAO imageDAO = new ImageDAO(this.connection);
		
		try {
			albumId = Integer.parseInt(request.getParameter("albumId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing album parameters");
			return;
		}
		
		AlbumDAO albumDAO = new AlbumDAO(this.connection);
		Album album;
		
		try {
			album = albumDAO.getAlbumById(albumId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		//TODO Controlla che l'album appartenga all'utente
		
		
		
		try {
			images = imageDAO.getImagesByUserNotInAlbum(user.getId(), albumId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		WebContext wctx = new WebContext(request, response, context, request.getLocale());
		wctx.setVariable("images", images);
		wctx.setVariable("album", album);
		
		String source_path = "/WEB-INF/editalbum.html";
		this.templateEngine.process(source_path, wctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
