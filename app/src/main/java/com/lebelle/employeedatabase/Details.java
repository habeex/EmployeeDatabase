package com.lebelle.employeedatabase;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lebelle.employeedatabase.data.EmployeeContract.EmployeeEntry;
import com.mikhaellopez.circularimageview.CircularImageView;

import static android.R.attr.id;

public class Details extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //employee content uri
    private Uri mClickedEmployeeUri;
    private ImageButton phoneButton, emailButton, textButton;
    private CircularImageView profileImage;

    private TextView mFirstName, mLastName, mPhone, mEmail, mAddress, mEmpId, mBio, mDesgn, mDept,
    mGender, mStatus, mSalary, mBank, mTax, mAcct, mEmpDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar1 = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get the transferred data from previous activity
        Intent intent = getIntent();
        mClickedEmployeeUri = intent.getData();

        //if intent does not contain an employee content uri then we know we have no details
        if (mClickedEmployeeUri == null) {
            Toast.makeText(this, "No Employee Data", Toast.LENGTH_SHORT).show();
        }else {
            //initialize a loader
            getLoaderManager().initLoader(0, null, this);
        }

        mFirstName = (TextView)findViewById(R.id.first_name_text_view1);
        mLastName = (TextView)findViewById(R.id.last_name_text_view1);
        mAddress = (TextView)findViewById(R.id.address_text_view2);
        mBio = (TextView)findViewById(R.id.bio_text_view2);
        mGender = (TextView)findViewById(R.id.gender_text_view2);
        mStatus = (TextView)findViewById(R.id.status_text_view2);
        mEmpId = (TextView)findViewById(R.id.id_text_view2);
        mEmpDate = (TextView)findViewById(R.id.employ_date_text_view2);
        mDesgn = (TextView)findViewById(R.id.designation_text_view2);
        mDept = (TextView)findViewById(R.id.dpt_text_view2);
        mSalary = (TextView)findViewById(R.id.salary_text_view2);
        mBank = (TextView)findViewById(R.id.bank_name_text_view2);
        mTax = (TextView)findViewById(R.id.tax_text_view2);
        mAcct = (TextView)findViewById(R.id.bank_acct_text_view2);

        profileImage = (CircularImageView)findViewById(R.id.avatar1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                return true;
            case R.id.action_edit:
                //edit data input
                Intent intent = new Intent(Details.this, MainForm.class);
                Uri clickedEmployeeUri = ContentUris.withAppendedId(EmployeeEntry.CONTENT_URI, id);
                intent.setData(clickedEmployeeUri);
                startActivity(intent);
                //exit activity
                //finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
        return new CursorLoader(this,
                mClickedEmployeeUri,
                projection,
                null,
                null,
                null);
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
            String firstname = cursor.getString(firstNameColumnIndex);
            String lastname = cursor.getString(lastNameColumnIndex);
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
            mFirstName.setText(firstname);
            mLastName.setText(lastname);
            mAddress.setText(address_text);
            mEmail.setText(email_text);
            mPhone.setText(phone_text);
            mBio.setText(bio_text);
            mEmpId.setText(id_text);
            mEmpDate.setText(date_text);
            mDesgn.setText(dsn_text);
            mDept.setText(dpt_text);
           mSalary.setText(salary_text);
            mTax.setText(tax_text);
            mBank.setText(bank_text);
            mAcct.setText(acct_text);

            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            profileImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,
                    200, false));


            //spinners
            switch (gender_text){
                case EmployeeEntry.GENDER_MALE:
                    mGender.setText("Male");
                    break;
                case EmployeeEntry.GENDER_FEMALE:
                    mGender.setText("Female");
                    break;
                default:
                    mGender.setText("Unknown");
            }

            switch (status_text){
                case EmployeeEntry.STATUS_SINGLE:
                    mStatus.setText("Single");
                    break;
                case EmployeeEntry.STATUS_MARRIED:
                    mStatus.setText("Married");
                    break;
                default:
                    mStatus.setText("Unknown");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //if loader is invalidated clear out all data in input field
        mFirstName.setText("");
        mLastName.setText("");
        mAddress.setText("");
        mEmail.setText("");
        mPhone.setText("");
        mBio.setText("");
        mEmpId.setText("");
        mEmpDate.setText("");
        mDesgn.setText("");
        mDept.setText("");
        mSalary.setText("");
        mTax.setText("");
        mBank.setText("");
        mAcct.setText("");
        mGender.setText("Unknown");//unknown
        mStatus.setText("Unknown");
    }
}
