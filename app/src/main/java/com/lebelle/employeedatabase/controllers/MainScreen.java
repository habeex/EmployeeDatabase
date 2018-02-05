package com.lebelle.employeedatabase.controllers;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lebelle.employeedatabase.R;
import com.lebelle.employeedatabase.data.EmployeeContract.EmployeeEntry;
import com.lebelle.employeedatabase.model.EmployeeCursorAdapter;
import com.lebelle.employeedatabase.model.RecycAdapter;

public class MainScreen extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecycAdapter.CustomItemListener{

    private static final int EMPLOYEE_LOADER = 0;

    //adapter for listview
    EmployeeCursorAdapter mCursorAdapter;

    private Uri clickedEmployeeUri;
    private RecycAdapter mAdapter;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, MainForm.class);
                startActivity(intent);
            }
        });

        final RecyclerView employeeListView = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        employeeListView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new RecycAdapter(this);
        employeeListView.setAdapter(mAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                int id = (int) viewHolder.itemView.getTag();
                String stringId = Integer.toString(id);

                Uri uri = EmployeeEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);

                getLoaderManager().restartLoader(EMPLOYEE_LOADER, null, MainScreen.this);            }
        }).attachToRecyclerView(employeeListView);


        //start loader
        getLoaderManager().initLoader(EMPLOYEE_LOADER, null, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_refresh:
                finish();
                startActivity(getIntent());
                Snackbar.make(findViewById(R.id.main_layout), "Refreshed!", Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).setActionTextColor(Color.YELLOW).show();
                return true;
            case R.id.action_search:
                //search all data input for an employee
                return true;
            case R.id.action_export:
                //export all data input to excel
                return true;
            case R.id.action_clear:
                //delete all data input
                showDeleteAllConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_delete_all);
        builder.setMessage(R.string.delete_all_dialog_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllEmployees();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllEmployees() {
        int data = getContentResolver().delete(EmployeeEntry.CONTENT_URI, null, null);
        if (data != -1) {
            Snackbar.make(findViewById(R.id.main_layout), "All Employees successfully deleted!", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).setActionTextColor(Color.YELLOW).show();
        } else {
            Snackbar.make(findViewById(R.id.main_layout), "Failed to delete all employees, please try again.", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).setActionTextColor(Color.YELLOW).show();
        }
    }


    private void deleteEmployee() {
        int rowsDeleted = getContentResolver().delete(EmployeeEntry.CONTENT_URI, null, null);

        if (rowsDeleted == 0) {
            Snackbar.make(findViewById(R.id.main_layout), R.string.delete_error, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).setActionTextColor(Color.YELLOW).show();
        } else {
            Snackbar.make(findViewById(R.id.main_layout), R.string.delete_success, Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).setActionTextColor(Color.YELLOW).show();
        }

    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
                EmployeeEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //mCursorAdapter.swapCursor(cursor);
        mAdapter.swapCursor(cursor);
        mCursor = cursor;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //mCursorAdapter.swapCursor(null);
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int position) {

        mCursor.moveToPosition(position);

        int id = mCursor.getInt(mCursor.getColumnIndex(EmployeeEntry._ID));
        //go to employee details page
        Intent intent = new Intent(MainScreen.this, Details.class);
        clickedEmployeeUri = ContentUris.withAppendedId(EmployeeEntry.CONTENT_URI, id);
        intent.setData(clickedEmployeeUri);
        startActivity(intent);

    }

    @Override
    public void onLongClick(int position) {
        mCursor.moveToPosition(position);
        final int id = mCursor.getInt(mCursor.getColumnIndex(EmployeeEntry._ID));
        Toast.makeText(getApplicationContext(), mCursor.getString(mCursor.getColumnIndex(EmployeeEntry.COLUMN_FIRST_NAME)), Toast.LENGTH_LONG).show();

        AlertDialog.Builder fixDialog = new AlertDialog.Builder(MainScreen.this);
        fixDialog.setTitle("Select Action");
        String[] fixDialogItems = {
                "Update Employee",
                "Delete Employee"};
        fixDialog.setItems(fixDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    Intent intent = new Intent(MainScreen.this, MainForm.class);
                    Uri clickedEmployeeUri = ContentUris.withAppendedId(EmployeeEntry.CONTENT_URI, id);
                    intent.setData(clickedEmployeeUri);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainScreen.this);
                    deleteDialog.setTitle("Delete");
                    deleteDialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //deleteEmployee(employeeId);
                            //deleteEmployee();

                            String stringId = Integer.toString(id);
                            Uri uri = EmployeeEntry.CONTENT_URI;
                            uri = uri.buildUpon().appendPath(stringId).build();
                            getContentResolver().delete(uri, null, null);
                        }
                    });

                    deleteDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (dialogInterface != null) {
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    deleteDialog.show();
                }
            }
        });
        fixDialog.show();
    }
}
