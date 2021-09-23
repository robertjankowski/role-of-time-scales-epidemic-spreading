package pl.wf;

import pl.wf.simulation.Simulation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation("config/test_filter_bubbles.yaml");
        simulation.runAll("on");
    }
}
