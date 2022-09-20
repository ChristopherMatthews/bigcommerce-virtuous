package com.bigcommerce.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bigcommerce.domain.BillingAddress;
import com.bigcommerce.domain.Order;
import com.bigcommerce.domain.Orders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.virtuous.domain.Contact;
import com.virtuous.domain.ContactAddress;
import com.virtuous.domain.ContactIndividual;
import com.virtuous.domain.ContactMethod;
import com.virtuous.domain.Field;
import com.virtuous.domain.Product;

@Service
public class OrderService {
	@Autowired
	private RestTemplate restTemplate;
	@Value("${findcontact_url}")
	private String findcontact_url;
	@Value("${product_url}")
	private String product_url;
	@Value("${contact_url}")
	private String contact_url;
	@Value("${collection}")
	private String collection;
	@Value("${contact_method_url}")
	private String contact_method_url;
	@Value("${x_auth_token}")
	private String xauth_token;
	@Value("${bearer_token}")
	private String bearer_token;
	
	public Order addOrder(Order order) {
		return order;
	}

	public void updateVirtuous(List<Product> list, Orders order) {
		String id = checkIfContactExist(order.getBilling_address().getEmail());
		Contact contact = buildContact(list, order);
		if (id != null) {
			System.out.println("Calling addproduct api");
			addProduct(list, order, id);
			addPhoneNumber(id, order);
			System.out.println("Succssfully added product");
		} else {
			System.out.println("Calling addUpdateContact api");
			addUpdateContact(contact, contact_url, HttpMethod.POST);
			id = checkIfContactExist(order.getBilling_address().getEmail());
			addProduct(list, order, id);
			addPhoneNumber(id, order);
			System.out.println("Succssfully addUpdateContact product");
		}
	}
	
	private void addUpdateContact(Contact contact, String url, HttpMethod method) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String jsonContact = null;
		try {
			jsonContact = ow.writeValueAsString(contact);
			//System.out.println(jsonContact);
			HttpHeaders headers = getVituousHttpHeaders();			
			HttpEntity<String> request = new HttpEntity<String>(jsonContact, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, method, request, String.class);
			System.out.println("Success: " + response.getStatusCode());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	private void addProduct(List<Product> list, Orders order, String id) {
		
		for(Product product: list) {
			List<Field> fieldList = new ArrayList<Field>();
			Field field = new Field("Source", "BigCommerce");
			fieldList.add(field);
			field = new Field("SKU", product.getSku());
			//enable below code for sandbox env 
			//field = new Field("SKU", "HWBKFREECHEN");
			fieldList.add(field);
			String shiftTimeZoneDate = shiftTimeZone(order.getDate_created());
			System.out.println(shiftTimeZoneDate);
			field = new Field("Date", shiftTimeZoneDate);
			fieldList.add(field);
			field = new Field("Quantity", Integer.toString(product.getQuantity()));
			fieldList.add(field);
			try {
				Gson gson = new Gson();
				String element = gson.toJson(fieldList,new TypeToken<ArrayList<Field>>() {}.getType());
				JSONArray listString = new JSONArray(element);
				element = "{\"fields\": " + listString.toString() + "}";
				JSONObject jObject  = new JSONObject(element);
				System.out.println(jObject);
				HttpHeaders headers = getVituousHttpHeaders();
				HttpEntity<String> request = new HttpEntity<String>(jObject.toString(), headers);
				ResponseEntity<String> response = restTemplate.exchange(product_url + id + collection, HttpMethod.POST, request, String.class);
				System.out.println("Success: " + response.getStatusCode());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private String shiftTimeZone(Date date) {
		SimpleDateFormat sdfAmerica = new SimpleDateFormat("M/d/Y");
		sdfAmerica.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		String sDateInAmerica = sdfAmerica.format(date);
		return sDateInAmerica;
	}

	private String checkIfContactExist(String email) {
		HttpHeaders headers = getVituousHttpHeaders();
		try {
			HttpEntity<String> request = new HttpEntity<String>(headers);
			ResponseEntity<String> response =
			restTemplate.exchange(findcontact_url+email, HttpMethod.GET, request,
			String.class); 
			if (response != null) { 
				JSONObject json = new JSONObject(response.getBody()); System.out.println(json.get("id")); return
			json.get("id").toString(); 
			
			}
					
		} catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}

	private Contact buildContact(List<Product> list, Orders order) {
		Contact contact = new Contact();
		if (order.getBilling_address() != null) {
			BillingAddress billAddress = order.getBilling_address();
			contact.setContactType("Household");
			String firstName = billAddress.getFirst_name();
			String firstLetStr = firstName.substring(0, 1);
	        String remLetStr = firstName.substring(1);
	        firstLetStr = firstLetStr.toUpperCase();
	        String firstLetterCapitalizedName = firstLetStr + remLetStr.toLowerCase();
			String lastName = billAddress.getLast_name();
			String LastNameLetStr = lastName.substring(0, 1);
	        String remLetStrL = lastName.substring(1);
	        LastNameLetStr = LastNameLetStr.toUpperCase();
	        String firstLetterCapitalizedLastName = LastNameLetStr + remLetStrL.toLowerCase();
			contact.setName( firstLetterCapitalizedName + " " + firstLetterCapitalizedLastName);
			contact.setIsPrivate(false);
			contact.setIsArchived(false);
			ContactAddress contactAddress = new ContactAddress();
			contactAddress.setLabel("Home");
			if (billAddress.getStreet_2() != null) {
				contactAddress.setAddress2(billAddress.getStreet_2().replaceAll("[^\" \",a-zA-Z0-9]", ""));
			}
			if (billAddress.getStreet_1() !=null) {
				contactAddress.setAddress1(billAddress.getStreet_1().replaceAll("[^\" \",a-zA-Z0-9]", ""));
			}
			if (billAddress.getCity() != null) {
				contactAddress.setCity(billAddress.getCity().replaceAll("[^\" \",a-zA-Z0-9]", ""));
			}
			if ( billAddress.getState() != null) {
				String state = billAddress.getState();
				contactAddress.setStateCode(state);
			}
			contactAddress.setPostal(billAddress.getZip());
			contactAddress.setCountryCode(billAddress.getCountry_iso2());
			contactAddress.setIsPrimary(true);
			contact.getContactAddresses().add(contactAddress);	
			ContactIndividual contactIndividual = new ContactIndividual();
			contactIndividual.setPrefix("");
			
			contactIndividual.setFirstName(firstLetterCapitalizedName);
			contactIndividual.setMiddleName("");
			contactIndividual.setLastName(firstLetterCapitalizedLastName);
			String gender = "";
			if (order != null && order.getBilling_address()!=null && order.getBilling_address().getForm_fields().size() > 0) {
				gender = order.getBilling_address().getForm_fields().get(0).getValue();
			}
			
			if (order != null && order.getBilling_address()!=null && order.getBilling_address().getForm_fields().size() > 1) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("Entered Database By", "Resource");
				map.put("Found Zoweh By", order.getBilling_address().getForm_fields().get(1).getValue());
				contactIndividual.setCustomFields(map);
			}
			
			if (gender.equalsIgnoreCase("Mr.") || gender.equalsIgnoreCase("Rev.") || gender.equalsIgnoreCase("Pastor")) {
				contactIndividual.setGender("Male");
				contactIndividual.setPrefix(gender);
			} else if (gender.equalsIgnoreCase("Mrs.") || gender.equalsIgnoreCase("Ms.")) {
				contactIndividual.setGender("Female");
				contactIndividual.setPrefix(gender);
			} else {
				contactIndividual.setGender("N/A");
			}
			contactIndividual.setIsPrimary(true);
			contactIndividual.setIsSecondary(true);
			contactIndividual.setIsDeceased(true);

			ContactMethod contactMethod = new ContactMethod();
			contactMethod.setIsOptedIn(true);
			contactMethod.setIsPrimary(true);
			contactMethod.setType("Home Email");
			contactMethod.setValue(billAddress.getEmail());
			contactIndividual.getContactMethods().add(contactMethod);
			contact.getContactIndividuals().add(contactIndividual);
		}		
		return contact;		
	}
	
	private void addPhoneNumber(String id, Orders order) {
		try {
			ContactMethod contactMethod = new ContactMethod();
			contactMethod.setIsOptedIn(true);
			contactMethod.setIsPrimary(true);
			contactMethod.setType("Mobile Phone");
			String input = order.getBilling_address().getPhone().replaceAll("[^0-9]", "");
			String phoneNumber = "";
			if (input.length() >= 10) {
				String formattedPhoneNumber = input.substring(input.length()-10);
				phoneNumber = formattedPhoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
			} else {
				phoneNumber = input.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
			}
			
			if (!phoneNumber.isEmpty()) {
				contactMethod.setValue(phoneNumber);
				contactMethod.setContactIndividualId(Integer.valueOf(id));
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String json = ow.writeValueAsString(contactMethod);
				HttpHeaders headers = getVituousHttpHeaders();
				HttpEntity<String> request = new HttpEntity<String>(json, headers);
				System.out.println("Calling Add Phone number virtuous api: ");
				ResponseEntity<String> response = restTemplate.exchange(contact_method_url, HttpMethod.POST, request, String.class);
				System.out.println("Success: " + response.getStatusCode());
			} else {
				System.out.println("Phone number is not provided");
			}
		} catch(Exception e) {
			System.err.print("Exception occurred while adding phone number" + e);
		}
		
	}
	
	private HttpHeaders getVituousHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", bearer_token);
		return headers;
	}

}
