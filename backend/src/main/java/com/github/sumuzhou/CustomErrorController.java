package com.github.sumuzhou;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

	private static final String PATH = "/error";

	@RequestMapping(value = PATH)
	public String error() {
		return "<html><head><title>Oops</title></head><body><p1>Something bad happened</p1></body></html>";
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
}
