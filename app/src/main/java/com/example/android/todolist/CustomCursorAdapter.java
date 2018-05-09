/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.todolist.data.TaskContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This CustomCursorAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.TaskViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;
    private final String TAG = CustomCursorAdapter.class.getSimpleName();


    /**
     * Constructor for the CustomCursorAdapter that initializes the Context.
     *
     * @param mContext the current Context
     */
    public CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false);

        return new TaskViewHolder(view);
    }


    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {

        // Indices for the _id, description, and priority columns
        int idIndex = mCursor.getColumnIndex(TaskContract.TaskEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int priorityIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY);
        int deadlineIndex = mCursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DEADLINE);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        String description = mCursor.getString(descriptionIndex);
        int priority = mCursor.getInt(priorityIndex);
        String deadline  = mCursor.getString(deadlineIndex);
        Log.e(TAG, "onBindViewHolder: deadline"+ deadline);
    
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yy", Locale.US);
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
             date  = simpleDateFormat.parse(deadline);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        
        //First get the Current date
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
    
        SimpleDateFormat df = new SimpleDateFormat("mm/dd/yy");
        String formattedDate = df.format(c);
        try {
            Date currentDate = df.parse(formattedDate);
            long diff = date.getTime() - currentDate.getTime();
            Log.e(TAG, "onBindViewHolder: Day left"+ TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) );
            
            if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)<0){
                holder.mCountDownTextView.setText("Swipe to Remove");
                holder.mCountDownTextView.setTextColor(Color.GRAY);
            }else if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) ==0){
                holder.mCountDownTextView.setText("Complete Task Today");
                holder.mCountDownTextView.setTextColor(Color.RED);
            }else{
                holder.mCountDownTextView.setText(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+" Days Left to Complete" );
                holder.mCountDownTextView.setTextColor(Color.GREEN);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    
        
        //Set values
        holder.itemView.setTag(id);
        holder.taskDescriptionView.setText(description);

        // Programmatically set the text and color for the priority TextView
        String priorityString = "" + priority; // converts int to String
        holder.priorityView.setText(priorityString);

        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);
    
        /*Log.e(TAG, "onBindViewHolder: time "+calendar.getTimeInMillis() );
        Log.e(TAG, "onBindViewHolder: current time"+ Calendar.getInstance().getTime());
        Log.e(TAG, "onBindViewHolder: diffrence"+ (calendar.getTimeInMillis() - System.currentTimeMillis()));*/
        //Set the time
        /*new CountDownTimer(  System.currentTimeMillis()-calendar.getTimeInMillis(),1000){
    
            @Override
            public void onTick(long millisUntilFinished) {
                String hms = String.format("%02d:%02d:%02d:%02d",
                        TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished))),
                        (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))),
                        (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                holder.mCountDownTextView.setText(hms);
            }
    
            @Override
            public void onFinish() {
                holder.mCountDownTextView.setText("Completed");
            }
        }.start();*/

    }


    /*
    Helper method for selecting the correct priority circle color.
    P1 = red, P2 = orange, P3 = yellow
    */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch(priority) {
            case 1: priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2: priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3: priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default: break;
        }
        return priorityColor;
    }


    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
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


    // Inner class for creating ViewHolders
    class TaskViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        TextView taskDescriptionView;
        TextView priorityView;
        TextView mCountDownTextView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(View itemView) {
            super(itemView);

            taskDescriptionView = (TextView) itemView.findViewById(R.id.taskDescription);
            priorityView = (TextView) itemView.findViewById(R.id.priorityTextView);
            mCountDownTextView = itemView.findViewById(R.id.textview_deadline_countdown);
            
        }
    }
}