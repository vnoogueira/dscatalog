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
import com.vnoogueira.dscatolog.entities.Category;
import com.vnoogueira.dscatolog.repositories.CategoryRepository;
import com.vnoogueira.dscatolog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {

		Page<Category> list = repository.findAll(pageRequest);

		return list.map(x -> new CategoryDTO(x));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		return new CategoryDTO(entity);

	}

	@Transactional
	public CategoryDTO insertCategory(CategoryDTO obj) {
		Category entity = new Category();
		entity.setName(obj.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO updateCategory(Long id, CategoryDTO obj) {
		try {

			Category entity = repository.getReferenceById(id);
			entity.setName(obj.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);

		} catch (EntityNotFoundException e) {

			throw new ResourceNotFoundException("Id not found: " + id);

		}
	}

	public void deleteCategory(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Database Integrity");
		} catch (DataIntegrityViolationException d) {

		}

	}

}
