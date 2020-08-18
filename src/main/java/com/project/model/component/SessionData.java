package com.project.model.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.project.model.service.SortType;

@Component
@Scope(value = "session")
public class SessionData {

	private SortType sortType;

	private SortType.OrderType orderType;

	private Map<String, Boolean> expanders;
	
	private int scrollPosition;
	
	private String lastVisited;

	{
		sortType = SortType.NAME;
		orderType = SortType.NAME.ASC;
		expanders = new HashMap<String, Boolean>();
		resetExpanders();
	}
	
	public void resetExpanders() {
		for (Expander expander : Expander.values())
			expanders.put(expander.getExpand(), false);
		scrollPosition = 0;
	}

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
		orderType = sortType.ASC;
	}

	public SortType.OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(SortType.OrderType orderType) {
		this.orderType = orderType;
	}

	public Map<String, Boolean> getExpanders() {
		return expanders;
	}

	public void switchExpander(Expander expander) {
		expanders.put(expander.getExpand(), !expanders.get(expander.getExpand()));
	}

	public int getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(int scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

	public void setExpanders(Map<String, Boolean> expanders) {
		this.expanders = expanders;
	}

	public String getLastVisited() {
		return lastVisited;
	}

	public void setLastVisited(String lastVisited) {
		this.lastVisited = lastVisited;
	}
	
}