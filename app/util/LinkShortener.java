package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import com.google.gson.Gson;

import play.Logger;
import util.bitly.BitlyResponse;

public class LinkShortener {
	private static final Gson gson = new Gson();

	public static String shorten(String longUrl) throws IOException {
		return shortenURLByBitly(longUrl);
	}

	private static String shortenURLByBitly(String longUrl) throws IOException {
		String url = null;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gujum1", "R_3bb25f225bedf407c49391046f4de415");
		int keySetSize = map.keySet().size();
		String key = map.keySet().toArray(new String[] {})[(int) (Math.random() * keySetSize)];
		URL bitLy;

		if (!longUrl.startsWith("http://")) {
			longUrl = "http://" + longUrl;
		}

		bitLy = new URL("https://api-ssl.bitly.com/v3/shorten?login=" + key
				+ "&apiKey=" + map.get(key) + "&longUrl="
				+ URLEncoder.encode(longUrl, "UTF-8"));
		BufferedReader in = new BufferedReader(new InputStreamReader(
				bitLy.openStream()));

		String inputLine;
		StringBuilder sb = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();
		String response = sb.toString();
		if (response.contains("\"status_code\": 200")) {
			BitlyResponse bitlyResonse = gson.fromJson(sb.toString(),
					BitlyResponse.class);
			url = bitlyResonse.getData().getUrl();
		}
		return url;
	}
}
