package com.bigcommerce.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Currency {
	private String name;
	private String code;
	private String symbol;
	private Integer decimalPlaces;

}
