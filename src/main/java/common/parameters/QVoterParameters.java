package common.parameters;

/**
 * Parameters of q-voter model in $$l_2$$ (communication) layer
 * <p>
 * p: probability that agent acts individually
 * <p>
 * q: number of neighbours
 */
public record QVoterParameters(double p, int q) {
}
