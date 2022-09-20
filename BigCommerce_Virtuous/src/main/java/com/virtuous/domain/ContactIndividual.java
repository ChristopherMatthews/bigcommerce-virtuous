package com.virtuous.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ContactIndividual{
    public String firstName;
    public String lastName;
    public String prefix;
    public String middleName;
    public String suffix;
    public String birthMonth;
    public String birthDay;
    public String birthYear;
    public String approximateAge;
    public String gender;
    public String passion;
    public Boolean isPrimary;
    public Boolean isSecondary;
    public Boolean isDeceased;
    public ArrayList<ContactMethod> contactMethods = new ArrayList<>();
    public Map<String, String> customFields = new HashMap<String,String>();
}