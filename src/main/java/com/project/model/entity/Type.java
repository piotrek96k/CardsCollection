package com.project.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;

@Entity
public class Type implements Comparable<Type>, Identifiable<String> {

	@Id
	@NotEmpty
	private String id;

	@ManyToMany(mappedBy = "types")
	private List<Card> cards;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	@Override
	public String toString() {
		return id;
	}

	@Override
	public int compareTo(Type other) {
		return id.compareTo(other.id);
	}

}
