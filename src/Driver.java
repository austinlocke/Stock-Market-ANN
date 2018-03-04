import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Driver class with main function to begin execution.
 * This driver has minimal functionality and relies on
 * helper classes.
 */
public class Driver {
    /**
     * Utility function to convert list of doubles into an array
     * for faster calculations. A list is originally used because we
     * do not know how many elements the data set will have.
     * @param list containing raw stock values
     * @return reference to array of doubles with same length as list
     */
    public static double[] listToArray(ArrayList<Double> list) {
        double[] arr = new double[list.size()];
        for(int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    /**
     * This method justs creates a new ArrayList and copy values
     * that were passed to it
     * @param list is the list of values to be copied.
     * @return is the reference to the new list
     */
    public static ArrayList<Double> copyList(ArrayList<Double> list){
        ArrayList<Double> copy = new ArrayList<>();
        for(double i: list)
            copy.add(i);
        return copy;
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Before you get started here are some basic stock tickers!");
        System.out.println("Google: GOOGL, Microsoft: MSFT, Facebook: FB, Netflix: NFLX");
        System.out.println("Intel: INTC, Amazon: AMZN, Wal-Mart: WMT\n");
        Scanner keyboard = new Scanner(System.in);
        String goAgain = "y";

        while(goAgain.equals("y") || goAgain.equals("Y")) {

            // Get stock and interval from user.
            System.out.print("Enter a stock ticker you are interested in: ");
            String ticker = keyboard.nextLine();
            System.out.print("Enter D, W, or M for next day, week, month price: ");
            String interval = keyboard.nextLine().toLowerCase();
            String time;
            String function;

            // Set API values according to user input.
            switch (interval) {
                case "d":
                    function = "TIME_SERIES_DAILY";
                    time = "day";
                    break;
                case "w":
                    function = "TIME_SERIES_WEEKLY";
                    time = "week";
                    break;
                case "m":
                    function = "TIME_SERIES_MONTHLY";
                    time = "month";
                    break;
                default:
                    function = "TIME_SERIES_WEEKLY";
                    time = "week";
            }

            // Make connection to AlphaVantage API and get raw string data.
            AlphaVantage connection = new AlphaVantage(function, ticker);
            connection.makeURLConnection();
            String data = connection.getJSON();

            // Pass raw data to parser class to get an ArrayList of valid values.
            JSONParse parser = new JSONParse(data);
            parser.parse();
            ArrayList<Double> rawVals = parser.getInputs();
            ArrayList<Double> inputs = new ArrayList<>();

            /* Only use first 4 inputs in data set. I found that any more and the old values
            hold too much weight on the predicted price. For example this will take last 4 days
            or the last 4 weeks or the last 4 months of a data set.
             */
            for (int i = 0; i < 4; i++) {
                inputs.add(rawVals.get(i));
            }

            /* We need to similar lists for our ANN. One has last 3 values for input and
            first (most recent) value for target. Our other list is used to predict and just contains
            the most recent 3 as input and will predict the next interval value
             */
            ArrayList<Double> copyInputs = copyList(inputs);
            copyInputs.remove(copyInputs.size() - 1);
            double[] copy = listToArray(copyInputs);

            // Calculate min and maxes of both lists, used later for normalizing and de-normalizing data.
            double expected = inputs.remove(0);
            double[] arr_inputs = listToArray(inputs);
            double min = Arrays.stream(arr_inputs).min().getAsDouble();
            double max = Arrays.stream(arr_inputs).max().getAsDouble();
            double minCopy = Arrays.stream(copy).min().getAsDouble();
            double maxCopy = Arrays.stream(copy).max().getAsDouble();

            // Create two normalizer objects. One for training set and one for prediction set.
            Normalize normalizer = new Normalize(max, min, 1, -1);
            Normalize normalizerCopy = new Normalize(maxCopy, minCopy, 1, -1);
            expected = normalizer.normalize(expected);

            // Normalize both data sets.
            for (int i = 0; i < arr_inputs.length; i++) {
                arr_inputs[i] = normalizer.normalize(arr_inputs[i]);
                copy[i] = normalizerCopy.normalize(copy[i]);
            }

            // Initialize and train network.
            NeuralNet network = new NeuralNet(arr_inputs, expected);
            network.train();

            // Our predict value will still be in normalized form so de-normalized it.
            double normalizedPredict = network.predict(copy);
            double actualPredict = normalizerCopy.deNormalize(normalizedPredict);

            // Print out expected price and ask user if they want to go again.
            System.out.printf("'%s' expected price for next %s is: %.2f\n", ticker, time, actualPredict);
            System.out.print("Would you like to go again? ('y' for yes): ");
            goAgain = keyboard.nextLine().toLowerCase();
            System.out.println();
        }
    }
}
