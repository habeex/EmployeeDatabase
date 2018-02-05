package com.lebelle.employeedatabase.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lebelle.employeedatabase.R;
import com.lebelle.employeedatabase.data.EmployeeContract.EmployeeEntry;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by Omawumi Eyekpimi on 26-Sep-17.
 */

public class EmployeeCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new {@link EmployeeCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public EmployeeCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
// Find individual views that we want to modify in the list item layout
        TextView firstnameTextView = view.findViewById(R.id.one_name_text_view);
        TextView lastnameTextView = view.findViewById(R.id.two_name_text_view);
        TextView designationTextView = view.findViewById(R.id.list_designation_text_view);
        CircularImageView userImage = view.findViewById(R.id.avatar);

        // Find the columns of pet attributes that we're interested in
        int firstnameColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_FIRST_NAME);
        int lastnameColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_LAST_NAME);
        int designationColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_DESIGNATION);
        int imageColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_IMAGE);

        // Read the pet attributes from the Cursor for the current pet
        String employeeFirstName = cursor.getString(firstnameColumnIndex);
        String employeeLastName = cursor.getString(lastnameColumnIndex);
        String employeeDesignation = cursor.getString(designationColumnIndex);
        byte[] image = cursor.getBlob(imageColumnIndex);

        // Update the TextViews with the attributes for the current pet
        firstnameTextView.setText(employeeFirstName);
        lastnameTextView.setText(employeeLastName);
        designationTextView.setText(employeeDesignation);
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        userImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,
                200, false));



    }

}
