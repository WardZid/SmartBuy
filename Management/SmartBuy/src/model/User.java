package model;

public class User {
	//type of manager
	//used for determining ui class after login
	//used for creating a new user
	public enum UserType {
		Manager,ShiftManager;
	}
	//attributes
	private String id;
	private String first_name;
	private String last_name;
	private UserType type;
	//constructors
	public User(String id,String first_name,String last_name,String type) {
		setId(id);
		setFirst_name(first_name);
		setLast_name(last_name);
		setType(UserType.valueOf(type));
		//this constructor takes the type of user in string form and converts it into an enum value
	}
	//Getters and Setters
	public UserType getType() {return type;}
	private void setType(UserType type) {this.type = type;}
	public String getId() {return id;}
	private void setId(String id) {this.id = id;}
	public String getFirst_name() {return first_name;}
	private void setFirst_name(String first_name) {this.first_name = first_name;}
	public String getLast_name() {return last_name;}
	private void setLast_name(String last_name) {this.last_name = last_name;}
	//Methods
	@Override
	public boolean equals(Object o) {
		//checks if same person
		User u=(User)o;
		if(u.id.equals(this.id))
			if(u.first_name.equals(this.first_name))
				if(u.last_name.equals(this.last_name))
					return true;
		return false;
	}
}
