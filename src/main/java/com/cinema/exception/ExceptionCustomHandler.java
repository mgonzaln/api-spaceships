package com.cinema.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionCustomHandler {

    @ExceptionHandler(value={BadParametersRequestException.class})
    public ResponseEntity<?> handleBadRequest(BadParametersRequestException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value={NegativeIDNotAllowedException.class})
    public ResponseEntity<?> greaterThanZero(NegativeIDNotAllowedException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value=DataExistsException.class)
    public ResponseEntity<String> handleDataExists(DataExistsException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value={NoAuthorizedException.class})
    public ResponseEntity<Object> handleNoAuthorized(Exception e){
        return new ResponseEntity<>("Not authorized to do this operation", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value={NotFoundException.class})
    public ResponseEntity<String> notfound(NotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(value={SpaceshipException.class})
    public ResponseEntity<Object> spaceshipExcedption(SpaceshipException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<String> exception(Exception e){
        log.error(e.getMessage());
        return new ResponseEntity<>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value={AuthorizationDeniedException.class})
    public ResponseEntity<String> noauth(Exception e){
        log.error("Not authorized to do this operation");
        return new ResponseEntity<>("Not authorized to do this operation", HttpStatus.UNAUTHORIZED);
    }
}
