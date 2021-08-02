package pl.wf.common.agent;

import pl.wf.common.SimulationConfig;

public class Agent {
    private static int ID = 0;
    private final int id;
    private int opinion;
    private double infectedTime;
    private double maxInfectedTime;
    private AgentState state;
    private boolean hasIllnessA;
    private boolean hasIllnessB;
    private int age;

    public Agent(int opinion, double maxInfectedTime) {
        this.id = ID++;
        this.opinion = opinion;
        this.infectedTime = 0;
        this.maxInfectedTime = maxInfectedTime;
        this.state = AgentState.SUSCEPTIBLE;
        this.hasIllnessA = false;
        this.hasIllnessB = false;
        this.age = 0;
    }

    public int getOpinion() {
        return opinion;
    }

    public void setOpinion(int opinion) {
        this.opinion = opinion;
    }

    public void flipOpinion() {
        if (opinion == 1) {
            opinion = -1;
        } else if (opinion == -1) {
            opinion = 1;
        }
    }

    public double getInfectedTime() {
        return infectedTime;
    }

    public void incrementInfectedTime(SimulationConfig config) {
        if (config.isVirtualLayer()) {
            if (opinion == 1) {
                infectedTime += 2;
            } else {
                infectedTime++;
            }
        } else {
            // None of illness impact the infected time
            infectedTime++;
        }
    }

    public void setInfectedTime(double infectedTime) {
        this.infectedTime = infectedTime;
    }

    public int getId() {
        return id;
    }

    public AgentState getState() {
        return state;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    public boolean isHasIllnessA() {
        return hasIllnessA;
    }

    public void setHasIllnessA(boolean hasIllnessA) {
        this.hasIllnessA = hasIllnessA;
    }

    public boolean isHasIllnessB() {
        return hasIllnessB;
    }

    public void setHasIllnessB(boolean hasIllnessB) {
        this.hasIllnessB = hasIllnessB;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "id=" + id +
                ", opinion=" + opinion +
                ", infectedTime=" + infectedTime +
                ", state=" + state +
                ", hasIllnessA=" + hasIllnessA +
                ", hasIllnessB=" + hasIllnessB +
                ", age=" + age +
                '}';
    }

    public double getMaxInfectedTime() {
        return maxInfectedTime;
    }

    public void setMaxInfectedTime(double maxInfectedTime) {
        this.maxInfectedTime = maxInfectedTime;
    }
}
