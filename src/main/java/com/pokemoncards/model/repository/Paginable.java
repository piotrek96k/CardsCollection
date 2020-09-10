package com.pokemoncards.model.repository;

public interface Paginable {

	public static final int PAGE_SIZE = 25;
	
	public default int getNumberOfPagesFromNumberOfCards(long cards) {
		return (int) cards / PAGE_SIZE + (cards % PAGE_SIZE == 0 ? 0 : 1);
	}
	
}
