package pl.wf;

import pl.wf.simulation.Simulation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation("config/example_config_model_2.yaml");
        simulation.runAll("off");
    }
}
