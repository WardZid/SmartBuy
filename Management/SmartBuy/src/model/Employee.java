package model;

public class Employee {
	public enum Gender{
		Male,
		Female
	}
	//attributes
	private int employeeId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private Gender gender;
	private String status;
	//constructors
	public Employee(int employeeId, String firstName, String lastName, String phoneNumber, String gender, String status) {
		setEmployeeId(employeeId);
		setFirstName(firstName);
		setLastName(lastName);
		setPhoneNumber(phoneNumber);
		setGender(gender);
		setStatus(status);
	}
	//getters and setters
	public int getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFullName() {
		return firstName+" "+lastName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getGender() {
		return gender.toString();
	}
	public void setGender(String gender) {
		this.gender = Gender.valueOf(gender);
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status=status;
	}
	//methods

	//method that returns if a worker is currently in a shift or not
	public boolean isActive() {
		if(status.equals("active")) return true;
		return false;
	}
	//used for inserting a new employee into te database
	public String insertEmployee() {
		return employeeId+",'"+firstName+"','"+lastName+"','"+phoneNumber+"','"+gender+"','"+status+"'";
	}
	//toString used for presenting the employee info to the manager
	public String toString() {
		return 	"\n  Employee ID:\t"+employeeId
				+"\n\n  Employee Name:\t"+firstName
				+" "+ lastName
				+"\n\n  Phone:\t\t"+phoneNumber
				+"\n\n  Gender:\t\t"+gender
				+"\n\n  Status:\t\t"+status;
	}
}
