package com.project.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.model.entity.Card;

public interface CardRepository extends JpaRepository<Card, String>, CardQuery{
	
	@Query(value = "select * from card order by name, id asc limit(100) offset(((:page)-1)*100)" , nativeQuery = true)
	public List<Card> getCardsByPageOrderByName(@Param("page")int page);
		
}
