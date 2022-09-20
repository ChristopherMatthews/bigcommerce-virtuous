package com.virtuous.domain;

import java.util.Date;
import lombok.Data;

@Data
public class Product {
	private Integer quantity;
	private String source;
	private String sku;
	private Date date;
	private Date created;

}
