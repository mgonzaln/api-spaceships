package com.cinema.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends  RuntimeException{

    private final String message;

    public NotFoundException(final String message)
    {
        this.message = message;
    }

}
