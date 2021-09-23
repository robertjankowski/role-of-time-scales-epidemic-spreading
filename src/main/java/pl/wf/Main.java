package pl.wf;

import pl.wf.simulation.Simulation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        if (args.length == 2) {
            String configPath = args[0];
            String prefixFile = args[1];
            Simulation simulation = new Simulation(configPath);
            simulation.runAll(prefixFile);
        }
    }
}
