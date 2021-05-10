package common;

public class Agent {
    private static int ID;
    private final int id;
    private int opinion;
    private int infectedTime;

    public Agent(int opinion, int infectedTime) {
        this.opinion = opinion;
        this.infectedTime = infectedTime;
        this.id = ID++;
    }

    public Agent() {
        // All agent at the beginning has a positive opinion
        this.opinion = 1;
        this.infectedTime = 0;
        this.id = ID++;
    }

    public int getOpinion() {
        return opinion;
    }

    public void setOpinion(int opinion) {
        this.opinion = opinion;
    }

    public int getInfectedTime() {
        return infectedTime;
    }

    public void setInfectedTime(int infectedTime) {
        this.infectedTime = infectedTime;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", opinion=" + opinion +
                ", infectedTime=" + infectedTime +
                '}';
    }
}
