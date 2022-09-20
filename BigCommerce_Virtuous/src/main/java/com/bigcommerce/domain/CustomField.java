package com.bigcommerce.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomField {
	private String fieldId;
    private String fieldValue;
    private String name;
    private String value;
}
