package com.api.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.dtos.UsuarioDto;
import com.api.models.Usuario;
import com.api.repositories.UsuarioRepository;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@GetMapping("/")
	@Cacheable("getAllUsuario")
	public List<UsuarioDto> listarTodos(Usuario usuario) {
		List<Usuario> usuarios = usuarioRepository.findAll();
		return UsuarioDto.converter(usuarios);
	}
	
	@CacheEvict(value = "getAllUsuario", allEntries = true)
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario){
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		
		usuarioRepository.save(usuario);
		return ResponseEntity.ok().build();
	}
	
	@CacheEvict(value = "getAllUsuario", allEntries = true)
	@PutMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid Usuario usuario)throws Exception{
		return usuarioRepository.findById(id).map(usuarioAtual -> {
			usuario.setId(usuarioAtual.getId());
			
			Usuario record = usuarioRepository.findUserById(usuario.getId());
			if (!record.getSenha().equals(usuario.getSenha())) {
				String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
				usuario.setSenha(senhaCriptografada);
			}
			
			usuarioRepository.save(usuario);
			return ResponseEntity.ok().build();
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@CacheEvict(value = "getAllUsuario", allEntries = true)
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity<?> deletar(@PathVariable(value = "id") Long id){
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		if (usuario.isPresent()) {
			usuarioRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
		
	}
}
