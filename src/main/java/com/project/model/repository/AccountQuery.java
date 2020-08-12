package com.project.model.repository;

import java.util.List;

import com.project.model.entity.AccountId;
import com.project.model.entity.Card;

public interface AccountQuery extends Query{

	public int countDistinctCardsByUsername(String username);
	
	public List<Card> getAccountCardsListByPage(String username, int page);
	
	public AccountId getAccountId(String name);
	
	public int getNumberOfPages(String username);
		
}
