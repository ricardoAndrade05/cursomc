package com.nelioalves.coursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nelioalves.coursomc.domain.Cidade;
import com.nelioalves.coursomc.domain.Cliente;
import com.nelioalves.coursomc.domain.Endereco;
import com.nelioalves.coursomc.domain.enums.TipoCliente;
import com.nelioalves.coursomc.dto.ClienteDTO;
import com.nelioalves.coursomc.dto.ClienteNewDTO;
import com.nelioalves.coursomc.repositories.ClienteRepository;
import com.nelioalves.coursomc.repositories.EnderecoRepository;
import com.nelioalves.coursomc.services.exceptions.DataIntegrityException;
import com.nelioalves.coursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;

	public Cliente find(Integer id) {
		Optional<Cliente> cliente = repo.findById(id);
		return cliente.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente cliente) {
		cliente.setId(null);
		enderecoRepository.saveAll(cliente.getEnderecos());
		return repo.save(cliente);
	}

	public Cliente update(Cliente clienteDadosAtualizados) {
		Cliente clienteBanco = find(clienteDadosAtualizados.getId());
		updateData(clienteBanco, clienteDadosAtualizados);
		return repo.save(clienteBanco);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possivel exlcuir porque há pedidos para esse cliente.");
		}

	}

	public List<Cliente> findAll() {
		return repo.findAll();
	}

	public Page<Cliente> buscaPaginada(Integer page, Integer linesPerPage, String direction, String orderBy) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO clienteDTO) {
		return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO clienteNewDTO) {
		Cliente cliente = new Cliente(null, clienteNewDTO.getNome(), clienteNewDTO.getEmail(),
				clienteNewDTO.getCpfOuCnpj(), TipoCliente.toEnum(clienteNewDTO.getTipo()));
		Cidade cidade = new Cidade(clienteNewDTO.getCidadeId(), null, null);
		Endereco endereco = new Endereco(null, clienteNewDTO.getLogradouro(), clienteNewDTO.getNumero(),
				clienteNewDTO.getComplemento(), clienteNewDTO.getBairro(), clienteNewDTO.getCep(), cidade, cliente);
		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(clienteNewDTO.getTelefone1());
		if (clienteNewDTO.getTelefone2() !=null) {
			cliente.getTelefones().add(clienteNewDTO.getTelefone2());
		}
		if (clienteNewDTO.getTelefone3() != null) {
			cliente.getTelefones().add(clienteNewDTO.getTelefone3());
		}
		return cliente;
		
	}

	private void updateData(Cliente clienteBanco, Cliente clienteDadosAtualizados) {
		clienteBanco.setNome(clienteDadosAtualizados.getNome());
		clienteBanco.setEmail(clienteDadosAtualizados.getEmail());
	}

}
