package com.vnoogueira.dscatolog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vnoogueira.dscatolog.dto.CategoryDTO;
import com.vnoogueira.dscatolog.dto.ProductDTO;
import com.vnoogueira.dscatolog.entities.Category;
import com.vnoogueira.dscatolog.entities.Product;
import com.vnoogueira.dscatolog.repositories.CategoryRepository;
import com.vnoogueira.dscatolog.repositories.ProductRepository;
import com.vnoogueira.dscatolog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {

		Page<Product> list = repository.findAll(pageRequest);

		return list.map(x -> new ProductDTO(x));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Product not found"));
		return new ProductDTO(entity, entity.getCategory());

	}

	@Transactional
	public ProductDTO insertProduct(ProductDTO obj) {
		Product entity = new Product();
		copyDtoToEntity(obj, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO updateProduct(Long id, ProductDTO obj) {
		try {

			Product entity = repository.getReferenceById(id);
			copyDtoToEntity(obj, entity);
			return new ProductDTO(entity);

		} catch (EntityNotFoundException e) {

			throw new ResourceNotFoundException("Id not found: " + id);

		}
	}

	public void deleteProduct(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Database Integrity");
		} catch (DataIntegrityViolationException d) {

		}

	}

	private void copyDtoToEntity(ProductDTO obj, Product entity) {
		entity.setName(obj.getName());
		entity.setDate(obj.getDate());
		entity.setDescription(obj.getDescription());
		entity.setPrice(obj.getPrice());
		entity.setImgUrl(obj.getImgUrl());
		entity.getCategory().clear();

		for (CategoryDTO catDto : obj.getCategories()) {
			Category category = categoryRepository.getReferenceById(catDto.getId());
			entity.getCategory().add(category);

		}
	}

}
