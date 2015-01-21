package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DatePickerFragment extends DialogFragment {
	public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

	private Date mDate;

	// passing date to DatePickerFragment./ Getting the date from crimeFragment
	public static DatePickerFragment newInstance(Date date) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_DATE, date);
		// creating new DatePickerFragment
		DatePickerFragment fragment = new DatePickerFragment();
		// setArguments args that have the Date
		fragment.setArguments(args);
		return fragment;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// getting the date that we set in setArguments
		mDate = (Date) getArguments().getSerializable(EXTRA_DATE);
		// Create Calendar to get year,month,and day
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		// show the dialog_date from the xml by id
		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_date, null);

		DatePicker datePicker = (DatePicker) v
				.findViewById(R.id.dialog_date_datePicker);
		// adding the year, month, day to the dialog_date
		datePicker.init(year, month, day, new OnDateChangedListener() {

			public void onDateChanged(DatePicker view, int year, int month,
					int day) {

				// Translate year, month,day and add it to mDate object 
				mDate = new GregorianCalendar(year, month, day).getTime();

				// Update argument to preserve selected value on rotation
				getArguments().putSerializable(EXTRA_DATE, mDate);
			}
		});

		// returning a new Dialog and add getActivity to constructor
		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setTitle(R.string.date_picker_title)

				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							// when click on positive Button
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//sending back the result from the OK pressing
								sendResult(Activity.RESULT_OK);

							}
						}).create();
	}// end onCreate

	//create method that pass int resultCode
	private void sendResult(int resultCode) {
		//get which TargetFragment we need to send result if empty return
		if (getTargetFragment() == null)
			return;
		//Packaging up  mDate before sending
		Intent i = new Intent();
		i.putExtra(EXTRA_DATE, mDate);
		
		//passing back the result to TargetFragment
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);

	}

}// end class
