package com.project.model.repository;

import java.util.List;

import com.project.model.entity.AccountId;
import com.project.model.entity.Cards;

public interface AccountQuery {

	public int countDistinctCardsByUsername(String username);
	
	public List<Cards> getAccountCardsListByPage(String username, int page);
	
	public AccountId getAccountId(String name);
	
	public int getNumberOfPages(String username);
		
}
