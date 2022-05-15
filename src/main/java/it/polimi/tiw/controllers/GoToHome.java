package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;

import java.io.IOException;

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
	
	public GoToHome() {
		super();
	}
	
	public void init() {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
		templateResolver.setTemplateMode(TemplateMode.HTML);
		
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
		
		String home_source_path = "/WEB-INF/homepage.html";
		
		WebContext wctx = new WebContext(request, response, context, request.getLocale());
		wctx.setVariable("user", user);
		this.templateEngine.process(home_source_path, wctx, response.getWriter());
		
	}
	
}