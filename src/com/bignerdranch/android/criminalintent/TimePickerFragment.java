package com.bignerdranch.android.criminalintent;

import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment {
	public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
	private Date mTime;

	public static TimePickerFragment newInstance(Date time) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TIME, time);

		TimePickerFragment fragment = new TimePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mTime = (Date) getArguments().getSerializable(EXTRA_TIME);

		// create calendar to get Hour and minute
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mTime);
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		View v = getActivity().getLayoutInflater().inflate(
				R.layout.dialog_time, null);

		TimePicker timePicker = (TimePicker) 
				  v.findViewById(R.id.dialog_time_timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(hourOfDay);
		timePicker.setCurrentMinute(minute);
		timePicker
				.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

					@Override
					public void onTimeChanged(TimePicker view, int hourOfDay,
							int minute) {

						Calendar calendar = Calendar.getInstance();
						calendar.setTime(mTime);

						// Update Time while preserving Date
						calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
						calendar.set(Calendar.MINUTE, minute);

						mTime = calendar.getTime();
						getArguments().putSerializable(EXTRA_TIME, mTime);

					}
				});

		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setTitle(R.string.time_picker_title)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								sendResult(Activity.RESULT_OK);

							}
						}).create();
	}

	private void sendResult(int resultCode) {
		if (getTargetFragment() == null)
			return;

		Intent i = new Intent();
		i.putExtra(EXTRA_TIME, mTime);
		getTargetFragment().onActivityResult(getTargetRequestCode(),
				resultCode, i);
	}

}// end class
