package com.amazon.SSOF.ut;

public class SSOFDaoUTSchemaNotFoundException extends Exception {

	@Override
	public String getMessage() {
		return "Schema annotation not found in this class";
	}
}
