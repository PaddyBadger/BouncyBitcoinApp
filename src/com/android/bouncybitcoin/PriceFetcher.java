package com.android.bouncybitcoin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.Uri;
import android.util.Log;

public class PriceFetcher {
	public static final String TAG="PriceFetcher";
	
	private static final String priceURL = "https://api.bitcoinaverage.com/ticker/";
	
	byte[] getUrlBytes(String urlSpec) throws IOException {

		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			} 
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}
	public String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}
	
	public String fetchPrice(String currency) {
		
		String last;
		try {
			
			String url = Uri.parse(priceURL + currency).buildUpon().build().toString();
			String jsonString = getUrl(url);
			
			JSONObject jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
			last = jsonObject.getString("last");
			float lastFormat = Float.parseFloat(last);
			last = String.format("%.2f", lastFormat);
			
		} catch (IOException ioe) {
			Log.e(TAG, "Failed to fetch items", ioe);
			return null;
		} catch (JSONException je) {
			Log.e(TAG, "Failed to parse items", je);
			return null;
		}
		return last;
	}
}

