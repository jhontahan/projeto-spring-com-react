package com.jhonathan;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jhonathan.service.JwtService;
import com.jhonathan.service.impl.SecurityUserDetailsService;

public class JwtTokenFilter extends OncePerRequestFilter{
	
	private JwtService jwtService;
	private SecurityUserDetailsService userDetailsService;

	public JwtTokenFilter(JwtService jwtService, SecurityUserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String autorization = request.getHeader("Authorization");
		
		
		
		if (autorization != null && autorization.startsWith("Bearer")) {
			String token = autorization.split(" ")[1];
			boolean isTokenValid = jwtService.isTokenValido(token);
			
			if(isTokenValid) {
				String login = jwtService.obterLoginUsuario(token);
				UserDetails usuario = userDetailsService.loadUserByUsername(login);
				
				UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
				
				user.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(user);
				
				
			}
		}

		filterChain.doFilter(request, response);
		
	}

}
