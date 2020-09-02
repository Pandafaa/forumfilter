package telran.ashkelon2020.accounting.service.filters;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import telran.ashkelon2020.accounting.dto.exceptions.UnauthorizedException;
import telran.ashkelon2020.accounting.dto.exceptions.UserNotFoundException;
import telran.ashkelon2020.accounting.service.security.AccountSecurity;

@Service
@Order(10)
public class AuthenticationFilter implements Filter {

	@Autowired
	AccountSecurity securityService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String token = request.getHeader("Authorization");
		 String method = request.getMethod();
		if (checkPathAndMethod(path, method)){
			String sessionId = request.getSession().getId();
			if (sessionId != null && token == null) {
				String login = securityService.getUser(sessionId);
				if (login != null) {
					request = new WrapperRequest(request, login);
					chain.doFilter(request, response);
					return;
				}
			}
			try {
				String login = securityService.getLogin(token);
				request = new WrapperRequest(request, login);
				securityService.addUser(sessionId, login);
			} catch (UserNotFoundException e) {
				response.sendError(404, e.getMessage());
				return;
			} catch (UnauthorizedException e) {
				response.sendError(401);
				return;
			} catch (Exception e) {
				response.sendError(400);
				return;
			}
		}

		chain.doFilter(request, response);
	}
	 private boolean checkPathAndMethod(String path, String method) {
	        boolean res = path.startsWith("/account") && !"/account/register".equalsIgnoreCase(path);
	        res = res || ("post".equalsIgnoreCase(method) && path.matches("/forum/post/[^/]+/?")); // add post?
	        res = res || ("delete".equalsIgnoreCase(method) && path.matches("/forum/post/[^/]+/?")); // delete post
	        res = res || ("put".equalsIgnoreCase(method) && path.matches("/forum/post/[^/]+/?")); // edit post
	        res = res || ("put".equalsIgnoreCase(method) && path.matches("/forum/post/[^/]+/?/like/?")); // like post
	        res = res || ("put".equalsIgnoreCase(method) && path.matches("/forum/post/[^/]+/comment/[^/]+/?")); // add comment
	        return res;
	    }



	private class WrapperRequest extends HttpServletRequestWrapper {
		String user;

		public WrapperRequest(HttpServletRequest request, String user) {
			super(request);
			this.user = user;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() {

				@Override
				public String getName() {
					return user;
				}
			};
		}
	}

}
