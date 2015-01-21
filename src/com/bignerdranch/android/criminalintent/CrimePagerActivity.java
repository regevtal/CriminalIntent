package com.bignerdranch.android.criminalintent;
//what running first onCreat or the passing method
import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class CrimePagerActivity extends FragmentActivity {
	private ViewPager mViewPager;
	//instance of arrayList hold crimes
	private ArrayList<Crime> mCrimes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		setContentView(mViewPager);

		//  getting data from ceimeLab to the arrayList of Crimes
		mCrimes = CrimeLab.get(this).getCrimes();

		// getting the activity instance of FragmentManager
		FragmentManager fm = getSupportFragmentManager();
		// set adapter to be unnamed instance of FragmentStatePagerAdapter for scrolling
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

			@Override
			public int getCount() {

				return mCrimes.size();
			}
          //get the next page
			@Override
			public Fragment getItem(int pos) {
				//get the pos of the next page
				Crime crime = mCrimes.get(pos);
				//returning the next page to show
				return CrimeFragment.newInstance(crime.getId());
			}

		});

		mViewPager.setOnPageChangeListener
						(new ViewPager.OnPageChangeListener() 
		{

					@Override
					public void onPageScrollStateChanged(int state) {
					}

					@Override
					public void onPageScrolled(int pos, float posOffset,
							int posOffsetPixels) {

					}
					//change title
					@Override
					public void onPageSelected(int pos) {
						Crime crime = mCrimes.get(pos);
						if (crime.getTitle() != null) {
							setTitle(crime.getTitle());
						}

					}
		});
       //Getting CrimeId 
		UUID crimeId = (UUID) getIntent().getSerializableExtra(
				CrimeFragment.EXTRA_CRIME_ID);
	
		for (int i = 0; i < mCrimes.size(); i++) {
			//if the position of the crime with id equal to crimeId
			if (mCrimes.get(i).getId().equals(crimeId)) {
				//Show that crime
				mViewPager.setCurrentItem(i);
			    break;
			}
				
		}
	}

}// end class
