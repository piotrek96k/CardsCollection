package com.project.model.repository;

public interface Query {

	public static final int PAGE_SIZE = 100;
	
	 default public int getNumberOfPagesFromNumberOfCards(long cards) {
		return (int)cards / PAGE_SIZE + (cards % PAGE_SIZE == 0 ? 0 : 1);
	}

}
