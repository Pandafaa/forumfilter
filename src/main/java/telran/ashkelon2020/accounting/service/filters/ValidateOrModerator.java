package telran.ashkelon2020.accounting.service.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.ashkelon2020.accounting.service.security.AccountSecurity;
import telran.ashkelon2020.forum.service.ForumService;


@Service
@Order(50)
public class ValidateOrModerator implements Filter {

	@Autowired
	ForumService forumService;
	@Autowired
	AccountSecurity securityService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
	
		String method = request.getMethod();
		if (checkPathAndMethod(path, method)) {
			String user = request.getUserPrincipal().getName();
			if (!userIsValid(path, user)) {
				response.sendError(403);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkPathAndMethod(String path, String method) {
		boolean res = path.matches("/forum/post/\\w+/?")
				&& ("Put".equalsIgnoreCase(method) || "Delete".equalsIgnoreCase(method));

		return res;
	}

	private boolean userIsValid(String path, String user) {

		String author = forumService.getPost(path.split("/")[3]).getAuthor();
		boolean res = author.equals(user) || securityService.checkHaveRole(user, "moderator");
		return res;
	}
}
