package com.pokemoncards.model.repository;

import java.util.List;
import java.util.Optional;

public interface CardField <T>{

	public List<T> findAll(Optional<String> username);
	
}
