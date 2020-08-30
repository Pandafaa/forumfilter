package telran.ashkelon2020.accounting.service.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

@Service
public class AuthenticationFilter implements Filter{

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
			//TODO
		}
		
		chain.doFilter(request, response);
	}

}
