package model;

import java.awt.image.BufferedImage;
public class Product {
	//Product categories
	public enum ProductType{
		BEVERAGES,
		BAKERY,
		CANNED_GOODS,
		DAIRY,
		FROZEN_FOODS,
		DRY_BAKING_GOODS,
		MEAT,
		PRODUCE,
		CLEANERS,
		PAPER_GOODS,
		PERSONAL_CARE,
		OTHER	//baby items, pet items, batteries, greeting cards, etc.
	}
	//attributes
	private int productId;
	private String productName;
	private ProductType productType;
	private int amount;
	private double weight;
	private double price;
	private String area;
	private int row;
	private int prepTime;
	private BufferedImage image;
	//Constructors
	public Product(int productId,String productName,String productType,int amount,double weight,double price,String area, int row,int prepTime,BufferedImage image) {
		setProductId(productId);
		setProductName(productName);
		setProductType(productType);
		setAmount(amount);
		setWeight(weight);
		setPrice(price);
		setArea(area);
		setRow(row);
		setPrepTime(prepTime);
		setImage(image);
	}
	//Getters and Setters
	public int getProductId() {return productId;}
	public void setProductId(int productId) {this.productId = productId;}
	public String getProductName() {return productName;}
	public void setProductName(String productName) {this.productName = productName;}
	public ProductType getProductType() {return productType;}
	public void setProductType(ProductType productType) {this.productType=productType;}
	public void setProductType(String productType) {this.productType=ProductType.valueOf(productType);}
	public int getAmount() {return amount;}
	public void setAmount(int amount) {this.amount = amount;}
	public double getWeight() {return weight;}
	public void setWeight(double weight) {this.weight = weight;}
	public double getPrice() {return price;}
	public void setPrice(double price) {this.price = price;}
	public String getArea() {return area;}
	public void setArea(String area) {this.area = area;}
	public int getRow() {return row;}
	public void setRow(int row) {this.row = row;}
	public void setPrepTime(int prepTime) {this.prepTime=prepTime;}
	public int getPrepTime() {return prepTime;}
	public void setImage(BufferedImage image) {this.image=image;}
	public BufferedImage getImage() {return image;}
	//Methods
	//helps with inserting a new product to the database
	public String insertProduct() {
		return productId+",'"+productName+"','"+productType+"',"+amount+","+weight+","+price+",'"+area+"',"+row+","+prepTime;
	}
	//for presenting the info to the manager
	public String toString() {
		return 	"\n  Product:\t"+productName
				+"\n\n  Product ID:\t"+productId
				+"\n\n  Product Type:  "+productType
				+"\n\n  Weight*:\t"+weight
				+"\n\n  Pricing:\t"+price
				+"\n\n  Amount**:\t"+amount
				+"\n\n  Area:\t"+area
				+"\n\n  Row:\t"+row
				+"\n\n  Prep Time:\t"+prepTime
				+"\n\n  *Weight is shown for a single unit."
				+ "\n\n  **Amount currently in stock.";
	}
}
