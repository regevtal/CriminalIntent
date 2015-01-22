package com.bignerdranch.android.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CrimeFragment extends Fragment {

	public static final String EXTRA_CRIME_ID = " com.bignerdranch.android.criminalintent.crime_id";
	public static final String TAG = "CrimeFragment";
	public static final String DIALOG_IMAGE = "image";
	public static final String DIALOG_DATE = "date";
	public static final String DIALOG_TIME = "time";
	private static final int REQUEST_DATE = 0;
	private static final int REQUEST_TIME = 1;
	private static final int REQUEST_PHOTO = 2;
	private static final int REQUEST_CONTACT = 3;

	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private Button mTimeButton;
	private Button reportButton;
	private Button mSuspectButton;
	private Button mSuspectDialButton;
	private ImageButton mPhotoButton;
	private ImageView mPhotoView;
	private CheckBox mSlovedCheckBox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Turn on options menu handling
		setHasOptionsMenu(true);
		// getting the args that pass with the argument convert to ID and add to
		// CrimeId
		UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
		// getting the crime that the ID belong to him
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
	}

	// Adding implementation of onCreateView that inflates fragment_crime.xml
	@TargetApi(11)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		// Inflate fragment_crime.xml by passing layout R ID,
		View v = inflater.inflate(R.layout.fragment_crime, parent, false);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		mTitleField = (EditText) v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		// create a class that implement the TextWatcher listener interface
		mTitleField.addTextChangedListener(new TextWatcher() {
			// convert CharSequence toString user input Return String
			// which use to set the crime title
			public void onTextChanged(CharSequence c, int start, int before,
					int count) {
				mCrime.setTitle(c.toString());
			}

			public void beforeTextChanged(CharSequence c, int start, int count,
					int after) {
				// This space intentionally left blank
			}

			public void afterTextChanged(Editable c) {
				// This one too
			}

		});

		mDateButton = (Button) v.findViewById(R.id.crime_date);
		// set the date
		updateDate();
		// when click on DateButton
		mDateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				// new instance of DatePickerFragment and pass the Date to
				// dialog DatePickerFragment
				DatePickerFragment dialog = DatePickerFragment
						.newInstance(mCrime.getDate());
				// Telling the dialog TargetFragment we want send back
				// REQUEST_DATE
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
				// Show the dialog of this CrimeFragment
				dialog.show(fm, DIALOG_DATE);

			}
		});

		mTimeButton = (Button) v.findViewById(R.id.crime_time);
		updateTime();
		mTimeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				TimePickerFragment dialog = TimePickerFragment
						.newInstance(mCrime.getDate());
				dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
				dialog.show(fm, DIALOG_TIME);

			}
		});

		// check the crime when is solved
		mSlovedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
		mSlovedCheckBox.setChecked(mCrime.isSolved());
		mSlovedCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// set the crime's solved property
						mCrime.setSolved(isChecked);
					}
				});

		mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "mPhotoButton click ");
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);

			}
		});

		mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
		mPhotoView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Photo p = mCrime.getPhoto();
				if (p == null)
					return;

				FragmentManager fm = getActivity().getSupportFragmentManager();
				String path = getActivity().getFileStreamPath(p.getFilename())
						.getAbsolutePath();
				ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);

			}
		});

		// if camera is not available,disable camera functionality
		PackageManager pm = getActivity().getPackageManager();
		boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
				|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Camera
						.getNumberOfCameras() > 0);

		if (!hasACamera) {
			mPhotoButton.setEnabled(false);
		}

		reportButton = (Button) v.findViewById(R.id.crime_reportButton);
		reportButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				Log.i(TAG, "Intent.ACTION_SEND");
				i.setType("text/plain");
				Log.i(TAG, "setType");
				i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
				Log.i(TAG, "putExtra");
				i.putExtra(Intent.EXTRA_SUBJECT,
						getString(R.string.crime_report_subject));
				Log.i(TAG, "putExtra 2");
				i = Intent.createChooser(i, getString(R.string.send_report));
				startActivity(i);
			}
		});

		mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
		mSuspectButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK,
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACT);


			}
		});
		
		 mSuspectDialButton = (Button) v.findViewById(R.id.crime_dialSuspect_button);
		 mSuspectDialButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				String number = mCrime.getSuspectNumber();
                Uri call = Uri.parse("tel:" + number);
                Intent i = new Intent(Intent.ACTION_DIAL,call);
                startActivity(i);
				
			}
		});
		

		if (mCrime.getSuspect() != null && mCrime.getSuspectNumber() != null) {
			mSuspectButton.setText(mCrime.getSuspect());
			mSuspectButton.setText(mCrime.getSuspectNumber());
		}

		return v;
	}// end onCreateView

	// fragment argument
	public static CrimeFragment newInstance(UUID crimeId) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_CRIME_ID, crimeId);
		// create new fragment that will show CrimeFragment
		CrimeFragment fragment = new CrimeFragment();
		// setArguments args that hold crimeId
		fragment.setArguments(args);
		// passing the fragment to get the id
		return fragment;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == REQUEST_DATE) {
			Date date = (Date) data
					.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
			mCrime.setDate(date);
			updateDate();
		}
		if (requestCode == REQUEST_TIME) {

			Date time = (Date) data
					.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
			mCrime.setDate(time);
			updateTime();
		} else if (requestCode == REQUEST_PHOTO) {
			// Create a new Photo object and attach it to the crime
			String filename = data
					.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
			if (filename != null) {
				// save the photo to file
				Photo p = new Photo(filename);
				mCrime.setPhoto(p);
				showPhoto();
			}
		}else if (requestCode == REQUEST_CONTACT) {
			Uri contactUri = data.getData();
			getContact(contactUri);
		}
		
	}// End onActivityResult
	
	
	private void getContact(Uri contactUri){
		ContentResolver cr = getActivity().getContentResolver();
		Cursor phones = cr.query(contactUri, null,null,null, null);
		//
		if (phones.getCount() == 0) {
			phones.close();
			return;
		}
	
		phones.moveToFirst();
		String suspect = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		mCrime.setSuspect(suspect);
		mCrime.setSuspectNumber(phoneNumber);
		mSuspectButton.setText(suspect);
		mSuspectDialButton.setText("Call: " + phoneNumber);
		phones.close();
	}

	private void showPhoto() {
		// (Re)set the image button's image based on our photo
		Photo p = mCrime.getPhoto();
		BitmapDrawable b = null;
		if (p != null) {
			String path = getActivity().getFileStreamPath(p.getFilename())
					.getAbsolutePath();
			b = PictureUtils.getScaledDrawable(getActivity(), path);

		}
		mPhotoView.setImageDrawable(b);
	}

	// Responding to the app icon (Home) menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			// to be implemented next
			return true;
		case R.id.menu_item_delete_crime:
			Log.i(TAG, "Crime been deleted and return to Crime list");
			CrimeLab.get(getActivity()).deleteCrime(mCrime);
			if (NavUtils.getParentActivityName(getActivity()) != null) {
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.crime_fragment_delete_option, menu);
	}

	private String getCrimeReport() {

		String solvedString = null;
		if (mCrime.isSolved()) {
			solvedString = getString(R.string.crime_report_solved);
		} else {
			solvedString = getString(R.string.crime_report_unsolved);
		}

		String dateFormat = "EEE, MMM dd";

		String dateString = DateFormat.format(dateFormat, mCrime.getDate())
				.toString();

		String suspect = mCrime.getSuspect();
		if (suspect == null) {
			suspect = getString(R.string.crime_report_no_suspect);
		} else {
			suspect = getString(R.string.crime_report_suspect, suspect);
		}

		String report = getString(R.string.crime_report,mCrime.getTitle(), dateString,
				solvedString, suspect);
		Log.i(TAG, "report");
		return report;
	}

	private void updateDate() {
		mDateButton.setText(mCrime.getDate().toString());
	}

	@SuppressLint("SimpleDateFormat")
	public void updateTime() {

		SimpleDateFormat timFormat = new SimpleDateFormat("kk:mm zzz");
		mTimeButton.setText(timFormat.format(mCrime.getDate()));
	}

	// save data
	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}

	@Override
	public void onStart() {
		super.onStart();
		showPhoto();
	}

	public void onStop() {
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}

}// end class Crime Fragment

