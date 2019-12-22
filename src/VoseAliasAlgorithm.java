import java.util.*;

/**
 * Implementation of Vose's alias algorithm for weighted distribution.
 *
 * references:  http://www.keithschwarz.com/darts-dice-coins/
 *              http://alaska-kamtchatka.blogspot.com/2011/12/voses-alias-method.html
 *
 * @author Josh Coulter
 * @version December 2019
 */
public class VoseAliasAlgorithm {
    private final static Random random = new Random();
    private final int[] alias;
    private final double[] probability;

    /**
     * Set-up alias and probability tables using given weight distribution array.
     *
     * @param weights supplied weight distributions
     */
    public VoseAliasAlgorithm(double[] weights){
        if (weights.length == 0) throw new NullPointerException();
        int n = weights.length;

        alias = new int[n];
        probability = new double[n];

        //Calculate scale for weights
        double sum = 0.0;
        for (double d : weights) sum += d;
        double scale = n / sum;

        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();

        //scale probabilities
        for (int i = 0; i < n; i++){
            weights[i] *= scale;
        }

        //add each probability to relevant worklist
        for (int i = 0; i < n; i++){
            if (weights[i] < 1) small.add(i);
            else large.add(i);
        }


        while (!small.isEmpty() && !large.isEmpty()){
            int s = small.removeLast();
            int l = large.removeLast();

            probability[s] = weights[s];
            alias[s] = l;

            weights[l] = (weights[l] + weights[s]) - 1;
            if (weights[l] < 1) small.add(l);
            else large.add(l);
        }

        while (!large.isEmpty()) {
            int l = large.removeLast();
            probability[l] = 1;
        }
        while (!small.isEmpty()){
            int s = small.removeLast();
            probability[s] = 1;
        }
    }

    /**
     * Generates a semi-random index using the probability and alias tables.
     *
     * @return weighted random index
     */
    public int getValue(){
        double rDouble = probability.length * random.nextDouble();
        int intConvert = (int) Math.floor(rDouble);
        double prob = rDouble - (double) intConvert;

        return prob <= probability[intConvert] ? intConvert : alias[intConvert];
    }
}
