package com.api.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.api.models.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

/*Gerenciador de Token*/
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter{

	/*Configura o gerenciador de autenticação*/
	protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
		
		/*Obriga a autenticar a URL*/
		super(new AntPathRequestMatcher(url));
		
		setAuthenticationManager(authenticationManager);
	}
	
	/*Retorna o usuário ao processar a autenticação*/
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		/*Pega o token para validar*/
		Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
		
		/*Retorna o usuario login, senha e acessos*/
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha(), user.getAuthorities()));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		new JWTTokenAutenticationService().addAuthentication(response, authResult.getName());
	}

}

