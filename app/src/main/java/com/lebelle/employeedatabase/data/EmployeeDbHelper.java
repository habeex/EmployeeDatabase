package com.lebelle.employeedatabase.data;

/**
 * Created by HP on 25-Sep-17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lebelle.employeedatabase.data.EmployeeContract.EmployeeEntry;

/**
 * Database helper for employees app. Manages database creation and version management.
 */
public class EmployeeDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = EmployeeDbHelper.class.getSimpleName();
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "employees.db";
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link EmployeeDbHelper}. * * @param context of the app
     */
    public EmployeeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the employees table
        String SQL_CREATE_EMPLOYEES_TABLE = "CREATE TABLE " +
                EmployeeEntry.TABLE_NAME + "(" +
                EmployeeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EmployeeEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_ADDRESS + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_EMAIL + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_PHONE + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_EMPLOYEE_IDE + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_DESIGNATION + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_DEPARTMENT + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_EMPLOYMENT_DATE + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_EMPLOYEE_SALARY + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_BIO + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_BANK + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_ACCT + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_TAX + " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_IMAGE + " BLOB NOT NULL, "
                + EmployeeEntry.COLUMN_EMPLOYEE_GENDER + " INTEGER NOT NULL, "
                + EmployeeEntry.COLUMN_EMPLOYEE_STATUS + " INTEGER NOT NULL " + ");";
        //Execute the SQL statement
        db.execSQL(SQL_CREATE_EMPLOYEES_TABLE);
    }
    /** * This is called when the database needs to be upgraded. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
        db.execSQL("DROP TABLE IF EXISTS " + EmployeeEntry.TABLE_NAME);
        onCreate(db);
    }
}
