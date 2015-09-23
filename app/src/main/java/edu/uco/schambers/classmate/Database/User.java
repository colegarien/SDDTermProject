package edu.uco.schambers.classmate.Database;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class User implements Serializable {
	private int id;
	private String name;
	private String password;
	private boolean Student;
	private String email;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isStudent() {
		return Student;
	}

	public void setIsStudent(boolean Student) {
		this.Student = Student;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private static final String NAME = "[a-zA-Z ]+";
	private static final String EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public boolean isValidName(String name) {

		Pattern pattern = Pattern.compile(NAME);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}
	// validating email id
	public boolean isValidEmail(String email) {

		Pattern pattern = Pattern.compile(EMAIL);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// validating password with retype password
	public boolean isValidPassword(String pass) {
		if (!pass.equals("") && pass.length() < 5) {
			return true;
		}
		return false;
	}

}
