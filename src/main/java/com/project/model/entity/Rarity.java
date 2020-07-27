package com.project.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Rarity {

	@Id
	@NotNull
	private String id;

	public Rarity(String id) {
		this.id = id;
	}

	public Rarity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
