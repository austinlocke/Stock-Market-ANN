import java.util.Scanner;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * This is a helper class that fetches a JSON from Alpha Vantage's
 * API using java's HttpURLConnection class.
 * Documentation for API @ https://www.alphavantage.co/documentation/
 * Reference for using HttpURLConnection
 * @ https://alvinalexander.com/blog/post/java/how-open-url-read-contents-httpurl-connection-java
 *
 */
public class AlphaVantage {
    private String results;
    private String url;
    private String key = "PW12Q28NI8MRHCOJ";

    /**
     * Contructs AlphaVantageAPI URL with desired time interval(function)
     * and stock(ticker).
     * @param function determines the time interval that AlphaVantage will return.
     * @param ticker is the stock ticker that is being queried.
     */
    public AlphaVantage(String function, String ticker) {
        url = "https://www.alphavantage.co/query?function="
        + function + "&symbol=" + ticker + "&interval=1min&apikey=" + key;
        results = null;
    }

    /**
     * Makes the connection to desired URL and stores results from url
     * in results.
     *
     */
    public void makeURLConnection() throws Exception {
        URL myURL;
        Scanner reader = null;
        StringBuilder json;

        try{
            // Attempt to make and open URL connection.
            myURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) myURL.openConnection();

            // Make GET request, set timeout to 15 seconds, and connect.
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15*1000);
            connection.connect();

            // Read json from the server
            reader = new Scanner(new InputStreamReader(connection.getInputStream()));
            json = new StringBuilder();

            // Read until empty and append newline.
            while (reader.hasNext()) {
                json.append(reader.nextLine() + '\n');
            }

            // Save string to results.
            results = json.toString();
        }

        // High level exception catch.
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            // Close the reader and catch any exception it throws.
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns server output (json) as string.
     * @return JSON representation of Stock data
     */
    public String getJSON() {
        return results;
    }
}
