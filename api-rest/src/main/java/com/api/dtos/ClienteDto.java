package com.api.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.api.models.Cliente;
import com.api.models.Telefone;

import lombok.Data;

@Data
public class ClienteDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String nome;
	
	private String cpf;
	
	private String cep;

	private String logradouro;

	private String bairro;

	private String localidade;

	private String uf;
	
	private List<Telefone> telefones = new ArrayList<Telefone>();

	public ClienteDto(Cliente cliente) {

		this.id = cliente.getId();
		this.nome = cliente.getNome();
		this.cpf = cliente.getCpf();
		this.cep = cliente.getCep();
		this.logradouro = cliente.getLogradouro();
		this.bairro = cliente.getBairro();
		this.localidade = cliente.getLocalidade();
		this.uf = cliente.getUf();
		this.telefones = cliente.getTelefones();

	}

	public static Page<ClienteDto> converter(Page<Cliente> clientes) {
		
		return clientes.map(ClienteDto::new);
	}
}
