package com.api.repositories;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	Usuario findByLogin(String login);
	
	@Query("select u from Usuario u where u.id = ?1")
	Usuario findUserById(Long id);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value="update usuario set token = ?1 where login = ?2")
	void atualizarToken(String token, String login);
	
	@Query("select u from Usuario u where u.nome like %?1%")
	Page<Usuario> buscarPorNome(String nome, Pageable paginacao);
	
	
}
