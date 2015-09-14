package edu.uco.schambers.classmate.Database;

import java.io.Serializable;


public class User implements Serializable {
	public int id;
	public String username;
	public String password;
	public String fname;
	public String lname;
	public boolean Student;
	public boolean faculty;
	public String phone;
	public String email;
	public boolean Male;

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

	public boolean isFaculty() {
		return faculty;
	}

	public void setIsStaff(boolean Staff) {
		this.faculty = Staff;
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
