package com.vnoogueira.dscatolog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vnoogueira.dscatolog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

}
	