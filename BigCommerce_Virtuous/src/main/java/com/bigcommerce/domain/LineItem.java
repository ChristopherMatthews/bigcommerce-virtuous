package com.bigcommerce.domain;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LineItem {
	private ArrayList<PhysicalItem> physicalItems;
    private ArrayList<DigitalItem> digitalItems;
    private ArrayList<GiftCertificate> giftCertificate;
    private int id;

}
