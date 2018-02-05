package com.lebelle.employeedatabase.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.lebelle.employeedatabase.data.EmployeeContract.EmployeeEntry;

/**
 * Created by HP on 25-Sep-17.
 */

/**
 * {@link ContentProvider} for Employee Database app.
 */

public class EmployeeProvider extends ContentProvider{
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = EmployeeProvider.class.getSimpleName();

    private static final int EMPLOYEES = 100;
    private static final int EMPLOYEES_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(EmployeeContract.CONTENT_AUTHORITY, EmployeeContract.PATH_EMPLOYEES, EMPLOYEES);
        sUriMatcher.addURI(EmployeeContract.CONTENT_AUTHORITY, EmployeeContract.PATH_EMPLOYEES + "/#", EMPLOYEES_ID);
    }


    /** Database Helper Object*/
    private EmployeeDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new EmployeeDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //this cursor will hold the result of the query
        Cursor cursor;

        //figure out if the Uri Matcher can match the Uri  to a specific code
        int match = sUriMatcher.match(uri);
        switch (match){
            case EMPLOYEES:
               cursor = database.query(EmployeeEntry.TABLE_NAME, projection, selection, selectionArgs,
                       null, null, sortOrder);
                break;
            case EMPLOYEES_ID:
                selection = EmployeeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(EmployeeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case EMPLOYEES:
                return insertEmployee(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertEmployee(Uri uri, ContentValues values){
        String firstname = values.getAsString(EmployeeEntry.COLUMN_FIRST_NAME);
        if (firstname == null){
            throw new IllegalArgumentException("Employee requires a first name");
        }

        String lastname = values.getAsString(EmployeeEntry.COLUMN_LAST_NAME);
        if (lastname == null){
            throw new IllegalArgumentException("Employee requires a last name");
        }
        String address = values.getAsString(EmployeeEntry.COLUMN_ADDRESS);
        if (address == null){
            throw new IllegalArgumentException("Employee requires an address");
        }
        String email = values.getAsString(EmployeeEntry.COLUMN_EMAIL);
        if (email == null){
            throw new IllegalArgumentException("Employee requires an email address");
        }
        String phone = values.getAsString(EmployeeEntry.COLUMN_PHONE);
        if (phone == null){
            throw new IllegalArgumentException("Employee requires a phone number");
        }
        String emp_id = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_IDE);
        if (emp_id == null){
            throw new IllegalArgumentException("Employee requires an ID No");
        }
        String emp_dsn = values.getAsString(EmployeeEntry.COLUMN_DESIGNATION);
        if (emp_dsn == null){
            throw new IllegalArgumentException("Employee requires a designation");
        }
        String emp_date = values.getAsString(EmployeeEntry.COLUMN_EMPLOYMENT_DATE);
        if (emp_date == null){
            throw new IllegalArgumentException("Employee requires an employment date");
        }
        String emp_dpt = values.getAsString(EmployeeEntry.COLUMN_DEPARTMENT);
        if (emp_dpt == null){
            throw new IllegalArgumentException("Employee requires a department");
        }
        String salary = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_SALARY);
        if (salary == null){
            throw new IllegalArgumentException("Employee requires payment");
        }
        String bio = values.getAsString(EmployeeEntry.COLUMN_BIO);
        if (bio == null){
            throw new IllegalArgumentException("Employee requires a bio");
        }
        String tax = values.getAsString(EmployeeEntry.COLUMN_TAX);
        if (tax == null){
            throw new IllegalArgumentException("Employee requires tax payment");
        }
        String bank = values.getAsString(EmployeeEntry.COLUMN_BANK);
        if (bank == null){
            throw new IllegalArgumentException("Employee requires a bank name");
        }
        String acct = values.getAsString(EmployeeEntry.COLUMN_ACCT);
        if (acct == null){
            throw new IllegalArgumentException("Employee requires a bank acct");
        }
        Integer gender = values.getAsInteger(EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
        if (gender == null ||!EmployeeEntry.isValidGender(gender)){
            throw new IllegalArgumentException("Employee requires a valid gender");
        }
        Integer status = values.getAsInteger(EmployeeEntry.COLUMN_EMPLOYEE_STATUS);
        if (status == null ||!EmployeeEntry.isValidStatus(status)){
            throw new IllegalArgumentException("Employee requires a valid status");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //insert the new employee with the given values
        long id = database.insert(EmployeeEntry.TABLE_NAME, null, values);
        //if the ID is -1, then insertion failed, log error
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //notify all listeners that data changed for the employee content uri
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case EMPLOYEES:
                return updateEmployee(uri, contentValues, selection, selectionArgs);
            case EMPLOYEES_ID:

                selection = EmployeeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateEmployee(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for" + uri);

        }
    }

    //update employees in the database with the given content values
    private int updateEmployee(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if (values.containsKey(EmployeeEntry.COLUMN_FIRST_NAME)){
            String firstname = values.getAsString(EmployeeEntry.COLUMN_FIRST_NAME);
            if (firstname == null){
                throw new IllegalArgumentException("Employee requires a first name");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_LAST_NAME)){
            String lastname = values.getAsString(EmployeeEntry.COLUMN_LAST_NAME);
            if (lastname == null){
                throw new IllegalArgumentException("Employee requires a last name");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_ADDRESS)){
            String address = values.getAsString(EmployeeEntry.COLUMN_ADDRESS);
            if (address == null){
                throw new IllegalArgumentException("Employee requires an address");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_EMAIL)){
            String email = values.getAsString(EmployeeEntry.COLUMN_EMAIL);
            if (email == null){
                throw new IllegalArgumentException("Employee requires an email address");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_PHONE)){
            String phone = values.getAsString(EmployeeEntry.COLUMN_PHONE);
            if (phone == null){
                throw new IllegalArgumentException("Employee requires a phone number");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_IDE)){
            String emp_id = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_IDE);
            if (emp_id == null){
                throw new IllegalArgumentException("Employee requires an ID");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_DESIGNATION)){
            String emp_dsn = values.getAsString(EmployeeEntry.COLUMN_DESIGNATION);
            if (emp_dsn == null){
                throw new IllegalArgumentException("Employee requires a designation");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_DEPARTMENT)){
            String emp_dpt = values.getAsString(EmployeeEntry.COLUMN_DEPARTMENT);
            if (emp_dpt == null){
                throw new IllegalArgumentException("Employee requires a department");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_EMPLOYMENT_DATE)){
            String emp_date = values.getAsString(EmployeeEntry.COLUMN_EMPLOYMENT_DATE);
            if (emp_date == null){
                throw new IllegalArgumentException("Employee requires an employment date");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_SALARY)){
            String salary = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_SALARY);
            if (salary == null){
                throw new IllegalArgumentException("Employee requires a salary");
            }
        }
            if (values.containsKey(EmployeeEntry.COLUMN_BIO)){
                String bio = values.getAsString(EmployeeEntry.COLUMN_BIO);
                if (bio == null){
                    throw new IllegalArgumentException("Employee requires a department");
                }
            }
                if (values.containsKey(EmployeeEntry.COLUMN_BANK)){
                    String bank = values.getAsString(EmployeeEntry.COLUMN_BANK);
                    if (bank == null){
                        throw new IllegalArgumentException("Employee requires a bank");
                    }
                }
                    if (values.containsKey(EmployeeEntry.COLUMN_ACCT)){
                        String acct = values.getAsString(EmployeeEntry.COLUMN_ACCT);
                        if (acct == null){
                            throw new IllegalArgumentException("Employee requires an account");
                        }
                    }
                        if (values.containsKey(EmployeeEntry.COLUMN_TAX)){
                            String tax = values.getAsString(EmployeeEntry.COLUMN_TAX);
                            if (tax == null){
                                throw new IllegalArgumentException("Employee requires a tax number");
                            }
                        }
        if (values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_GENDER)){
            Integer gender = values.getAsInteger(EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
            if (gender == null){
                throw new IllegalArgumentException("Employee requires a gender");
            }
        }
        if (values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_STATUS)){
            Integer status = values.getAsInteger(EmployeeEntry.COLUMN_EMPLOYEE_STATUS);
            if (status == null){
                throw new IllegalArgumentException("Employee requires a status");
            }
        }
        //if there is no value to update
        if (values.size() == 0){
            return 0;
        }

        //otherwise get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(EmployeeEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case EMPLOYEES:
                rowsDeleted = database.delete(EmployeeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EMPLOYEES_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID

                selection = EmployeeEntry._ID + "=?";
                selectionArgs = new String[]{id};
                rowsDeleted = database.delete(EmployeeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case EMPLOYEES:
                return EmployeeEntry.CONTENT_LIST_TYPE;
            case EMPLOYEES_ID:
                return EmployeeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + "with match " + match);
        }

    }

}
