package com.virtuous.domain;

import lombok.Data;

@Data
public class ContactAddress{
    private String label;
    private String address1;
    private String address2;
    private String city;
    private String stateCode;
    private String postal;
    private String countryCode;
    private Boolean isPrimary;
    private String latitude;
    private String longitude;
}