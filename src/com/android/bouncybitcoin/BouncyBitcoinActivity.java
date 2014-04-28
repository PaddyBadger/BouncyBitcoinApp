package com.android.bouncybitcoin;

import static android.hardware.SensorManager.DATA_X;
import static android.hardware.SensorManager.DATA_Y;
import static android.hardware.SensorManager.SENSOR_ACCELEROMETER;
import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

@SuppressWarnings("depreciation")
public class BouncyBitcoinActivity extends SingleFragmentActivity implements Callback, SensorListener{
	private static final String TAG="BBA";
	private static final int PADDY_DPI = 320;
	private static int BALL_RADIUS;
	private static final int MAXSEGMENTS = 8;
	private static final int PADDY_PHONE_HEIGHT = 1184;
	private static final int PADDY_PHONE_WIDTH = 720;
	private TouchableView surface;
	private SurfaceHolder holder;
	private Paint ballPaint;
	private int ballNumber;
	private ArrayList<BouncyBitcoinModel> models = new ArrayList<BouncyBitcoinModel>();
	private GameLoop gameLoop;
	private Paint backgroundPaint;
	private SensorManager sensorMgr;
	private long lastSensorUpdate = -1;
	private long lastPriceUpdate = -1;
	public NumbersActivity nA;
	private int SurfaceWidth;
	private int SurfaceHeight;
	private Random rand = new Random();
	private boolean ballsSpawned;
	private String currencyCode;
	private PriceFragment fragment;
	
	public Fragment createFragment() {
		
		Fragment currencyFragment = new PriceFragment();
		return currencyFragment;	
	}
	
	public final int displayHeight() {
		DisplayMetrics d = this.getResources().getDisplayMetrics();
		int screenHeight = d.heightPixels;
		return screenHeight;
	}
	public final int displayWidth() {
		DisplayMetrics d = this.getResources().getDisplayMetrics();
		int screenWidth = d.widthPixels;
		return screenWidth;
	}
	
	public final int displayDensity() {
		DisplayMetrics d = this.getResources().getDisplayMetrics();
		int screenDensity = d.densityDpi;
		
		return screenDensity;
	}
	
	
	
	 public void displayPrice(String price) {
		 
		 if (ballsSpawned == false) {
			 String eight = "88888888888.88";
			 String eights = (String) eight.subSequence(14-price.length(),14);
	    	
			 for (int i = 0; i < price.length(); i ++) { 
	    		 int[] eightSegments = nA.numbers.get(eights.charAt(i));
	    		 ballPaint = new Paint();
	    		 ballPaint.setColor(randomColor());
	    		 
	    		 for (int j = 0; j < eightSegments.length; j ++) {
	    			 int[][] coords = nA.ballCoords(eightSegments[j]);
	    			 
	    			 for (int k = 0; k < coords.length; k++) {
	    				 BouncyBitcoinModel ball = new BouncyBitcoinModel(BALL_RADIUS, ballPaint);
	    				 ball.setMove(((coords[k][0] + 170 * i)*(displayHeight()/PADDY_PHONE_HEIGHT)-100), coords[k][1]*displayWidth()/PADDY_PHONE_WIDTH); 
	    				 ball.moveBall(rand.nextInt(SurfaceWidth), rand.nextInt(SurfaceHeight));
	    				 ball.setSize(SurfaceWidth, SurfaceHeight);
	    				 ball.setHomeSegment(i, eightSegments[j]);
	    				 models.add(ball); 
					 	this.ballNumber++;			 
	    			 }
	    		 }
	    	 }
	    	 
	    	 this.ballsSpawned = true;
		}
		
		boolean[][] validSeqs = new boolean[price.length()][MAXSEGMENTS];
		for (int m = 0; m < price.length(); m ++) {
			int[] segments = nA.numbers.get(price.charAt(m));
			for (int j = 0; j < segments.length; j ++) {
				validSeqs[m][segments[j]] = true;
			}
		}
		for (int k = 0; k < ballNumber; k++){
			models.get(k).toggleMoveState(validSeqs);
		}
	}
	 
	 private int randomColor() {
			int r = rand.nextInt(256);
			int g = rand.nextInt(256);
			int b = rand.nextInt(256);
			
			return Color.argb(255, r, g, b);
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		BALL_RADIUS = displayDensity()*13/PADDY_DPI;
		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		currencyCode = intent.getStringExtra("Currency code");
		fragment = (PriceFragment)singleFragment;
		fragment.fetchPriceInCurrency(currencyCode);
		
		setContentView(R.layout.activity_bouncy_bitcoin);
	
		this.ballNumber = 0;
		surface = (TouchableView) findViewById(R.id.bouncy_bitcoin_surface);
		surface.setBallsActivity(this);
		holder = surface.getHolder();
		surface.getHolder().addCallback(this);
		
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.WHITE);
		
		ballPaint = new Paint();
		ballPaint.setColor(randomColor());
		ballPaint.setAntiAlias(true);
		
		nA = new NumbersActivity();
		
		
	}

	@Override
	public void onPause() {
		super.onPause();
		
		for (int i = 0; i < ballNumber; i ++) {
			models.get(i).setVibrator(null);
		}
		
		sensorMgr.unregisterListener(this, SENSOR_ACCELEROMETER);
		sensorMgr = null;
		
		for (int i = 0; i < ballNumber; i ++) {
			models.get(i).setAccel(0, 0);
		}
		
		// implement pausing on all models
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
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.SurfaceWidth = width;
		this.SurfaceHeight = height;
		for (int i = 0; i < ballNumber; i ++) {

			models.get(i).setSize(width, height);
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
			Paint thisBallPaint;
			
			for (int i = 0; i < ballNumber; i ++) {
				synchronized (models.get(i)) {
					ballX = models.get(i).ballPixelX;
					ballY = models.get(i).ballPixelY;
					thisBallPaint = models.get(i).ballPaint;
				}
				c.drawCircle(ballX,  ballY, BALL_RADIUS, thisBallPaint);
			}		
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			for (int i = 0; i < ballNumber; i ++) {
				models.get(i).setSize(0,0);
			}
			gameLoop.safeStop();
		} finally {
			gameLoop = null;
		}
	}
	
	public class GameLoop extends Thread {
		private volatile boolean running = true;
		private long loopCurTime;
		private long lastPriceUpdate;
		
		public void run() {
			while (running) {
				try {
					TimeUnit.MILLISECONDS.sleep(5);
					
					draw();
					
					for (int i = 0; i < ballNumber; i ++) {
						
						models.get(i).updatePhysics();
						
					}
					loopCurTime = System.currentTimeMillis();
					if (lastPriceUpdate == -1 || loopCurTime - lastPriceUpdate > 30000) {
						lastPriceUpdate = loopCurTime;

						PriceFragment fragment = (PriceFragment)singleFragment;
						fragment.fetchPriceInCurrency(currencyCode);
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
					
					models.get(i).setAccel(values[DATA_X], values[DATA_Y]);
				}
			}
			
		}
	}
	
	public void explode(MotionEvent event) {
		for (int i = 0; i < ballNumber; i ++) {
			models.get(i).tryExplode(event);
		}
	}
 }
