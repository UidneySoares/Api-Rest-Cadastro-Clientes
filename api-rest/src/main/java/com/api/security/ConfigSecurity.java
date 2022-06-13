package com.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.api.services.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	
	/*Configura as solicitações de acesso Http*/
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/*Proteção contra quem não está validado por Token*/
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		/*Restrição a URL - Permissão para acesso a API*/
		.disable().authorizeRequests()
		.antMatchers("/").permitAll()
		
		/*Acesso Clientes*/
		
		.antMatchers(HttpMethod.GET, "/cliente/**").hasAnyRole("ADMINISTRADOR", "OPERADOR")
		.antMatchers(HttpMethod.POST, "/cliente/**").hasRole("ADMINISTRADOR")
		.antMatchers(HttpMethod.PUT, "/cliente/**").hasRole("ADMINISTRADOR")
		.antMatchers(HttpMethod.DELETE, "/cliente/**").hasRole("ADMINISTRADOR")
		
		/*Acesso usuários*/
		
		.antMatchers(HttpMethod.POST, "/login").permitAll()
		.antMatchers(HttpMethod.GET, "/usuario/").hasAnyRole("ADMINISTRADOR")
		.antMatchers(HttpMethod.POST, "/usuario/*").hasRole("ADMINISTRADOR")
		.antMatchers(HttpMethod.PUT, "/usuario/*").hasRole("ADMINISTRADOR")
		.antMatchers(HttpMethod.DELETE, "/usuario/*").hasRole("ADMINISTRADOR")
		
		
		/*Redireciona após logout*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/home")
		
		/*Mapeia Url logout*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/*Filtrar requisições de login para autenticar*/
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
				UsernamePasswordAuthenticationFilter.class)

		/*Filtra requisições para verificar o Token presente no Header*/
		.addFilterBefore(new JWTApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		/*Service  que consultará o usuário no BD*/
		auth.userDetailsService(implementacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
	}
	
	/*Configurar recursos estáticos da web(HTML, Img, CSS, JS) */
	@Override
	public void configure(WebSecurity web) throws Exception {	
	}
	
}