package com.patmoorehouse.ticket_fairy;

import com.patmoorehouse.ticket_fairy.SwipePageUpcoming;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SwipePageAdapter extends FragmentPagerAdapter{

	Context context;
	public SwipePageAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.  
        // Return a DummySectionFragment (defined as a static inner class  
        // below) with the page number as its lone argument. 
		Fragment fragment;
		if(position == 0)
		{
	        fragment = new SwipePageUpcoming();  
	        Bundle args = new Bundle();
	        args.putInt(SwipePageUpcoming.ARG_SECTION_NUMBER, position);
	        fragment.setArguments(args);  
		}
		else
		{
			fragment = new SwipePagePopular();  
	        Bundle args = new Bundle();
	        args.putInt(SwipePagePopular.ARG_SECTION_NUMBER, position);
	        fragment.setArguments(args);    			
		}
        return fragment;  
	}

	@Override
	public int getCount() {
		int numPages = 2;
		return numPages;
	}

    @Override  
    public String getPageTitle(int position) { 
        switch (position) {  
        case 0:  
            return context.getResources().getString(R.string.upcoming);
		case 1:  
        	return context.getResources().getString(R.string.popular);
        }
        return null;
    }  		
}