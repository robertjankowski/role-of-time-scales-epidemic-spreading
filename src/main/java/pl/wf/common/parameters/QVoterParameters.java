package pl.wf.common.parameters;

import java.text.DecimalFormat;

/**
 * Parameters of q-voter model in $$l_2$$ (communication) layer
 * <p>
 * p: probability that agent acts individually
 * <p>
 * q: number of neighbours
 */
public final class QVoterParameters {
    private final static DecimalFormat df = new DecimalFormat("0.00");

    private double p;
    private int q;

    public QVoterParameters() {
        this.p = 0.5;
        this.q = 3;
    }

    public QVoterParameters(double p, int q) {
        this.p = p;
        this.q = q;
    }

    public QVoterParameters(QVoterParameters other) {
        this.p = other.p;
        this.q = other.q;
    }

    public double getP() {
        return p;
    }

    public int getQ() {
        return q;
    }

    public void setP(double p) {
        this.p = p;
    }

    public void setQ(int q) {
        this.q = q;
    }

    @Override
    public String toString() {
        return "p=" + df.format(p) + "_q=" + q;
    }
}
