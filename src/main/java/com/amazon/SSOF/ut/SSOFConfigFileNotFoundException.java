package com.amazon.SSOF.ut;

public class SSOFConfigFileNotFoundException extends Exception {

	private String cause;

	public SSOFConfigFileNotFoundException(String cause) {
		this.cause = cause;
	}
}
