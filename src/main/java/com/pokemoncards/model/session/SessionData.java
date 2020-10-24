package com.pokemoncards.model.session;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionData {

	private SortType sortType;

	private SortType.OrderType orderType;

	private Map<String, Boolean> expanders;

	private double scrollPosition;

	private String lastVisited;

	{
		sortType = SortType.NAME;
		orderType = SortType.NAME.ASC;
		expanders = new HashMap<String, Boolean>();
		resetExpanders();
		lastVisited = "";
	}

	public void resetExpanders() {
		for (Expander expander : Expander.values())
			expanders.put(expander.getExpand(), false);
		scrollPosition = 0.0;
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

	public double getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(double scrollPosition) {
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