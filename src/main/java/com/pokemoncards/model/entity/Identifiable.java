package com.pokemoncards.model.entity;

public interface Identifiable<T> {

	public T getId();

	public void setId(T id);

}
