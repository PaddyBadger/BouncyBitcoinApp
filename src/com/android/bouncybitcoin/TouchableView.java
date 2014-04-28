package com.android.bouncybitcoin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class TouchableView extends SurfaceView{
	private final String TAG="TV";
	private BouncyBitcoinActivity bbActivity = null;
	

	public TouchableView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public TouchableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public TouchableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		// if (event.getAction() == MotionEvent.ACTION_DOWN) {
		if (bbActivity != null){ 
			bbActivity.explode(event);
		}
	//	}
		return true;
	}

	public void setBallsActivity(BouncyBitcoinActivity bbActivity) {
		this.bbActivity = bbActivity;
	}
}
