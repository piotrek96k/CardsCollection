package com.project.model.repository;

import java.util.ArrayList;
import java.util.List;

public abstract class RepositoryImpl {

	protected <T> List<T> getInstancesList(Class<T> type, List<?> rawList) {
		List<T> list = new ArrayList<T>();
		for (Object object : rawList)
			if (object instanceof Object[])
				list.add(getInstance(type, (Object[]) object));
		return list;
	}

	protected <T> T getInstance(Class<T> type, Object[] parameters) {
		Class<?>[] parameterTypes = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++)
			parameterTypes[i] = parameters[i].getClass();
		try {
			return type.getConstructor(parameterTypes).newInstance(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
