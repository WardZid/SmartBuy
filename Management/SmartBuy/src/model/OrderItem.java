package model;

public class OrderItem {
	//attributes
	private Product product;
	private int amount;
	//constructors
	public OrderItem(Product product,int amountInOrder) {
		setProduct(product);
		setAmount(amountInOrder);
	}
	//getters and setters
	public Product getProduct() {return product;}
	public void setProduct(Product product) {this.product = product;}
	public int getAmount() {return amount;}
	public void setAmount(int amount) {this.amount = amount;}
	//methods
	@Override
	public String toString() {
		return "\n\tProduct: ["+product.getProductName()+"]\n\t\t Amount:["+amount+"]\t | Area: ["+product.getArea()+"]\t | Row: ["+product.getRow()+"]\n";
	}
}
