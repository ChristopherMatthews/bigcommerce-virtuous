package com.virtuous.domain;

import lombok.Data;

@Data
public class ContactMethod{
	private Integer contactIndividualId;
    private String type;
    private String value;
    private Boolean isOptedIn;
    private Boolean isPrimary;
}