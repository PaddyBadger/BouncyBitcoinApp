package com.android.bouncybitcoin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class Sensors extends Activity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensors);
        
		ListView sensorList = (ListView)findViewById(R.id.sensor_list);
		sensorList.setAdapter(new SensorListAdapter(this));
    }

}
