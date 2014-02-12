package com.android.bouncybitcoin;

import static android.hardware.SensorManager.DATA_X;
import static android.hardware.SensorManager.DATA_Y;
import static android.hardware.SensorManager.SENSOR_ACCELEROMETER;
import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

import java.util.concurrent.TimeUnit;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

@SuppressWarnings("depreciation")
public class BouncyBitcoinActivity extends Activity implements Callback, SensorListener{
	private static final String TAG= "BBA";
	private static final int BALL_RADIUS=15;
	private SurfaceView surface;
	private SurfaceHolder holder;
	private Paint ballPaint;
	private int ballNumber = 10;
	private final BouncyBitcoinModel[] models = new BouncyBitcoinModel [ballNumber];
	private GameLoop gameLoop;
	private Paint backgroundPaint;
	private SensorManager sensorMgr;
	private long lastSensorUpdate = -1;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_bouncy_bitcoin);
		
		surface = (SurfaceView) findViewById(R.id.bouncy_bitcoin_surface);
		holder = surface.getHolder();
		surface.getHolder().addCallback(this);
		
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.WHITE);
		
		ballPaint = new Paint();
		ballPaint.setColor(Color.CYAN);
		ballPaint.setAntiAlias(true);
		
		for (int i = 0; i < ballNumber; i ++) {
			BouncyBitcoinModel model = new BouncyBitcoinModel(BALL_RADIUS);
			models[i] = model;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		for (int i = 0; i < ballNumber; i ++) {
			models[i].setVibrator(null);
		}
		
		sensorMgr.unregisterListener(this, SENSOR_ACCELEROMETER);
		sensorMgr = null;
		
		for (int i = 0; i < ballNumber; i ++) {
			models[i].setAccel(0, 0);
		}
	}
	
	@Override 
	protected void onResume() {
		super.onResume(); 
		
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		boolean accelSupported = sensorMgr.registerListener(this, 
				SENSOR_ACCELEROMETER,
				SENSOR_DELAY_GAME);
		
		if (!accelSupported) {
			sensorMgr.unregisterListener(this , SENSOR_ACCELEROMETER);
		}
		
		Vibrator vibrator = (Vibrator) getSystemService(Activity.VIBRATOR_SERVICE);
		for (int i = 0; i < ballNumber; i ++) {
			models[i].setVibrator(vibrator);
		}
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		for (int i = 0; i < ballNumber; i ++) {
			models[i].setSize(width, height);
		}
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		gameLoop = new GameLoop();
		gameLoop.start();
	}
	
	private void draw() {
		
		Canvas c = null;
		try {
			c = holder.lockCanvas();
			
			if (c != null) {
				doDraw(c);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}
	
	private void doDraw(Canvas c) {
		int width = c.getWidth();
		int height = c.getHeight();
		c.drawRect(0, 0, width, height, backgroundPaint);
			
			float ballX, ballY;
			
			for (int i = 0; i < ballNumber; i ++) {
				synchronized (models[i]) {
					ballX = models[i].ballPixelX;
					ballY = models[i].ballPixelY;
				}
				c.drawCircle(ballX,  ballY, BALL_RADIUS, ballPaint);
			}	
			
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			for (int i = 0; i < ballNumber; i ++) {
				models[i].setSize(0,0);
			}
			gameLoop.safeStop();
		} finally {
			gameLoop = null;
		}
	}
	
	private class GameLoop extends Thread {
		private volatile boolean running = true;
		
		public void run() {
			while (running) {
				try {
					TimeUnit.MILLISECONDS.sleep(5);
					
					draw();
					
					for (int i = 0; i < ballNumber; i ++) {
						models[i].updatePhysics();
					}
				} catch (InterruptedException ie) {
					running = false;
				}
			}
		}
		
		public void safeStop() {
			running = false;
			interrupt();
		}
	}
	
	public void onAccuracyChanged(int sensor, int accuracy) {
		
	}
	
	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SENSOR_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			
			if (lastSensorUpdate == -1 || (curTime - lastSensorUpdate) > 50) {
				lastSensorUpdate = curTime;
				
				for (int i = 0; i < ballNumber; i ++) {
					models[i].setAccel(values[DATA_X], values[DATA_Y]);
				}
			}
		}
	}
}
