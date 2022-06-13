package com.api.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.dtos.ClienteDto;
import com.api.models.Cliente;
import com.api.repositories.ClienteRepository;
import com.google.gson.Gson;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<ClienteDto> listarPorId(@PathVariable(value = "id") Long id) {
		Optional<Cliente> cliente = clienteRepository.findById(id);
		if (cliente.isPresent()) {
			return new ResponseEntity<ClienteDto>(new ClienteDto(cliente.get()), HttpStatus.OK);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping(value = "/")
	@Cacheable("getAllCliente")
	public Page<ClienteDto> listarTodos(@RequestParam(required = false) String nome, 
		@PageableDefault(sort = "id", direction = Direction.ASC, page = 0, size = 10) Pageable paginacao) {

		if (nome == null) {
			Page<Cliente> clientes = clienteRepository.findAll(paginacao);
			return ClienteDto.converter(clientes);
		} else {
			Page<Cliente> clientes = clienteRepository.buscarPorNome(nome, paginacao);
			return ClienteDto.converter(clientes);
		}
	}

	@PostMapping(value = "/", produces = "application/json")
	@CacheEvict(value = "getAllCliente", allEntries = true)
	public ResponseEntity<Cliente> cadastrar(@RequestBody @Valid Cliente cliente) throws Exception {

		for (int pos = 0; pos < cliente.getTelefones().size(); pos++) {
			cliente.getTelefones().get(pos).setCliente(cliente);
		}

		/* Consumindo API Via CEP */

		URL url = new URL("https://viacep.com.br/ws/" + cliente.getCep() + "/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		while ((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}

		Cliente aux = new Gson().fromJson(jsonCep.toString(), Cliente.class);
		cliente.setCep(aux.getCep());
		cliente.setLogradouro(aux.getLogradouro());
		cliente.setBairro(aux.getBairro());
		cliente.setLocalidade(aux.getLocalidade());
		cliente.setUf(aux.getUf());
		clienteRepository.save(cliente);
		return ResponseEntity.ok().build();
	}

	@PutMapping(value = "/{id}", produces = "application/json")
	@CacheEvict(value = "getAllCliente", allEntries = true)
	public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid Cliente cliente) throws Exception {

		return clienteRepository.findById(id).map(clienteAtual -> {
			cliente.setId(clienteAtual.getId());
			
			for (int pos = 0; pos < cliente.getTelefones().size(); pos++) {
				cliente.getTelefones().get(pos).setCliente(cliente);
			}

			try {
				
				/* Consumindo API Via CEP */
				URL url = new URL("https://viacep.com.br/ws/" + cliente.getCep() + "/json/");
				URLConnection connection = url.openConnection();
				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String cep = "";
				StringBuilder jsonCep = new StringBuilder();
				while ((cep = br.readLine()) != null) {
					jsonCep.append(cep);
				}

				Cliente aux = new Gson().fromJson(jsonCep.toString(), Cliente.class);
				cliente.setCep(aux.getCep());
				cliente.setLogradouro(aux.getLogradouro());
				cliente.setBairro(aux.getBairro());
				cliente.setLocalidade(aux.getLocalidade());
				cliente.setUf(aux.getUf());
				
			}catch (Exception e) {}
			clienteRepository.save(cliente);
			return ResponseEntity.ok().build();
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping(value = "/{id}", produces = "application/json")
	@CacheEvict(value = "getAllCliente", allEntries = true)
	public ResponseEntity<?> deletar(@PathVariable(value = "id") Long id) {

		Optional<Cliente> cliente = clienteRepository.findById(id);
		if (cliente.isPresent()) {
			clienteRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();

	}
}
