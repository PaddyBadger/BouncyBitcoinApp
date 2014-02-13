package com.android.bouncybitcoin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PriceFragment extends Fragment {
	private static final String TAG = "PriceFragment";
	
	TextView mListView;
	String mItems;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		new FetchPriceTask().execute();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setupAdapter();
		return null;
	}
	
	void setupAdapter() {
		if (getActivity() == null || mListView == null) return;
		
		new FetchPriceTask().execute();	
		
	}
	
	private class FetchPriceTask extends AsyncTask<Void,Void,String> {
		@Override
		protected void onPostExecute(String result) {
			((BouncyBitcoinActivity) getActivity()).displayPrice(result);
		}
		
		@Override
		protected String doInBackground(Void... params) {
			return new PriceFetcher().fetchPrice();
		}
	}
}
