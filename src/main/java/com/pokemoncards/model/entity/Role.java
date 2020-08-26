package com.pokemoncards.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;

@Entity
public class Role implements Identifiable<String>{

	@Id
	@NotBlank
	private String id;

	@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
	private List<Account> accounts;
	
	public Role() {}
	
	public Role(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	@Override
	public String toString() {
		return "Role [role=" + id + "]";
	}

}
