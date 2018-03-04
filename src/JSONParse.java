import java.util.ArrayList;

/**
 * This class handles the parsing of the json that was returned
 * from the AlphaVantage class.
 */
public class JSONParse {
    private String json;
    private ArrayList<Double> inputs;

    /**
     * Constructor accepts json string as input and initializes
     * inputs to an ArrayList of doubles.
     * @param json
     */
    public JSONParse(String json){
        this.json = json;
        inputs = new ArrayList<Double>();
    }

    /**
     * This method parses the json and stores the list of values
     * in the ArrayList inputs.
     */
    public void parse() {
        String[] lines = json.split("\n");
        String[] split;
        String num;
        ArrayList<String> line_values = new ArrayList<>();
        CharSequence cs = "close\"";
        for(String line: lines) {
            if (line.contains(cs)) {
                split = line.split(":");
                split = split[1].split(",");
                num = split[0];
                num = num.replace("\"", "");
                inputs.add(Double.parseDouble(num));
            }
        }
    }


    /**
     *
     * @return a refernce to ArrayList inputs for ANN.
     */
    public ArrayList<Double> getInputs() {
        return inputs;
    }
}
