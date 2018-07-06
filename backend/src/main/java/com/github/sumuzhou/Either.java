package com.github.sumuzhou;

import java.io.Serializable;

public class Either implements Serializable {
	
	private static final long serialVersionUID = -4494813756556654836L;
	private String error;
	private Object data;
	private Either(Object obj) {
		if (obj instanceof Exception) error = ((Exception) obj).getMessage();
		else data = obj;
	}
	public static Either of(Object obj) {
		return new Either(obj);
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

}
