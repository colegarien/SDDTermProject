package edu.uco.schambers.classmate.Database;

import java.io.Serializable;


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

}
