package com.pokemoncards.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

@Entity
public class Type implements Comparable<Type>, Identifiable<String> {

	@Id
	@NotEmpty
	private String id;

	@ManyToMany(mappedBy = "types", fetch = FetchType.LAZY)
	private List<Card> cards;
	
	@Transient
	private long quantity;
	
	@Transient
	private long userQuantity;
	
	public  Type() {
	}
	
	public Type(String id) {
		this.id = id;
	}
	
	public Type(Type type,long quantity) {
		this(type.getId());
		this.quantity = quantity;
	}
	
	public Type(Type type, long quantity, long userQuantity) {
		this(type, quantity);
		this.userQuantity = userQuantity;
	}

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
	
	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public long getUserQuantity() {
		return userQuantity;
	}

	public void setUserQuantity(long userQuantity) {
		this.userQuantity = userQuantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Type other = (Type) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
