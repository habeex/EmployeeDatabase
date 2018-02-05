package com.lebelle.employeedatabase.model;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lebelle.employeedatabase.R;
import com.lebelle.employeedatabase.Utils;
import com.lebelle.employeedatabase.data.EmployeeContract;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by DELL on 2/5/2018.
 */

public class RecycAdapter extends RecyclerView.Adapter<RecycAdapter.ViewHolder>{
    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;
    CustomItemListener customItemListener;



    public RecycAdapter(CustomItemListener itemListener) {
        customItemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(EmployeeContract.EmployeeEntry._ID);
        int firstnameColumnIndex = mCursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_FIRST_NAME);
        int lastnameColumnIndex = mCursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_LAST_NAME);
        int designationColumnIndex = mCursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_DESIGNATION);
        int imageColumnIndex = mCursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_IMAGE);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Read the pet attributes from the Cursor for the current pet
        String employeeFirstName = mCursor.getString(firstnameColumnIndex);
        String employeeLastName = mCursor.getString(lastnameColumnIndex);
        String employeeDesignation = mCursor.getString(designationColumnIndex);
        byte[] image = mCursor.getBlob(imageColumnIndex);


        // Update the TextViews with the attributes for the current pet
        final int id = mCursor.getInt(idIndex);
        holder.itemView.setTag(id);
        holder.firstnameTextView.setText(employeeFirstName);
        holder.lastnameTextView.setText(employeeLastName);
        holder.designationTextView.setText(employeeDesignation);
        /*Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.userImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, 200,
                200, false));*/
        holder.userImage.setImageBitmap(Utils.getBitmapFromByte(mCursor.getBlob(mCursor.getColumnIndex(EmployeeContract.EmployeeEntry.COLUMN_IMAGE))));


    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView firstnameTextView;
        TextView lastnameTextView;
        TextView designationTextView;
        CircularImageView userImage;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public ViewHolder(View itemView) {
            super(itemView);

            firstnameTextView = itemView.findViewById(R.id.one_name_text_view);
            lastnameTextView = itemView.findViewById(R.id.two_name_text_view);
            designationTextView = itemView.findViewById(R.id.list_designation_text_view);
            userImage = itemView.findViewById(R.id.avatar);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //go to employee details page
            customItemListener.onClick(getLayoutPosition());

        }

        @Override
        public boolean onLongClick(View view) {
            customItemListener.onLongClick(getLayoutPosition());
            return true;
        }
    }

    public interface CustomItemListener {
        void onClick(int position);
        void onLongClick(int position);

    }
}
