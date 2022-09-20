package com.bigcommerce.domain;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BillingAddress {
	 private String first_name;
	    private String last_name;
	    private String company;
	    private String street_1;
	    private String street_2;
	    private String city;
	    private String state;
	    private String zip;
	    private String country;
	    private String country_iso2;
	    private String phone;
	    private String email;
	    private ArrayList<FormField> form_fields;

}
