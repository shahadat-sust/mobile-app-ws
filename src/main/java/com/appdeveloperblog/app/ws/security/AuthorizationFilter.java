package com.appdeveloperblog.app.ws.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {

	public AuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
		if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			SecurityContextHolder.getContext().setAuthentication(null);
			chain.doFilter(request, response);
			return;
		}
		
		String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");
		UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
		String username = Jwts.parser()
			.setSigningKey(SecurityConstants.getTokenSecret())
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
		
		if (username != null) {
			return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
		} else {
			return null;
		}
	}
	
	

}
