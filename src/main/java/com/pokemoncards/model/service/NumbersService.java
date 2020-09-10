package com.pokemoncards.model.service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class NumbersService {
	
	private static DecimalFormat formatter;

	private NumbersService() {}
	
	public static String formatInteger(int value) {
		if (formatter == null)
			formatter = getDecimalFormatter();
		return formatter.format(value);
	}

	private static DecimalFormat getDecimalFormatter() {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter;
	}
	
}
