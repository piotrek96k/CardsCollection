package com.project.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.model.entity.Rarity;

public interface RarityRepository extends JpaRepository<Rarity, String>{

	@Query(value = "select * from rarity order by id asc", nativeQuery = true)
	public List<Rarity> findAllRaritiesOrderById();
	
}
