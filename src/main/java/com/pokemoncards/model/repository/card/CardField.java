package com.pokemoncards.model.repository.card;

import java.util.List;
import java.util.Optional;

public interface CardField <T>{

	public List<T> findAll(Optional<String> username);
	
	public Optional<T> findById(String id, Optional<String> username);
	
}
