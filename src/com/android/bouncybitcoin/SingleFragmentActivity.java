package com.android.bouncybitcoin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public abstract class SingleFragmentActivity extends FragmentActivity {
	protected Fragment singleFragment;

	protected abstract Fragment createFragment();
	
	protected int getLayoutResId() {
		return R.layout.home;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResId());
		
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		
		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction()
			.add(R.id.fragmentContainer,  fragment)
			.commit();
			
			
		}
		this.singleFragment = fragment;
	}

}
