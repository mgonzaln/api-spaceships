package com.cinema.exception;

import lombok.Getter;

@Getter
public class BadParametersRequestException extends RuntimeException{

    private final String message;

    public BadParametersRequestException(final String message){
        this.message = message;
    }
}
