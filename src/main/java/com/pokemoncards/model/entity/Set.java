package com.pokemoncards.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

@Entity
public class Set implements Comparable<Set>, Identifiable<String>{

	@Id
	@NotEmpty
	private String id;
	
	@Transient
	private long quantity;
	
	@Transient
	private long userQuantity;

	public Set() {
	}
	
	public Set(String id) {
		this.id = id;
	}
	
	public Set(Set set, long quantity) {
		this(set.getId());
		this.quantity = quantity;
	}
	
	public Set(Set set, long quantity, long userQuantity) {
		this(set, quantity);
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
		Set other = (Set) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int compareTo(Set other) {
		return id.compareTo(other.id);
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
