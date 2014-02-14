package com.android.bouncybitcoin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener {
	private static final String TAG="Home";

	private Button bouncingBallBtn;
	//private Button bouncingBallBtnGbp;
	// private Button bouncingBallBtnEur;
	// private Button bouncingBallBtnCny;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        bouncingBallBtn = (Button) findViewById(R.id.bouncing_ball_btn);
        bouncingBallBtn.setOnClickListener(this);
    } 
    
    public void onClick(View v) {
    	if (v == bouncingBallBtn) {
    		startActivity(new Intent(Home.this, BouncyBitcoinActivity.class));
    	}
    }
}
