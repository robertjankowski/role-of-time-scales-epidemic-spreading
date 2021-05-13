package common.agent;

import common.SimulationConfig;

public class Agent {
    private static int ID;
    private final int id;
    private int opinion;
    private double infectedTime;
    private AgentState state;
    private boolean hasIllnessA;
    private boolean hasIllnessB;
    private int age;

    public Agent(int opinion) {
        this.id = ID++;
        this.opinion = opinion;
        this.infectedTime = 0;
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
            // TODO: for now when simulations are preformed on multilayer we do not consider comordibities
            if (opinion == 1) {
                infectedTime += 2;
            } else {
                infectedTime++;
            }
        } else {
            if (config.isComorbidities()) {
                // Illness A does not impact the maximum infected time
                if (hasIllnessB) {
                    infectedTime += 0.5;
                } else {
                    infectedTime++;
                }
            } else {
                infectedTime++;
            }
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
}
