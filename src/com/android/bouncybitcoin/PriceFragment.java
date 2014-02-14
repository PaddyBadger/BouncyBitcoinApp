package com.android.bouncybitcoin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PriceFragment extends Fragment {
	private static final String TAG = "PriceFragment";
	
	TextView mListView;
	String mItems;

	private String currency;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	
		return null;
	}
	
	public void fetchPriceInCurrency(String currency) {
		this.currency = currency;
		Log.d(TAG, "PriceFragment"  + currency);
		new FetchPriceTask().execute();
	}
	
	private class FetchPriceTask extends AsyncTask<Void,Void,String> {
		@Override
		protected void onPostExecute(String result) {
			((BouncyBitcoinActivity) getActivity()).displayPrice(result);
		}
		
		@Override
		protected String doInBackground(Void... params) {
			return new PriceFetcher().fetchPrice(currency);
		}
	}
}
