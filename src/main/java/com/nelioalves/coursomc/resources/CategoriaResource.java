package com.nelioalves.coursomc.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.nelioalves.coursomc.domain.Categoria;
import com.nelioalves.coursomc.dto.CategoriaDTO;
import com.nelioalves.coursomc.services.CategoriaService;

@RestController
@RequestMapping(value="/categorias")
public class CategoriaResource {
	
	@Autowired
	private CategoriaService service;
	
	@RequestMapping(value ="/{id}",method = RequestMethod.GET)
	public ResponseEntity<Categoria> find(@PathVariable Integer id ) {
		Categoria categoria = service.buscar(id);
		return ResponseEntity.ok().body(categoria);
		
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> insert(@RequestBody Categoria obj){
		obj = service.inserir(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@RequestBody Categoria obj,@PathVariable Integer id){
		obj.setId(id);
		obj = service.atualizar(obj);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value ="/{id}",method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id ){
		service.excluir(id);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<CategoriaDTO>> findAll() {
		List<Categoria> categorias = service.buscaTodas();
		List<CategoriaDTO> categoriasDTO = categorias.stream().map(categoria -> new CategoriaDTO(categoria))
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(categoriasDTO);

	}
}
