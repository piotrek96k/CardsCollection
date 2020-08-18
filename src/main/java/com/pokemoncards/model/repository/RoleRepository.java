package com.pokemoncards.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pokemoncards.model.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String>{
	
}
