package com.cinema.exception;

import lombok.Getter;

@Getter
public class NoAuthorizedException extends RuntimeException{

    private final String message;

    public NoAuthorizedException(String message){
     this.message = message;
    }
}
