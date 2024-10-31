package com.cinema.exception;

import lombok.Getter;

@Getter
public class SpaceshipException extends RuntimeException
{
    private final String message;
    public SpaceshipException(String message){
    this.message=message;
    }
}
