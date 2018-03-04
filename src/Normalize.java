/**
 * Normalize Class to normalize input for ANN. Both functions referenced can be found from
 * http://www.heatonresearch.com/encog/ Utility class
 */
public class Normalize {
    private double dataHigh;
    private double dataLow;
    private double normalizedHigh;
    private double normalizedLow;

    /**
     * Construct the normalization utility, allow the normalization range to be specified.
     * @param high The high value for the input data.
     * @param low  The low value for the input data.
     * @param normHigh The high value for the normalized data.
     * @param normLow  The low value for the normalized data.
     */
    public Normalize(double high, double low, double normHigh, double normLow) {
        dataHigh = high;
        dataLow = low;
        normalizedHigh = normHigh;
        normalizedLow = normLow;
    }

    public double deNormalize(double norm) {
        return (((norm + 1)*(dataHigh - dataLow)) / 2) + dataLow;
    }

    /**
     * Normalize x.
     * @param x The value to be normalized.
     * @return The result of the normalization.
     */
    public double normalize(double x) {
        return ((x - dataLow)
                / (dataHigh - dataLow))
                * (normalizedHigh - normalizedLow) + normalizedLow;
    }
}
