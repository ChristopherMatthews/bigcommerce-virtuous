package com.bigcommerce.domain;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhysicalItem {
	private String id;
    private String parentId;
    private int variantId;
    private int productId;
    private String sku;
    private String name;
    private String url;
    private int quantity;
    private boolean isTaxable;
    private String imageUrl;
    private Discount discounts;
    private int discountAmount;
    private int couponAmount;
    private int listPrice;
    private int salePrice;
    private int extendedListPrice;
    private int extendedSalePrice;
    private String type;
    private boolean addedByPromotion;
    private boolean isShippingRequired;
    private GiftWrapping giftWrapping;
    private ArrayList<Object> categories;
}
