package com.pokemoncards.model.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pokemoncards.model.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String>{
	
}
