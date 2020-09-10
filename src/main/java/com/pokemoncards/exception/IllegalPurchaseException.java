package com.pokemoncards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class IllegalPurchaseException extends RuntimeException{

	private static final long serialVersionUID = 6075167584349399310L;

}
