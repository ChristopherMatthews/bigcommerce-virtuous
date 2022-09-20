package com.bigcommerce.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor	
public class GiftCertificate {
	private String name;
    private int quantity;
    private boolean isTaxable;
    private int amount;
    private String type;
}
