package com.pokemoncards.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pokemoncards.model.entity.Type;

public interface TypeRepository extends JpaRepository<Type, String>, CardField<Type>{

}
