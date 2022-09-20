package com.virtuous.domain;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Contact {
	public Integer id;
	public String contactType;
    public String referenceSource;
    public String referenceId;
    public String name;
    public String informalName;
    public String description;
    public String website;
    public String maritalStatus;
    public String anniversaryMonth;
    public String anniversaryDay;
    public String anniversaryYear;
    public String originSegmentId;
    public Boolean isPrivate;
    public Boolean isArchived;
    public ArrayList<ContactAddress> contactAddresses = new ArrayList<>();
    public ArrayList<ContactIndividual> contactIndividuals = new ArrayList<>();
    public String customFields;
    public ArrayList<CustomCollection> customCollections = new ArrayList<>();
}
