package com.pokemoncards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NotAuthenticatedException extends RuntimeException{

	private static final long serialVersionUID = -2696055711193664606L;

}
