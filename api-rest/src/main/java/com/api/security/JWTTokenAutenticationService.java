package com.api.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.api.config.ApplicationContextLoad;
import com.api.models.Usuario;
import com.api.repositories.UsuarioRepository;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticationService {

	/* Validade do Token */
	private static final long EXPIRATION_TIME = 86400000;

	private static final String SECRET = "SenhaSecreta";

	/* Tipo padrão do Token */
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";

	/* Gerando token e adicionando ao Header */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		Date hoje = new Date();
		
		String JWT = Jwts.builder()
				.setIssuer("API Cadastro")
				.setSubject(username)
				.setIssuedAt(hoje)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		String token = TOKEN_PREFIX + " " + JWT;

		/* Adiciona no Header */
		response.addHeader(HEADER_STRING, token);
		
		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).atualizarToken(JWT, username);
		
		/*Liberando resposta para portas diferentes que consumirem a API*/
		liberarCors(response);
		
		/* Escreve o Token na resposta */
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}

	/* Usuário validado com Token ou retorna Unauthorized */
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		String token = request.getHeader(HEADER_STRING);
		
		try {
		if (token != null) {
		
			String apenasToken = token.replace(TOKEN_PREFIX, "").trim();
			String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(apenasToken)
					.getBody().getSubject();
			if (user != null) {
				/* Faz um Load trazendo tudo que foi carregado no Context */
				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
						.findByLogin(user);

				if (usuario != null) {
					if(apenasToken.equalsIgnoreCase(usuario.getToken())) {
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(), 
							usuario.getSenha() , 
							usuario.getAuthorities());
				}
			  }
			}

		}
		} catch (ExpiredJwtException e) {
			try {
				response.getOutputStream().print("Seu Token Expirou.");
			} catch (IOException e1) {}
		}
		liberarCors(response);
		return null;

	}

	private void liberarCors(HttpServletResponse response) {
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Headers") == null){
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods","*");
		}
	}
		
}