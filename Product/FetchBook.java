package com.example.app1;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchBook extends AsyncTask<String, Void, List<Book>> {

    private AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(List<Book> output);
    }

    public FetchBook(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected List<Book> doInBackground(String... params) {
        List<Book> result = new ArrayList<>();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                return result;
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            int count = 4;
            for (int i = 0; i < count; i++) {
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                double averageRating = volumeInfo.optDouble("averageRating", -1);
                int ratingsCount = volumeInfo.optInt("ratingsCount", -1);

                // Skip books with invalid averageRating or ratingsCount
                if (averageRating == -1 || ratingsCount == -1) {
                    count++;
                    continue;
                }

                String title = volumeInfo.optString("title", "No Title");
                double price = determinePrice(volumeInfo);
                result.add(new Book(title, price));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public double determinePrice(JSONObject volumeInfo) {
        double averageRating = volumeInfo.optDouble("averageRating");
        int ratingCount = volumeInfo.optInt("ratingsCount");

        // Perform the calculation
        double calculatedPrice = Math.pow((averageRating + ratingCount), 1.6);

        // Round to 2 decimal places
        BigDecimal price = BigDecimal.valueOf(calculatedPrice);
        price = price.setScale(2, RoundingMode.HALF_UP);

        return price.doubleValue();
    }

    @Override
    protected void onPostExecute(List<Book> books) {
        super.onPostExecute(books);
        delegate.processFinish(books);
    }
}