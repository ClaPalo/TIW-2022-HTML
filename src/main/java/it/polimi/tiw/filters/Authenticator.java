package it.polimi.tiw.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Authenticator implements Filter {
	
	public Authenticator() {
		// TODO Auto-generated constructor stub
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String login_path = req.getServletContext().getContextPath() + "/index.html";
		
		HttpSession ses = req.getSession();
		
		if (ses.isNew() || ses.getAttribute("user") == null) {
			res.sendRedirect(login_path);
			return;
		}
		
		chain.doFilter(request, response);
		
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
}