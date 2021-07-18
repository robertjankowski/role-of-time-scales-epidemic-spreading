import simulation.Simulation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation("config/test_link_removal.yaml");
        simulation.runAll("", true);
    }
}