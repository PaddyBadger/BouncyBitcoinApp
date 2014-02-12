package com.android.bouncybitcoin;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import android.util.Log;

public class PriceFetcher {
	public static final String TAG="PriceFetcher";
	
	private static final String USD = "https://api.bitcoinaverage.com/ticker/USD";
	private static final String JSON_PRICE = "price";
	
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
	
	public String fetchPrice() {
		final String last;
		try {
			String url = Uri.parse(USD).buildUpon().build().toString();
			String jsonString = getUrl(url);
			Log.i(TAG, "Received " + jsonString);
			
			JSONObject jsonObject = (JSONObject) new JSONTokener(jsonString).nextValue();
			last = jsonObject.getString("last");
			Log.i(TAG, "String: " + last);
			
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

