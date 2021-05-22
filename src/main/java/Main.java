import simulation.Simulation;

public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation("config/test_q_voter_phase.yaml");
        simulation.runAll("test_q_voter_phase");
    }
}