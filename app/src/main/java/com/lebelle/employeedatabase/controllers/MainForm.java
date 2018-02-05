package com.lebelle.employeedatabase.controllers;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.lebelle.employeedatabase.R;
import com.lebelle.employeedatabase.Utils;
import com.lebelle.employeedatabase.data.EmployeeContract.EmployeeEntry;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainForm extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int NEW_EMPLOYEE_LOADER = 0;

    private ImageButton btn;
    private CircularImageView circularImageView;
    private static final String EMPLOYEE_IMAGE_DIRECTORY = "/lebelleami";
    private static final int GALLERY = 1, CAMERA = 2;

    //employee content uri
    private Uri mClickedEmployeeUri;

    private Uri employeeUri;

    private Spinner mGenderSpinner, mStatusSpinner;
    /**
     * Gender for employees, 0 for Male, 1 for female
     */
    private int mGender = EmployeeEntry.GENDER_UNKNOWN;

    /**
     * Marital status for employees, 0 for Single, 1 for Married
     */
    private int mStatus = EmployeeEntry.STATUS_UNKNOWN;

    private boolean mEmployeeHasChanged = false;
    private boolean hasImageChanged = false;

    private TextInputEditText first_name,last_name, address, email, phone, emp_id, emp_date, desgn, dept,
            salary, bio, bank, tax, acct;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mEmployeeHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mEmployeeHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_form);
        Toolbar toolbar1 = findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //get the transferred data from previous activity
        Intent intent = getIntent();
        mClickedEmployeeUri = intent.getData();

        Intent detailsIntent = getIntent();
        employeeUri = detailsIntent.getData();

        mGenderSpinner = findViewById(R.id.gender);
        mStatusSpinner = findViewById(R.id.status);
        first_name = findViewById(R.id.name_text_view);
        last_name = findViewById(R.id.last_name_text_view);
        address = findViewById(R.id.address_text_view);
        email = findViewById(R.id.email_text_view);
        phone = findViewById(R.id.phone_text_view);
        emp_id = findViewById(R.id.id_text_view);
        emp_date = findViewById(R.id.employ_date_text_view);
        desgn = findViewById(R.id.dsn_text_view);
        dept = findViewById(R.id.dpt_text_view);
        salary = findViewById(R.id.salary_text_view);
        bio = findViewById(R.id.bio_text_view);
        bank = findViewById(R.id.bank_name_text_view);
        tax = findViewById(R.id.tax_text_view);
        acct = findViewById(R.id.bank_acct_text_view);

        circularImageView = findViewById(R.id.avatar);
        btn = findViewById(R.id.change_avatar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
                mEmployeeHasChanged = true;
            }
        });

        if (mClickedEmployeeUri == null){
            setTitle(R.string.add_employee);
            invalidateOptionsMenu();
        } else if (employeeUri == null) {
            setTitle(R.string.add_employee);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_employee);
        }

        first_name.setOnTouchListener(mTouchListener);
        last_name.setOnTouchListener(mTouchListener);
        address.setOnTouchListener(mTouchListener);
        email.setOnTouchListener(mTouchListener);
        phone.setOnTouchListener(mTouchListener);
        emp_id.setOnTouchListener(mTouchListener);
        emp_date.setOnTouchListener(mTouchListener);
        desgn.setOnTouchListener(mTouchListener);
        dept.setOnTouchListener(mTouchListener);
        salary.setOnTouchListener(mTouchListener);
        bio.setOnTouchListener(mTouchListener);
        bank.setOnTouchListener(mTouchListener);
        tax.setOnTouchListener(mTouchListener);
        acct.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
        mStatusSpinner.setOnTouchListener(mTouchListener);


    setupGenderSpinner();
        setupStatusSpinner();

        //start loader
        getLoaderManager().initLoader(NEW_EMPLOYEE_LOADER, null, this);

    }

    //set up spinner selection
    private void setupGenderSpinner(){
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> genderArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        genderArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderArrayAdapter);
        addListenerOnGenderSpinner();
    }

    public void addListenerOnGenderSpinner() {
        // This method is supposed to call the on item selected listener on the spinner class
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//this method gets called automatically when the user selects an item so we need to retrieve what the use has clicked
                String gender = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(gender)){
                    if (gender.equals(getString(R.string.gender_male))){
                        mGender = EmployeeEntry.GENDER_MALE;//Male
                    }else if(gender.equals(getString(R.string.gender_female))){
                        mGender = EmployeeEntry.GENDER_FEMALE;//Male  }
                }else {mGender= EmployeeEntry.GENDER_UNKNOWN;
                }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = EmployeeEntry.GENDER_UNKNOWN;
            }
        });
    }
    //set up status spinner selection
    private void setupStatusSpinner(){
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_status_options, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //apply the adapter to the spinner
        mStatusSpinner.setAdapter(adapter);
        addListenerOnStatusSpinner();
    }

    public void addListenerOnStatusSpinner() {
        // This method is supposed to call the on item selected listener on the spinner class
        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//this method gets called automatically when the user selects an item so we need to retrieve what the use has clicked
                String status = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(status)){
                    if (status.equals(getString(R.string.status_single))){
                        mStatus = EmployeeEntry.STATUS_SINGLE;//Single
                    }else if(status.equals(getString(R.string.status_married))){
                        mStatus = EmployeeEntry.STATUS_MARRIED;//Married  }                }
            }else {mStatus= EmployeeEntry.STATUS_UNKNOWN;
            }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                mStatus = EmployeeEntry.STATUS_UNKNOWN;
            }
        });
    }
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Image Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera", "Cancel"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    boolean result = Utils.checkPermission(MainForm.this);
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                            case 2:
                                circularImageView.setImageResource(R.drawable.user);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //saveImage(bitmap);
                    Toast.makeText(MainForm.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    circularImageView.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainForm.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            circularImageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(MainForm.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + EMPLOYEE_IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    /**
     * user input method to insert new employee into the database
     *
     */
    private void insertEmployee(){
        String firstNameString = first_name.getText().toString().trim();
        String lastNameString = last_name.getText().toString().trim();
        String addressString = address.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String phoneString = phone.getText().toString().trim();
        String employeeIdString = emp_id.getText().toString().trim();
        String employeeDsnString = desgn.getText().toString().trim();
        String employeeDptString = dept.getText().toString().trim();
        String employmentDteString = emp_date.getText().toString().trim();
        String salaryString = salary.getText().toString().trim();
        String bioString = bio.getText().toString().trim();
        String bankString = bank.getText().toString().trim();
        String taxString = tax.getText().toString().trim();
        String acctString = acct.getText().toString().trim();

        //save image in database
        circularImageView.setDrawingCacheEnabled(true);
        circularImageView.buildDrawingCache();
        Bitmap bitmap = circularImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Check if this is supposed to be a new employee
        // and check if all the fields in the editor are blank
        if (mClickedEmployeeUri == null &&
                TextUtils.isEmpty(firstNameString) && TextUtils.isEmpty(lastNameString) &&
                TextUtils.isEmpty(addressString) && TextUtils.isEmpty(emailString) &&
                TextUtils.isEmpty(employeeIdString) && TextUtils.isEmpty(phoneString) &&
                TextUtils.isEmpty(employeeDsnString) && TextUtils.isEmpty(employeeDptString)
                &&  TextUtils.isEmpty(employmentDteString)
                && TextUtils.isEmpty(salaryString) && TextUtils.isEmpty(bioString)&& TextUtils.isEmpty(bankString)
                && TextUtils.isEmpty(taxString)&& TextUtils.isEmpty(acctString)&& mStatus == EmployeeEntry.STATUS_UNKNOWN
                && mGender == EmployeeEntry.GENDER_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new employee.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        //create a contentValues Object where column names are the keys
        //and employee details from the form are the values
        ContentValues values = new ContentValues();
        values.put(EmployeeEntry.COLUMN_FIRST_NAME, firstNameString);
        values.put(EmployeeEntry.COLUMN_LAST_NAME,lastNameString);
        values.put(EmployeeEntry.COLUMN_ADDRESS, addressString);
        values.put(EmployeeEntry.COLUMN_EMAIL, emailString);
        values.put(EmployeeEntry.COLUMN_PHONE, phoneString);
        values.put(EmployeeEntry.COLUMN_EMPLOYEE_IDE, employeeIdString);
        values.put(EmployeeEntry.COLUMN_DESIGNATION, employeeDsnString);
        values.put(EmployeeEntry.COLUMN_DEPARTMENT, employeeDptString);
        values.put(EmployeeEntry.COLUMN_EMPLOYMENT_DATE, employmentDteString);
        values.put(EmployeeEntry.COLUMN_EMPLOYEE_SALARY, salaryString);
        values.put(EmployeeEntry.COLUMN_BIO, bioString);
        values.put(EmployeeEntry.COLUMN_BANK, bankString);
        values.put(EmployeeEntry.COLUMN_TAX, taxString);
        values.put(EmployeeEntry.COLUMN_ACCT, acctString);
        values.put(EmployeeEntry.COLUMN_IMAGE, Utils.getPictureByteOfArray(bitmap));
        values.put(EmployeeEntry.COLUMN_EMPLOYEE_GENDER, mGender);
        values.put(EmployeeEntry.COLUMN_EMPLOYEE_STATUS, mStatus);

        if(mClickedEmployeeUri == null){
            //insert a new row for employee and return the id of that new row
            Uri newUriRow = getContentResolver().insert(EmployeeEntry.CONTENT_URI, values);
            //make toast based on success
            if (newUriRow == null){
                Toast.makeText(this, "Error while saving Employee data", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Employee data saved with row id:" + newUriRow, Toast.LENGTH_SHORT).show();
            }

        } else {
            // Otherwise this is an EXISTING employee, so update the employee with content URI: mCurrentEmployeeUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentEmployeeUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mClickedEmployeeUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Update employee failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Update employee successful",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        if (mClickedEmployeeUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mEmployeeHasChanged) {
                    finish(); // close this activity and return to preview activity (if there is any)
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                                finish();
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_save:
                //save data input
                insertEmployee();
                //exit activity
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener(){
           public void onClick(DialogInterface dialog, int id){
               deleteEmployee();
           }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
           public void onClick (DialogInterface dialog, int id){
               if (dialog != null){
                   dialog.dismiss();
               }
           }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteEmployee() {
        if (mClickedEmployeeUri != null){
            int rowsDeleted = getContentResolver().delete(mClickedEmployeeUri, null, null);

            if (rowsDeleted == 0){
                Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case NEW_EMPLOYEE_LOADER:
                //Define a projection that specifies which columns from the database
                //you will actually use for the query
                String[] projection = {
                        EmployeeEntry._ID,
                        EmployeeEntry.COLUMN_FIRST_NAME,
                        EmployeeEntry.COLUMN_LAST_NAME,
                        EmployeeEntry.COLUMN_ADDRESS,
                        EmployeeEntry.COLUMN_EMAIL,
                        EmployeeEntry.COLUMN_PHONE,
                        EmployeeEntry.COLUMN_EMPLOYEE_GENDER,
                        EmployeeEntry.COLUMN_EMPLOYEE_STATUS,
                        EmployeeEntry.COLUMN_EMPLOYEE_IDE,
                        EmployeeEntry.COLUMN_EMPLOYMENT_DATE,
                        EmployeeEntry.COLUMN_DESIGNATION,
                        EmployeeEntry.COLUMN_DEPARTMENT,
                        EmployeeEntry.COLUMN_EMPLOYEE_SALARY,
                        EmployeeEntry.COLUMN_BANK,
                        EmployeeEntry.COLUMN_ACCT,
                        EmployeeEntry.COLUMN_TAX,
                        EmployeeEntry.COLUMN_BIO,
                        EmployeeEntry.COLUMN_IMAGE};
                //this loader will execute the content provider query method on a background thread
                if (mClickedEmployeeUri != null) {
                    return new CursorLoader(this,
                            mClickedEmployeeUri,
                            projection,
                            null,
                            null,
                            null);
                }
            default:
                return null;
        }

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //bail early if the cursor is null or there is less than one row
        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        if (cursor.moveToFirst()){
            //find the columns of employee attributes that we're interested in
            int firstNameColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_FIRST_NAME);
            int lastNameColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_LAST_NAME);
            int addressColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_ADDRESS);
            int emailColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMAIL);
            int phoneColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_PHONE);
            int bioColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_BIO);
            int genderColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
            int statusColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_STATUS);
            int idColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_IDE);
            int dateColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYMENT_DATE);
            int dsnColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_DESIGNATION);
            int dptColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_DEPARTMENT);
            int salaryColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_SALARY);
            int taxColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_TAX);
            int bankColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_BANK);
            int acctColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_ACCT);
            int imageColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_IMAGE);

            //extract the values from the cursor
            String firstname_text = cursor.getString(firstNameColumnIndex);
            String lastname_text = cursor.getString(lastNameColumnIndex);
            String address_text = cursor.getString(addressColumnIndex);
            String email_text = cursor.getString(emailColumnIndex);
            String phone_text = cursor.getString(phoneColumnIndex);
            String bio_text = cursor.getString(bioColumnIndex);
            int gender_text = cursor.getInt(genderColumnIndex);
            int status_text = cursor.getInt(statusColumnIndex);
            String id_text = cursor.getString(idColumnIndex);
            String date_text = cursor.getString(dateColumnIndex);
            String dsn_text = cursor.getString(dsnColumnIndex);
            String dpt_text = cursor.getString(dptColumnIndex);
            String salary_text = cursor.getString(salaryColumnIndex);
            String tax_text = cursor.getString(taxColumnIndex);
            String bank_text = cursor.getString(bankColumnIndex);
            String acct_text = cursor.getString(acctColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);

            //update the database with the new values
            first_name.setText(firstname_text);
            last_name.setText(lastname_text);
            address.setText(address_text);
            email.setText(email_text);
            phone.setText(phone_text);
            bio.setText(bio_text);
            emp_id.setText(id_text);
            emp_date.setText(date_text);
            desgn.setText(dsn_text);
            dept.setText(dpt_text);
            salary.setText(salary_text);
            tax.setText(tax_text);
            bank.setText(bank_text);
            acct.setText(acct_text);
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);

            circularImageView.setImageBitmap(Utils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(EmployeeEntry.COLUMN_IMAGE))));

            //spinners
            switch (gender_text){
                case EmployeeEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case EmployeeEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
            }

            switch (status_text){
                case EmployeeEntry.STATUS_SINGLE:
                    mStatusSpinner.setSelection(1);
                    break;
                case EmployeeEntry.STATUS_MARRIED:
                    mStatusSpinner.setSelection(2);
                    break;
                default:
                    mStatusSpinner.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//if loader is invalidated clear out all data in input field
        first_name.setText("");
        last_name.setText("");
        address.setText("");
        email.setText("");
        phone.setText("");
        bio.setText("");
        emp_id.setText("");
        emp_date.setText("");
        desgn.setText("");
        dept.setText("");
        salary.setText("");
        tax.setText("");
        bank.setText("");
        acct.setText("");
        mGenderSpinner.setSelection(0);//unknown
        mStatusSpinner.setSelection(0);
    }
}




