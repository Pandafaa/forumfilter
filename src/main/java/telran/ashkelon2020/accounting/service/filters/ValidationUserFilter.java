package telran.ashkelon2020.accounting.service.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(30)
public class ValidationUserFilter implements Filter {

	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
	
		String method = request.getMethod();
		if (checkPathAndMethod(path,method)) {
			String user = request.getUserPrincipal().getName();
			//		String login = path.split("/")[3];
			if (!userIsValid(path,user)) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}
	
	private boolean userIsValid(String path, String user) {
		boolean res =path.matches("/account/user/"+user)||path.matches("/forum/post/"+user)||
				path.matches("/forum/post/\\w+/comment/"+user);
		return res;
	}

	private boolean checkPathAndMethod(String path,String method) {
		boolean res = path.matches("/account/user/\\w+/?")
				||path.matches("/forum/post/\\w+/comment/\\w+/?")&&"Put".equalsIgnoreCase(method)
				||path.matches("/forum/post/\\w+/?")&&"Post".equalsIgnoreCase(method);

		return res;
	}

}
