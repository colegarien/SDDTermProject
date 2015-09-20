package edu.uco.schambers.classmate.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataRepo extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 8;
	private static final String DATABASE_NAME = "CLASSMATE_TABLES";
	private static final String CREATE_TABLE_USER = "create table users " +
			"(user_id integer primary key AUTOINCREMENT," +
			"id integer, username text, " +
			"password text, fname text,lname text , " +
			"isstudent integer, phone text, " +
			"email text, ismale integer)";

	private static final String Role = "create table dbRoles " +
			"(user_id integer, " +
			"groups text, " +
			"primary key(user_id, groups), "+
			"CONSTRAINT user_fkey Foreign key(user_id) references users(user_id)"+
			")";

	public DataRepo(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_USER);
		db.execSQL(Role);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d("DEBUG", "Database version has changed to version: " + DATABASE_VERSION);
		db.execSQL("DROP TABLE IF EXISTS users");
		db.execSQL("DROP TABLE IF EXISTS dbRoles");
		onCreate(db);
	}

	public boolean userExist(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from users where id = '" + id + "'", null);

		if (res.getCount() > 0)
			return false;

		return true;
	}


	public void createUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("id", user.getId());
		contentValues.put("username", user.getUsername());
		contentValues.put("password", user.getPassword());
		contentValues.put("fname", user.getFname());
		contentValues.put("lname", user.getLname());
		contentValues.put("isstudent", user.isStudent() ? 1 : 0);
		contentValues.put("phone", user.getPhone());
		contentValues.put("email", user.getEmail());
		contentValues.put("ismale", user.isMale() ? 1 : 0);

		db.insert("users", null, contentValues);
		createRole(user);
	}

	public void createRole(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		if(user.isStudent()) {
			//contentValues.put("user_id", user.getId());
			contentValues.put("groups", "StudentGroup");
		}
		else{
			//contentValues.put("user_id", user.getId());
			contentValues.put("groups", "FacultyGroup");
		}

		db.insert("dbRoles", null, contentValues);
	}

	public User getUser(String username) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from users where username = '" + username + "'", null);

		if (res.moveToNext())
		{
			User user = new User();

			user.setId(res.getInt(0));
			user.setUsername(res.getString(1));
			user.setPassword(res.getString(2));
			user.setFname(res.getString(3));
			user.setLname(res.getString(4));
			user.setIsStudent(res.getInt(5) == 1);
			user.setPhone(res.getString(6));
			user.setEmail(res.getString(7));
			user.setIsMale(res.getInt(8) == 1);

			return user;
		}

		return null;
	}

	public boolean validateUser(String username, String password) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from users where username = '" + username + "' and password = '" + password + "'", null);

		if (res.getCount() > 0)
			return true;

		return false;
	}

	public boolean validateUser(String password) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "select * from users where password = '" + password + "'", null);

		if (res.getCount() > 0)
			return true;

		return false;
	}

}