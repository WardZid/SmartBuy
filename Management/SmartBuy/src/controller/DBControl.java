package controller;

import java.sql.*;
import model.*;
public class DBControl {
	public static Connection connection = null;
	//address of database we intend to connect to
	private static String dbUrl = "jdbc:mysql://localhost:3306/newschema?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=GMT";
	
	//jdbc mysql connector driver
	private static String JDBCDriver="com.mysql.cj.jdbc.Driver";
	//query object
	public static Statement statement;
	//user saved here after login
	//state of log in is determined by having the user equal null or an actual user object
	private static User user;
	//Methods
	public static void setUser(User u){user=u;}//setting user post-login
	public static User getUser(){return user;}
	public static void removeUser() {user=null;}//removing user to log off
	public static void connect()throws Exception{
	//*************connection to database
		try {
			System.out.println("***DB connection initiated***");
			Class.forName(JDBCDriver);
			System.out.println("Driver Fetched");
			connection = DriverManager.getConnection(dbUrl, "root", "0000"); //apply user+pass for database
			
			
			System.out.println("Connected");
			statement =connection.createStatement();
			System.out.println("Statement created");
		}
		catch(SQLException e) {
			//this error happens when the database couldnt be reached
			e.printStackTrace();
			throw new Exception("Please fix your internet connection");
		} 
		catch (ClassNotFoundException e) {e.printStackTrace();}
		catch (Exception e) {e.printStackTrace();}
	}
	public static void disconnect(){
	//*************disconnect from database
		try {
			connection.close();
			System.out.println("***DB connection terminated***");
		} catch (SQLException e) { System.out.println("Disconnection error"); }
	}
}