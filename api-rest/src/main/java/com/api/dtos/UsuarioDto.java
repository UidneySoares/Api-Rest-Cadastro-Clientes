package com.api.dtos;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.api.models.Role;
import com.api.models.Usuario;

import lombok.Data;

@Data
public class UsuarioDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String login;
	private String nome;
	private String senha;
	private List<Role> roles;

	public UsuarioDto(Usuario usuario) {

		this.id = usuario.getId();
		this.login = usuario.getLogin();
		this.nome = usuario.getNome();
		this.senha = usuario.getSenha();
		this.roles = usuario.getRoles();
	}

	public static List<UsuarioDto> converter(List<Usuario> usuarios) {
		return usuarios.stream().map(UsuarioDto::new).collect(Collectors.toList());

	}

}
