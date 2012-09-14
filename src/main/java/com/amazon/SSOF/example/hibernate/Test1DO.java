package com.amazon.SSOF.example.hibernate;

import java.io.Serializable;

public class Test1DO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2978123831841548171L;
	private Integer id;
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
