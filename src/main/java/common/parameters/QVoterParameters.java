package common.parameters;

/**
 * Parameters of q-voter model in $$l_2$$ (communication) layer
 * <p>
 * p: probability that agent acts individually
 * <p>
 * q: number of neighbours
 */
public final class QVoterParameters {
    private final double p;
    private final int q;

    public QVoterParameters() {
        this.p = 0.5;
        this.q = 3;
    }

    public QVoterParameters(double p, int q) {
        this.p = p;
        this.q = q;
    }

    public double getP() {
        return p;
    }

    public int getQ() {
        return q;
    }

    @Override
    public String toString() {
        return "QVoterParameters{" +
                "p=" + p +
                ", q=" + q +
                '}';
    }
}
