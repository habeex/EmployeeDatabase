package com.lebelle.employeedatabase.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by HP on 23-Sep-17.
 */

public final class EmployeeContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private EmployeeContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.lebelle.employeedatabase";

    //use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //possible path
    public static final String PATH_EMPLOYEES = "employees-path";

    /**
     * Inner class that defines constant values for the employees database table.
     * Each entry in the table represents a single employee.
     */
    public static final class EmployeeEntry implements BaseColumns {

        //THE MIME TYPE OF THE {@link #CONTENT_URI} for a list of employees
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_EMPLOYEES;
        //THE MIME TYPE OF THE {@link #CONTENT_URI} for a single employee
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EMPLOYEES;

        /**
         * the content URI to access the employees data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EMPLOYEES);

        /**
         * Name of database table for employees
         */
        public final static String TABLE_NAME = "employees";

        /**
         * Unique ID number for the employee (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the employee.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_FIRST_NAME = "firstname";
        public final static String COLUMN_LAST_NAME = "lastname";
        public final static String COLUMN_DESIGNATION = "designation";
        public final static String COLUMN_DEPARTMENT = "department";
        public final static String COLUMN_ADDRESS = "address";
        public final static String COLUMN_PHONE = "phone";
        public final static String COLUMN_EMAIL = "email";
        public final static String COLUMN_EMPLOYEE_IDE = "identity";
        public final static String COLUMN_EMPLOYMENT_DATE = "date";
        public final static String COLUMN_EMPLOYEE_SALARY = "salary";
        public final static String COLUMN_BIO = "bio";
        public final static String COLUMN_TAX = "tax";
        public final static String COLUMN_BANK = "bank";
        public final static String COLUMN_ACCT = "acct";
        public final static String COLUMN_IMAGE = "image";

        /**
         * Gender of the employee.
         * <p>
         * The only possible values are {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_EMPLOYEE_GENDER = "gender";

        /**
         * Possible values for the gender of the employee.
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }

        /**
         * Status of the employee.
         * <p>
         * The only possible values are {@link #STATUS_UNKNOWN}, {@link #STATUS_SINGLE},
         * or {@link #STATUS_MARRIED}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_EMPLOYEE_STATUS = "status";

        /**
         * Possible values for the gender of the employee.
         */
        public static final int STATUS_UNKNOWN = 0;
        public static final int STATUS_SINGLE = 1;
        public static final int STATUS_MARRIED = 2;

        /**
         * Returns whether or not the given gender is {@link #STATUS_UNKNOWN}, {@link #STATUS_SINGLE},
         * or {@link #STATUS_MARRIED}.
         */
        public static boolean isValidStatus(int status) {
            if (status == STATUS_UNKNOWN || status == STATUS_SINGLE || status == STATUS_MARRIED) {
                return true;
            }
            return false;
        }
    }
}

