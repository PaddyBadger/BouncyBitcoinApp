package com.android.bouncybitcoin;

import android.annotation.SuppressLint;
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
	private Button bouncingBallBtnGbp;
	private Button bouncingBallBtnEur;
	private Button bouncingBallBtnCny;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        bouncingBallBtn = (Button) findViewById(R.id.bouncing_ball_btn);
        bouncingBallBtn.setOnClickListener(this);
        
        bouncingBallBtnGbp = (Button) findViewById(R.id.bouncing_ball_gbp);
        bouncingBallBtnGbp.setOnClickListener(this);
        
        bouncingBallBtnEur = (Button) findViewById(R.id.bouncing_ball_eur);
        bouncingBallBtnEur.setOnClickListener(this);
        
        bouncingBallBtnCny = (Button) findViewById(R.id.bouncing_ball_cny);
        bouncingBallBtnCny.setOnClickListener(this);
    } 
    
	public void onClick(View v) {
		Intent currency = new Intent(Home.this, BouncyBitcoinActivity.class);

    	if (v == bouncingBallBtn) {
    		currency.putExtra("Currency code", "USD");
    		startActivity(currency);
    		
    	} else if (v == bouncingBallBtnGbp) {
    		currency.putExtra("Currency code", "GBP");
    		startActivity(currency);
    		
    	} else if (v == bouncingBallBtnEur) {
    		currency.putExtra("Currency code", "EUR");
    		startActivity(currency);
    		
    	} else if (v == bouncingBallBtnCny) {
    		currency.putExtra("Currency code", "CNY");
    		startActivity(currency);
    		
    	}
    }
}
