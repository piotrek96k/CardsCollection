package com.project.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Role {

	@Id
	private String role;

	@ManyToMany(mappedBy = "roles")
	private List<Account> users;
	
	public Role() {}
	
	public Role(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public List<Account> getUsers() {
		return users;
	}

	public void setUsers(List<Account> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Role [role=" + role + "]";
	}

}
