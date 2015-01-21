package com.bignerdranch.android.criminalintent;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class CrimeListFragment extends ListFragment {
	private static final String TAG = "CrimeListFragment";
	private boolean mSubtitleVisible;
	private Button mAddCrimeBtn;
	// ArrayList of Crimes
	private ArrayList<Crime> mCrimes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// when retain the variable not changing
		setRetainInstance(true);
		mSubtitleVisible = false;
		// Set hasOptionMenu
		setHasOptionsMenu(true);
		// get the activity and set there title
		getActivity().setTitle(R.string.crimes_title);
		// access to crimeLab context and get the list of Crimes
		mCrimes = CrimeLab.get(getActivity()).getCrimes();
		// create new adapter
		CrimeAdapter adapter = new CrimeAdapter(mCrimes);
		setListAdapter(adapter);

	}// end onCreate

	@TargetApi(11)
	@Override
	// set subtitle if mSubtitleVisible is true
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list_or_empty_view_item, container,
				false);
		ListView view = (ListView) v.findViewById(android.R.id.list);

		view.setEmptyView(v.findViewById(android.R.id.empty));

		mAddCrimeBtn = (Button) v.findViewById(R.id.btn_add_crime);
		mAddCrimeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Crime crime = new Crime();
				CrimeLab.get(getActivity()).addCrime(crime);
				Intent i = new Intent(getActivity(), CrimePagerActivity.class);
				i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
				startActivityForResult(i, 0);
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (mSubtitleVisible) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
			}
		}
		
		// delete options
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			// use floating context menu on Froyo and Gingerbread
			registerForContextMenu(view);
		} else {
			// Use contextual action bar on Honeycomb and higher
			view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			view.setMultiChoiceModeListener(new MultiChoiceModeListener() {
				
				@Override
				public void onItemCheckedStateChanged(ActionMode mode,
						int position, long id, boolean checked) {
					// Required, but not used in this implementation
				}

				// ActionMode.Callback methods
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.crime_list_item_context, menu);
					return true;
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					// Required, but not used in this implementation
					return false;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					switch (item.getItemId()) {
					case R.id.menu_item_delete_crime:
						//get the list from the adapter
						CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
						CrimeLab crimeLab = CrimeLab.get(getActivity());
						// Go throw each crime it the list
						for (int i = adapter.getCount() - 1; i >= 0; i--) {
							// If item is check
							if (getListView().isItemChecked(i)) {
								// Get the item from the list and delete it
								crimeLab.deleteCrime(adapter.getItem(i));
							}
						}
						
						mode.finish();
						adapter.notifyDataSetChanged();
						return true;
					default:
						return false;
					}
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					// Required, but not used in this implementation

				}
			});
		}
		return v;

	}// end onCreateView
		// when click pass the CrimeFragemnt that belong to the crime number


	// create a new adapter subclass to work with the crime object
	private class CrimeAdapter extends ArrayAdapter<Crime> {

		// Constructor and create in CrimeAdapter the list of crimes
		public CrimeAdapter(ArrayList<Crime> crimes) {
			super(getActivity(), 0, crimes);
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// if we weren't given a view, inflate one
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_crime, null);
			}
			// Configure the view for this crime
			// get the data in position
			Crime c = getItem(position);
			// convert from memory to text view
			TextView titleTextView = (TextView) convertView
					.findViewById(R.id.crime_list_item_titleTextView);
			titleTextView.setText(c.getTitle());

			TextView dateTextView = (TextView) convertView
					.findViewById(R.id.crime_list_item_dateTextView);
			dateTextView.setText(c.getDate().toString());

			CheckBox solvedCheckBox = (CheckBox) convertView
					.findViewById(R.id.crime_list_item_solvedCheckBox);
			solvedCheckBox.setChecked(c.isSolved());
			return convertView;
		}

	}//end of CrimeAdapter

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Get the crime from the adapter
		Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
		// Start CrimePagerActivity with this crime
		Intent i = new Intent(getActivity(), CrimePagerActivity.class);
		// put inside EXTRA_CRIME_ID crimeId and start crime activity
		i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
		startActivity(i);

	}

	// inflate an option menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_crime_list, menu);
		// set menu item title based on mSubTitleVisible
		MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
		if (mSubtitleVisible && showSubtitle != null) {
			showSubtitle.setTitle(R.string.hide_subtitle);
		}
	}

	// Responding to menu selection
	@TargetApi(11)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// get the item that selected
		switch (item.getItemId()) {
		// in case select menu_item_new_crime:
		case R.id.menu_item_new_crime:
			Crime crime = new Crime();
			CrimeLab.get(getActivity()).addCrime(crime);
			Intent i = new Intent(getActivity(), CrimePagerActivity.class);
			i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
			startActivityForResult(i, 0);
			return true;
			// in case select menu_item_show_subtitle:
		case R.id.menu_item_show_subtitle:
			if (getActivity().getActionBar().getSubtitle() == null) {
				getActivity().getActionBar().setSubtitle(R.string.subtitle);
				mSubtitleVisible = true;
				item.setTitle(R.string.hide_subtitle);
			} else {
				getActivity().getActionBar().setSubtitle(null);
				mSubtitleVisible = false;
				item.setTitle(R.string.show_subtitle);
			}
			return true;
		default:
			// Don't do anything
			return super.onOptionsItemSelected(item);
		}
	}

	// inflate a menu resource and use it to populate the context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context,
				menu);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// when click get the info
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// get what is int position
		int position = info.position;

		// get the list adapter of crime
		CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
		// get the item with position that click
		Crime crime = adapter.getItem(position);

		switch (item.getItemId()) {
		case R.id.menu_item_delete_crime:
			CrimeLab.get(getActivity()).deleteCrime(crime);
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart() called");
	}

	@Override
	public void onPause() {
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}

	@Override
	public void onResume() {
		super.onResume();
		((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
		Log.d(TAG, "onResume() called");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop() called");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() called");
	}

}// end class
