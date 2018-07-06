package com.github.sumuzhou;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class SPAConfig {

	@ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFoundException() {
    	return "forward:/index.html";
    }

}
