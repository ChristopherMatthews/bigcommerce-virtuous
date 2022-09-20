package com.bigcommerce.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GiftWrapping {
	private String name;
    private String message;
    private int amount;

}
