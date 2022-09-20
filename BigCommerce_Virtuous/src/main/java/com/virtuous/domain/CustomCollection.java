package com.virtuous.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CustomCollection{
	private Integer customCollectionId;
	private Integer collectionInstanceId;
    public String customCollectionName;
    public List<Field> fields = new ArrayList<>();
}