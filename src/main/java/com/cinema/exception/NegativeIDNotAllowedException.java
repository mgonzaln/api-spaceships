package com.cinema.exception;

import lombok.Getter;

@Getter
public class NegativeIDNotAllowedException extends RuntimeException{

    private final String message;
    public NegativeIDNotAllowedException(String message){
        this.message = message;
    }
}
