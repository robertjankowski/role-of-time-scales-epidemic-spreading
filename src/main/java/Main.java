import simulation.Simulation;

public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation("config/test_q_voter.yaml");
        simulation.runAll("test_q_voter_new_shuffle");
    }
}