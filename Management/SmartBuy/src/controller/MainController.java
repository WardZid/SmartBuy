package controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import model.*;

public class MainController {
	public static void logIn(String id,String password) throws Exception{
		//method receives email and user and logs the user in as long as the values entered are eligible
		//id and password must not be left blank
		if(id.equals("")) throw new Exception("Enter an ID please");
		if(password.equals("")) throw new Exception("Enter a password please");
		
		DBControl.connect();
		System.out.println("Connected for log in");
		
		try {
			boolean userExists=false;//if id is never matched, an exception will be thrown
			ResultSet rs = DBControl.statement.executeQuery("SELECT * FROM Managers"); //get list of managers from db
			while (rs.next()){
				if(rs.getString("id").equals(id)) {
					userExists=true;
					if(rs.getString("password").equals(password)){
						//if the ID and password match, the user is signed in
						DBControl.setUser(new User(
								rs.getString("id"),
								rs.getString("first_name"),
								rs.getString("last_name"),
								rs.getString("type")
								));
						System.out.println("Log in Successful");
						//update date time of last time user entered
						DBControl.statement.execute("UPDATE managers SET last_connected='"+currentTime()+"' where id="+id);
						break;
					}else throw new Exception("Incorrect Password");
				}	
			}
			if(!userExists)//if no user found with the entered id
				throw new Exception("no such user!");
		} catch (Exception e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	public static void logOut() {
		//user is set as null to logout
		DBControl.removeUser();
	}
	public static String userType() {
		//return type of user that is signed in
		return DBControl.getUser().getType().toString();
	}
	private static String currentTime() {
		//current time is formatted into an appropriate datetime for mysql
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //formats date and time to be suitable for sql
		Date date = new Date(System.currentTimeMillis());  
		System.out.println("user logged at: "+formatter.format(date)); 
		return formatter.format(date);
	}
	public static void addNewManager(String id,String firstName,String lastName,String password,String type) throws Exception {
		DBControl.connect();
		
		int linesaffected;
		try {
			//insert the new manager into the database
			//if the manager already exiss or any other similar errors occur, the exception will have that data and will be thrown to the ui
			linesaffected = DBControl.statement.executeUpdate("INSERT INTO managers(id,first_name,last_name,password,type) VALUES ("
																+id +",'"
																+ firstName +"','"
																+lastName+"','"
																+password+"','"
																+type+"')");
			//validation print
			System.out.println("lines affected: "+linesaffected);
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	public static Product[] getProducts(String searchEntry) throws Exception{
		//get all products from database according to search entry
		DBControl.connect();
		try {
			ResultSet rs;//result of query
			if(searchEntry.isEmpty())//if the search is empty, all the products are fetched
				rs = DBControl.statement.executeQuery("SELECT * FROM Products");
			else//the search entry is used in the statement to fetch appropriate products
				rs = DBControl.statement.executeQuery("SELECT * FROM Products WHERE product_name LIKE '%"+searchEntry+"%'");
			System.out.println("Products Recieved");
			int i=0,size =0;
			//t get the size of the table received
			rs.last();    // moves to the last row
			size = rs.getRow(); // get row id 
			System.out.println("size of product table: ["+size+"]");
			
			Product listOfProducts[]=new Product[size];
			rs.beforeFirst();	//---move back to first row
			while(rs.next()) {
				//build array of products received 
				listOfProducts[i++]=new Product(rs.getInt("product_id"),
						rs.getString("product_name"),
						rs.getString("product_type"),
						rs.getInt("amount"),
						rs.getInt("weight"),
						rs.getInt("price"),
						rs.getString("area"),
						rs.getInt("row"),
						rs.getInt("prep_time"),
						blobToImage(rs.getBlob("image")));
			}
			System.out.println("List of products contructed and returned");
			return listOfProducts;
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			DBControl.disconnect();
		}
		return null;
		//null returned just for error's sake. if all goes as planned, the array is returned, if not, an exception is thrown
	}
	private static BufferedImage blobToImage(java.sql.Blob blob) {
		//method turns a blob into a buffered image 
		try {
			//first inot binary stream
			InputStream in = blob.getBinaryStream();  
			//then the library does its thing
			BufferedImage image = ImageIO.read(in);
			return image;
		}catch(Exception e) {
			System.out.println("Image Convertion Error");
			e.printStackTrace();
			return null;
		}
	}
	public static void addNewProduct(int productId, String productName,String productType, int amount, double weight, double price,String area,int row,int prepTime,String imagePath) throws Exception{
		DBControl.connect();
		try {
			//i have to use a prepared statement because it is the easiest way to upload an image to mysql atabase
			File imageFile=new File(imagePath);//get the image as a file
			InputStream is = new FileInputStream(imageFile);
			PreparedStatement ps = DBControl.connection.prepareStatement("insert into products values(?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, productId);
            ps.setString(2, productName);
            ps.setString(3, productType);
            ps.setInt(4, amount);
            ps.setDouble(5,weight );
            ps.setDouble(6, price);
            ps.setString(7, area);
            ps.setInt(8, row);
            ps.setInt(9,prepTime );
            ps.setBinaryStream(10, is);
            ps.executeUpdate();
			System.out.println("new product added to database"+productName);
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	public static void editProduct(int productId,int columnIndex,String newChange) throws Exception {
		DBControl.connect();
		int linesaffected;
		try {
			String column="";
			System.out.println(columnIndex);
			if(columnIndex==9) {
				//prepared statements help with blob uploading
				File imageFile=new File(newChange);
				InputStream is = new FileInputStream(imageFile);
				PreparedStatement ps = DBControl.connection.prepareStatement("UPDATE products SET image = ? WHERE product_id = ?");
				ps.setBinaryStream(1, is);
				ps.setInt(2, productId);
				ps.executeUpdate();
			}else {//determine which column was sent to be changed and apply the appropriate colu0mn name
				if(columnIndex==1) column="product_name";
				else if(columnIndex==2) column="amount";
				else if(columnIndex==3) column="weight";
				else if(columnIndex==4) column="price";
				else if(columnIndex==5) column="area";
				else if(columnIndex==6) column="row";
				else if(columnIndex==7) column="prep_time";
				else if(columnIndex==8) column="product_type";
				else throw new Exception("edit problem");
				linesaffected = DBControl.statement.executeUpdate("update products set "+column+" = '"+newChange+"' where product_id="+productId+";");
				System.out.println("lines affected: "+linesaffected);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBControl.disconnect();
		}
	}
	public static void deleteProduct(int productId) throws Exception {
		DBControl.connect();
		int linesaffected;
		try {
			//delete a product from the database using its id
			linesaffected = DBControl.statement.executeUpdate("Delete from products where product_id="+productId);
			System.out.println("Product Deleted! \nlines affected: "+linesaffected);
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	public static Employee[] getEmployees() throws Exception{
		DBControl.connect();
		try {
			ResultSet rs = DBControl.statement.executeQuery("SELECT * FROM employees");
			System.out.println("Employee list recieved from database");
			int i=0,size =0;
			//find out the number of employees
			rs.last();    // moves to the last row
			size = rs.getRow(); // get row id 
			System.out.println("size of Employee list: ["+size+"]");
			
			Employee listOfEmployees[]=new Employee[size];
			rs.beforeFirst();	//---move back to first row
			while(rs.next()) {
				//build employee list
				listOfEmployees[i++]=new Employee(rs.getInt("employee_id"),
						rs.getString("first_name"),
						rs.getString("last_name"),
						rs.getString("phone_number"),
						rs.getString("gender"),
						rs.getString("status"));
			}
			System.out.println("List of employees contructed and returned");
			return listOfEmployees;
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			DBControl.disconnect();
		}
		return null;
		//null returned just for error's sake. if all goes as planned, the array is returned, if not, an exception is thrown
	}
	public static void addNewEmployee(int employeeId, String firstName,String lastName, String phoneNumber, String gender) throws Exception{
		//build an employee object before insertion to database
		Employee newEmployee=new Employee(
				employeeId,
				firstName,
				lastName,
				phoneNumber,
				gender,
				null
				);
		DBControl.connect();
		
		int linesaffected;
		try {
			//use the insertEmployee method for ease of use
			linesaffected = DBControl.statement.executeUpdate("INSERT INTO employees VALUES ("+newEmployee.insertEmployee()+")");
			System.out.println("new employee added to database"+newEmployee.getFullName());
			System.out.println("lines affected: "+linesaffected);
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	public static void editEmployee(int employeeId,int columnIndex,String newChange) throws Exception {
		DBControl.connect();
		int linesaffected;
		try {
			System.out.println(columnIndex);
			//determine which column is meant to be updated and apply string appropriately
			String column="";
			if(columnIndex==1) column="first_name";
			else if(columnIndex==2) column="last_name";
			else if(columnIndex==3) column="phone_number";
			else if(columnIndex==4) column="gender";
			else throw new Exception("edit problem");
			//concatenate string with proper inputs
			linesaffected = DBControl.statement.executeUpdate("update employees set "+column+" = '"+newChange+"' where employee_id="+employeeId+";");
			System.out.println("lines affected: "+linesaffected);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBControl.disconnect();
		}
	}
	public static void deleteEmployee(int employeeId) throws Exception {
		DBControl.connect();
		int linesaffected;
		try {
			linesaffected = DBControl.statement.executeUpdate("Delete from employees where employee_id="+employeeId);
			System.out.println("Employee Deleted! \nlines affected: "+linesaffected);
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	public static void updateEmployeeStatus(int employeeId, String status) throws Exception {
		DBControl.connect();
		int linesAffected;
		try {
			//set employee status to be in a shift or not in a shift
			//status can be 'active' or 'inactive'
			linesAffected = DBControl.statement.executeUpdate("UPDATE employees SET status = '"+status+"' WHERE employee_id = "+employeeId);
			System.out.println("Employee status changed: "+status+"\nLines affected: "+ linesAffected);
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	public static Order[] fetchOrders() throws Exception{
		DBControl.connect();
		try {
			ResultSet rs = DBControl.statement.executeQuery("SELECT order_id,total_time,TIMESTAMPDIFF(second,added_at,current_timestamp()) AS wait_time,order_status FROM orders WHERE order_status = 'WAITING' OR order_status = 'IN_PROGRESS'");
			System.out.println("Orders recieved from database");
			int i=0,size=0;
			//find out size of result table
			rs.last();    // moves to the last row
			size = rs.getRow(); // get row id 
			System.out.println("size of Order list: ["+size+"]");
			
			Order listOfOrders[]=new Order[size];
			rs.beforeFirst();	//---move back to first row
			while(rs.next()) {
				listOfOrders[i]=new Order(rs.getInt("order_id"),
						rs.getInt("total_time"),
						rs.getInt("wait_time"),
						rs.getString("order_status"));
				//gets list of products in each specific order
				listOfOrders[i].setMyList(fetchProductsInOrder(listOfOrders[i].getOrderId()));
				//calculates the priority of each order according the the algorithm
				calculatePriority(listOfOrders[i]);
				i++;
			}
			//sort the array of orders according to their priority
			sortByPriority(listOfOrders,listOfOrders.length);
			return listOfOrders;
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	private static ArrayList<OrderItem> fetchProductsInOrder(int orderId) throws Exception{
		ArrayList<OrderItem> myList = new ArrayList<>();
		//method for getting all the items in an order, and the amount
		//the info is organized in an assisting OrderItem class
		DBControl.connect();
		try {
			ResultSet rs = DBControl.statement.executeQuery("SELECT P.*,PO.amount_in_order FROM products P, products_in_order PO WHERE P.product_id = PO. product_id AND PO.order_id = "+orderId);
			
			while(rs.next()) {
				//fill list with products and their amount
				myList.add(new OrderItem(new Product(
						rs.getInt("product_id"),
						rs.getString("product_name"),
						rs.getString("product_type"),
						rs.getInt("amount"),
						rs.getDouble("weight"),
						rs.getDouble("price"),
						rs.getString("area"),
						rs.getInt("row"),
						rs.getInt("prep_time"),
						blobToImage(rs.getBlob("image"))
						),
						rs.getInt("amount_in_order")
						)
					);
			}
			System.out.println("List of products in order contructed and returned: " +orderId);
			return myList;
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			DBControl.disconnect();
		}
		return myList;
	}
	private static void calculatePriority(Order o) {
		//time needed to prepare order
		double t=o.getTotalTime();
		//time that the order has been waiting since it was added
		double w=o.getWaitTime();
		//the t is added before dividing by t to ensure priority stays above 1
		double p = (w+t)/t;
		//save the result
		o.setPriority(p);
	}
	private static void sortByPriority(Order orders[],int ind) {
		//recursive bubble sort
		if(ind>1) {
			for(int i=0;i<ind-1;i++)
				if(orders[i].getPriority()>orders[i+1].getPriority()) {
					//swap
					Order temp=orders[i];
					orders[i]=orders[i+1];
					orders[i+1] = temp;
				}
			sortByPriority(orders,ind-1);
		}
	}
	public static void startOrder(int orderId) throws Exception {
		DBControl.connect();
		int linesAffected;
		try {
			//set an orders status to being worked on
			linesAffected = DBControl.statement.executeUpdate("UPDATE orders SET order_status = 'IN_PROGRESS', queued_at = CURRENT_TIMESTAMP() WHERE order_id = "+orderId);
			System.out.println("Order num: "+orderId+" started\nLines Affected: "+linesAffected);
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
	public static void finishOrder(int orderId) throws Exception {
		DBControl.connect();
		int linesAffected;
		try {
			//set an order's status to completed
			linesAffected = DBControl.statement.executeUpdate("UPDATE orders SET order_status = 'COMPLETED', completed_at = CURRENT_TIMESTAMP() WHERE order_id = "+orderId);
			System.out.println("Order num: "+orderId+" finished\nLines Affected: "+linesAffected);
		} catch (SQLException e) {
			throw e;
		}finally {
			DBControl.disconnect();
		}
	}
}