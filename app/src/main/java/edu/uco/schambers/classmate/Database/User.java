package edu.uco.schambers.classmate.Database;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class User implements Serializable {
	private int id;
	private String username;
	private String password;
	private String fname;
	private String lname;
	private boolean Student;
	private String phone;
	private String email;
	private boolean Male;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = SHA256Encrypt.encrypt(password);
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public boolean isStudent() {
		return Student;
	}

	public void setIsStudent(boolean Student) {
		this.Student = Student;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isMale() {
		return Male;
	}

	public void setIsMale(boolean Male) {
		this.Male = Male;
	}

	private static final String NAME = "[a-zA-Z ]+";
	private static final String EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	private boolean isValidName(String name) {

		Pattern pattern = Pattern.compile(NAME);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}
	// validating email id
	private boolean isValidEmail(String email) {

		Pattern pattern = Pattern.compile(EMAIL);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// validating password with retype password
	private boolean isValidPassword(String pass) {
		if (pass != null && pass.length() > 6) {
			return true;
		}
		return false;
	}

}
