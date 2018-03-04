/**
 * Core class used to train data set and determine weights to
 * properly predict future stock values.
 * Equations and reference material used for backpropagation can be found at
 * @ https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
 */
public class NeuralNet {
    private double[] inputs;
    private double target, output, outputNet;
    private double[][] inputWeights;
    private double[][] adjInputWeights;

    private double[] hiddenInputs;
    private double[] hiddenOutputs;
    private double[] hiddenWeights;
    private double[] adjHiddenWeights;

    /**
     * Constructor for Neural Net class that initializes arrays
     * of weights depending on size of input array.
     * @param inputVals is an adjusted array of inputs that has been
     *               normalized.
     */
    public NeuralNet(double[] inputVals, double targetVal) {
        int len = inputVals.length;
        inputs = inputVals;
        target = logisticFunction(targetVal);
        inputWeights = new double[len][len / 2];
        adjInputWeights = new double[len][len / 2];

        hiddenInputs = new double[len / 2];
        hiddenOutputs = new double[len / 2];
        hiddenWeights = new double[len / 2];
        adjHiddenWeights = new double[len / 2];
        output = 0.0;
        outputNet = 0.0;

        initializeWeights();
        initializeNetwork();
    }

    /**
     * Computes the net input values of the hidden layer nodes before
     * the logistic function is applied to them.
     * @param inputs is an adjusted array of inputs that has been
     *               normalized
     * @param inputWeights is a 2D array of inputs weights
     * @return
     */
    public void setHiddenInputs(double[] inputs, double[][] inputWeights) {
        for(int j = 0; j < hiddenInputs.length; j++){
            for(int i = 0; i < inputs.length; i++){
                hiddenInputs[j] += inputs[i]*inputWeights[i][j];
            }
        }
    }

    /**
     * Squashes each hidden input to its correct output using
     * logistic function.
     * @param hiddenInputs
     */
    public void setHiddenOutputs(double[] hiddenInputs) {
        for(int i = 0; i < hiddenInputs.length; i++){
            hiddenOutputs[i] = logisticFunction(hiddenInputs[i]);
        }
    }

    /**
     * Computes final output after logistic function
     * is applied to net output.
     * @param hiddenOutputs outputs computed by setHiddenOutputs
     * @param hiddenWeights weights associated with hidden outputs
     */
    public void setOutput(double[] hiddenOutputs, double[] hiddenWeights) {
        for(int i = 0; i < hiddenOutputs.length; i++){
            outputNet += hiddenOutputs[i]*hiddenWeights[i];
        }
        output = logisticFunction(outputNet);
    }

    /**
     * Initialze all weights connected to input layer,
     * and initialize all weights connected to hidden layer to
     * random value.
     */
    public void initializeWeights(){
        for(int i = 0; i < inputWeights.length; i++){
            for(int j = 0; j < inputWeights[0].length; j++){
                inputWeights[i][j] = Math.random();
            }
        }

        for(int i = 0; i < hiddenWeights.length; i++){
            hiddenWeights[i] = Math.random();
        }
    }

    /**
     * Calls 4 initializing methods that set initial weights,
     * then calculated hidden layer attributes and sets initial
     * output.
     */
    public void initializeNetwork(){
        setHiddenInputs(inputs, inputWeights);
        setHiddenOutputs(hiddenInputs);
        setOutput(hiddenOutputs, hiddenWeights);
    }

    /**
     * Logistic function used to squash data.
     * @param x value to be squashed.
     * @return value from the function f(x) = 1 / (1 + e^-x)
     */
    public static double logisticFunction(double x){
        return 1 / (1 + Math.pow(Math.E, -x));
    }

    /**
     * After all weights are adjusted and stored, copy to the actual weights.
     */
    public void copyWeights(){
        for(int i = 0; i < hiddenWeights.length; i++) {
            hiddenWeights[i] = adjHiddenWeights[i];
        }
        for(int i = 0; i < inputWeights.length; i++) {
            for (int j = 0; j < inputWeights[0].length; j++) {
                inputWeights[i][j] = adjInputWeights[i][j];
            }
        }
    }

    /**
     * This method trains the data set to adjust weights until the correctly predict
     * or converge to the target value. We can then use these adjusted weights to predict
     * future values.
     */
    public void train() {
        while(Math.abs(target - output) < 0.000001) {
            /* Adjust hidden weights first according to the backpropagation rule of weight adjustments. */
            for (int i = 0; i < hiddenWeights.length; i++) {
                adjHiddenWeights[i] = hiddenWeights[i] - (0.5 * ((output - target) * output * (1 - output) * hiddenOutputs[i]));
            }

            /* Adjust input weights according to backpropagation rule of weight adjustment */
            for (int i = 0; i < inputWeights.length; i++) {
                for (int j = 0; j < inputWeights[0].length; j++) {
                    adjInputWeights[i][j] = inputWeights[i][j] - (0.5 * ((output - target) * (output * (1 - output)) * (hiddenOutputs[i] * (1 - hiddenOutputs[i])) * inputs[i]));
                }
            }

            copyWeights();
            initializeNetwork();
        }
    }

    /**
     * Once weights are adjusted by training, this method
     * will return the expected value.
     * @param newInputs is the new list of inputs that you want to predict from
     * @return the unpacked output
     */
    public double predict(double[] newInputs){
        inputs = newInputs;
        initializeNetwork();
        return unpack(output);
    }

    /**
     * Inverse of the logistic function, this is used to revert output back
     * to its form before it was squashed by the logistic function.
     * @param x is the value to be 'unpacked'
     * @return the unpacked (inverse of logistic) value
     */
    public static double unpack(double x){
        return -Math.log(-1 + (1 / x));
    }
}
