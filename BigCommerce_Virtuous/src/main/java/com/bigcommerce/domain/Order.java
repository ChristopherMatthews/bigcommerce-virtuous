package com.bigcommerce.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Order {
	private String store_id;
	private String producer;
	private Datas data;
}
