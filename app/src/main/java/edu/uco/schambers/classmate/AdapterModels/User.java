package edu.uco.schambers.classmate.AdapterModels;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nelson.
 */

public class User implements Serializable {
	private int pKey;
	private int id;
	private String name;
	private String password;
	private boolean Student;
	private String email;

	public int getpKey(){return pKey;}

	public void setpKey(int pKey) {this.pKey = pKey;}

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


	//Regex
	private static final String NAME = "[a-zA-Z ]+";
	private static final String EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	//validate string using regex
	public static boolean isValidName(String name) {
		Pattern pattern = Pattern.compile(NAME);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}
	// validating email address using regex
	public static boolean isValidEmail(String email) {
		Pattern pattern = Pattern.compile(EMAIL);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// validating password with retype password
	public static boolean isValidPassword(String pass) {
		if (!pass.equals("") && pass.length() > 7) {
			return true;
		}
		return false;
	}

}
