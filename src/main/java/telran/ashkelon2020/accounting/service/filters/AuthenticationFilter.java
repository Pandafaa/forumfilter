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
import org.springframework.stereotype.Service;

import telran.ashkelon2020.accounting.dto.exceptions.UnauthorizedException;
import telran.ashkelon2020.accounting.dto.exceptions.UserNotFoundException;
import telran.ashkelon2020.accounting.service.security.AccountSecurity;

@Service
public class AuthenticationFilter implements Filter{
	
	@Autowired
	AccountSecurity securityService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		String token = request.getHeader("Authorization");
		System.out.println(path);
		System.out.println(method);
		System.out.println(token);
		if (!"/account/register".equalsIgnoreCase(path)) {
			try {
				String login = securityService.getLogin(token);
			} catch (UserNotFoundException e) {
				response.sendError(404, e.getMessage());
				return;
			} catch (UnauthorizedException e) {
				response.sendError(401);
				return;
			}catch (Exception e) {
				response.sendError(400);
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

}
