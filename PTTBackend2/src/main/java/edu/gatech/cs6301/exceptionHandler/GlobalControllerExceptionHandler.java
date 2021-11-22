package edu.gatech.cs6301.exceptionHandler;

import edu.gatech.cs6301.exception.InvalidRequestBodyException;
import edu.gatech.cs6301.exception.ResourceConflictException;
import edu.gatech.cs6301.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

//<<<<<<< HEAD:PTTBackend2/src/main/java/com/cs6301/PTTBackend2/exceptionHandler/GlobalControllerExceptionHandler.java
//import com.cs6301.PTTBackend2.exception.*;
//
//=======
//
//>>>>>>> origin/backend-jipeng:PTTBackend2/src/main/java/edu/gatech/cs6301/exceptionHandler/GlobalControllerExceptionHandler.java

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<String> handleResourceConflict(RuntimeException runtimeException) {
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(RuntimeException runtimeException) {
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleInvalidRequestBodyJackson(RuntimeException runtimeException) {
        return new ResponseEntity<>("Invalid Request Body", HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NumberFormatException.class})
    public ResponseEntity<String> handleInvalidRequestParameter(RuntimeException runtimeException) {
        return new ResponseEntity<>("Invalid Path ID", HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleInvalidQueryParameter(RuntimeException runtimeException) {
        return new ResponseEntity<>("Invalid Query Parameter(s)", HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidRequestBodyException.class})
    public ResponseEntity<String> handleInvalidRequestBody(RuntimeException runtimeException) {
        return new ResponseEntity<>(runtimeException.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<String> handleUnknownURL(RuntimeException runtimeException) {
        return new ResponseEntity<>("Unknown URL", HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<String> handleUnknownMethod(RuntimeException runtimeException) {
        return new ResponseEntity<>("Unknown Method", HttpStatus.METHOD_NOT_ALLOWED);
    }
}
