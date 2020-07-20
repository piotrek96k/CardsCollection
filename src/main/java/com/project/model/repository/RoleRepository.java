package com.project.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.model.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String>{
	
}
