package com.android.bouncybitcoin;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;

public class BouncyBitcoinModel {
	private static final String TAG = "Bouncy";
	// the ball speed is meters / second. When we draw to the screen,
		// 1 pixel represents 1 meter. That ends up too slow, so multiply
		// by this number. Bigger numbers speeds things up.
		private final float pixelsPerMeter() {
			float minX = 5;
	    	float maxX = 15;
	    	
	    	Random rand = new Random();
	    	
	    	float nextSpeed = rand.nextFloat() * (maxX - minX) + minX;
	    	return nextSpeed;
		}
		
		private final int ballRadius;
		
		public final Paint ballPaint;
		
		// these are public, so make sure you synchronize on LOCK 
		// when reading these. I made them public since you need to
		// get both X and Y in pairs, and this is more efficient than
		// getter methods. With two getters, you'd still need to 
		// synchronize.
		public float ballPixelX, ballPixelY;
		
		private int moveToX, moveToY;
		
		private boolean doesMove = false;
		
		private static final float textAttraction = 0.03F;
		
		private int pixelWidth, pixelHeight;
		
		// values are in meters/second
		private float velocityX, velocityY;
		
		// typical values range from -10...10, but could be higher or lower if
		// the user moves the phone rapidly
		private float accelX, accelY;

	    // if the ball bounces and the velocity is less than this constant,
	    // stop bouncing.
	    private static final float STOP_BOUNCING_VELOCITY = 2f;

	    private volatile long lastTimeMs = -1;
		
		public final Object LOCK = new Object();
		
		private AtomicReference<Vibrator> vibratorRef =
			new AtomicReference<Vibrator>();
		
		/**
	     * When the ball hits an edge, multiply the velocity by the rebound.
	     * A value of 1.0 means the ball bounces with 100% efficiency. Lower
	     * numbers simulate balls that don't bounce very much.
	     */
		private static final float rebound() {
	    	float minX = 0.3f;
	    	float maxX = 0.6f;
	    	
	    	Random rand = new Random();
	    	
	    	float nextRebound = rand.nextFloat() * (maxX - minX) + minX;
	    	return nextRebound;
	    }
		
		public final float displayWidth() {
			return pixelWidth / 2.4f;
		}
		
		public void setMove(int mX, int mY) {
			this.doesMove = true;
			this.moveToX = mX;
			this.moveToY = mY;
		}
		
		public BouncyBitcoinModel(int ballRadius, Paint ballPaint) {
			this.ballRadius = ballRadius;
			this.ballPaint = ballPaint;
		}
		
		public void setAccel(float ax, float ay) {
			synchronized (LOCK) {
				if (this.doesMove) {
					this.accelX = (ax / 500) + textAttraction*(moveToY+displayWidth() - ballPixelX); // refector to constants
					this.accelY = (ay / 500) - textAttraction*(800-(moveToX+20)*8/11 - ballPixelY);
				} else {
					this.accelX = ax;
					this.accelY = ay;
				}
			}
		}
		
		public void setSize(int width, int height) {
			synchronized (LOCK) {
				this.pixelWidth = width;
				this.pixelHeight = height;
			}
		}
		
	    public int getBallRadius() {
	        return ballRadius;
	    }

	    /**
	     * Call this to move the ball to a particular location on the screen. This
	     * resets the velocity to zero, but the acceleration doesn't change so
	     * the ball should start falling shortly.
	     */
	    public void moveBall(int ballX, int ballY) {
	    	synchronized (LOCK) {
	            this.ballPixelX = ballX;
	            this.ballPixelY = ballY;
	            velocityX = 0;
	            velocityY = 0;
	        }
	    }
	    
	    public void updatePhysics() {
	        // copy everything to local vars (hence the 'l' prefix)
	        float lWidth, lHeight, lBallX, lBallY, lAx, lAy, lVx, lVy;
	        synchronized (LOCK) {
	            lWidth = pixelWidth;
	            lHeight = pixelHeight;
	            lBallX = ballPixelX;
	            lBallY = ballPixelY;
	            lVx = velocityX;            
	            lVy = velocityY;
	            lAx = accelX;
	            lAy = -accelY;
	        }


	        if (lWidth <= 0 || lHeight <= 0) {
	            // invalid width and height, nothing to do until the GUI comes up
	            return;
	        }


	        long curTime = System.currentTimeMillis();
	        if (lastTimeMs < 0) {
	            lastTimeMs = curTime;
	            return;
	        }

	        long elapsedMs = curTime - lastTimeMs;
	        lastTimeMs = curTime;
	        
	        // update the velocity
	        // (divide by 1000 to convert ms to seconds)
	        // end result is meters / second
	        lVx += ((elapsedMs * lAx) / 1000) * pixelsPerMeter();
	        lVy += ((elapsedMs * lAy) / 1000) * pixelsPerMeter();
	        
	        // update the position
	        // (velocity is meters/sec, so divide by 1000 again)
	        lBallX += ((lVx * elapsedMs) / 1000) * pixelsPerMeter();
	        lBallY += ((lVy * elapsedMs) / 1000) * pixelsPerMeter();
	        
	        boolean bouncedX = false;
	        boolean bouncedY = false;
	        if (this.doesMove){
	        	lVx = lVx / 1.02f; 
	        	lVy = lVy / 1.02f;
	        }
	        if (lBallY - ballRadius < 0) {
	            lBallY = ballRadius;
	            lVy = -lVy * rebound();
	            bouncedY = true;
	        } else if (lBallY + ballRadius > lHeight) {
	            lBallY = lHeight - ballRadius;
	            lVy = -lVy * rebound();
	            bouncedY = true;
	        }
	        if (bouncedY && Math.abs(lVy) < STOP_BOUNCING_VELOCITY) {
	            lVy = 0;  
	            bouncedY = false;
	        }

	        if (lBallX - ballRadius < 0) {
	        	lBallX = ballRadius;
	        	lVx = -lVx * rebound();
	        	bouncedX = true;
	        } else if (lBallX + ballRadius > lWidth) {
	            lBallX = lWidth - ballRadius;
	            lVx = -lVx * rebound();
	            bouncedX = true;
	        }
	        
	        if (bouncedX && Math.abs(lVx) < STOP_BOUNCING_VELOCITY) {
	        	lVx = 0;
	        	bouncedX = false;
	        }
	        

	        // safely copy local vars back to object fields
	        synchronized (LOCK) {
	            ballPixelX = lBallX;
	            ballPixelY = lBallY;
	            
	            velocityX = lVx;
	            velocityY = lVy;
	        }
	        
	        if (bouncedX || bouncedY) {
	        	Vibrator v = vibratorRef.get();
	        	if (v != null) {
	        		v.vibrate(10L);
	        	}
	        }
	    }
	    
	    public void setVibrator(Vibrator v) {
	    	vibratorRef.set(v);
	    }
}
