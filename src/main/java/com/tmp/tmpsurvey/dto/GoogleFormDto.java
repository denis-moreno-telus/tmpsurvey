package com.tmp.tmpsurvey.dto;

public class GoogleFormDto {

	private String name;
	private String id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GoogleFormDto(String id, String surveyName) {
		this.id = id;
		this.name = surveyName;

	}

}
