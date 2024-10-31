package com.cinema.exception;

import lombok.Getter;

@Getter
public class DataExistsException extends RuntimeException
{
private final String message;

    public DataExistsException(final String message){
        this.message = message;
    }
}
