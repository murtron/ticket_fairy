package com.patmoorehouse.ticket_fairy;

    //The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnGigSelectedListener {
		/** Called by SwipePageContent when a list item is selected */
		public void onGigSelected(Gig gig);
	}
