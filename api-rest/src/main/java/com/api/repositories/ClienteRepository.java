package com.api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.models.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{
	
	@Query("select u from Cliente u where u.nome like %?1%")
	Page<Cliente> buscarPorNome(String nome, Pageable paginacao);
	
	@Query("select u from Cliente u where u.id = ?1")
	Cliente findUserById(Long id);
	
}
