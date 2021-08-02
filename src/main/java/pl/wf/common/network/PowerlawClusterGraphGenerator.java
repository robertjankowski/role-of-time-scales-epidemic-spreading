package pl.wf.common.network;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import pl.wf.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Holme and Kim algorithm for growing graphs with powerlaw
 * degree distribution and approximate average clustering.
 * <p>
 * BA code was used from JGraphT library
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class PowerlawClusterGraphGenerator<V, E> implements GraphGenerator<V, E, V> {
    private final Random rng;
    private final int m0;
    private final int m;
    private final int n;
    private final double p;

    public PowerlawClusterGraphGenerator(int m0, int m, int n, double p) {
        this.p = p;
        this.rng = new Random();
        this.m0 = m0;
        this.m = m;
        this.n = n;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        Set<V> oldNodes = new HashSet<>(target.vertexSet());
        Set<V> newNodes = new HashSet<>();
        new CompleteGraphGenerator<V, E>(m0).generateGraph(target, resultMap);
        target.vertexSet().stream().filter(v -> !oldNodes.contains(v)).forEach(newNodes::add);

        List<V> nodes = new ArrayList<>(n * m);
        nodes.addAll(newNodes);

        for (int i = 0; i < m0 - 2; i++) {
            nodes.addAll(newNodes);
        }

        for (int i = m0; i < n; i++) {
            V source = target.addVertex();

            List<V> newEndpoints = new ArrayList<>();
            V u = nodes.get(rng.nextInt(nodes.size()));

            target.addEdge(source, u);
            newEndpoints.add(source);
            newEndpoints.add(u);
            int added = 1;

            while (added < m) {
                if (rng.nextDouble() < p) { // Clustering step: add triangle
                    var neighbours = Graphs.neighborListOf(target, u).stream()
                            .filter(nbr -> nbr != source)
                            .filter(nbr -> !target.containsEdge(source, nbr))
                            .collect(Collectors.toList());
                    if (!neighbours.isEmpty()) {
                        var nbr = Utils.getRandomListElement(neighbours);
                        target.addEdge(source, nbr);
                        newEndpoints.add(nbr);
                        newEndpoints.add(source);
                        added++;
                        continue;
                    }
                }
                u = nodes.get(rng.nextInt(nodes.size()));
                if (!target.containsEdge(source, u)) {
                    target.addEdge(source, u);
                    added++;
                    newEndpoints.add(source);
                    if (i > 1) {
                        newEndpoints.add(u);
                    }
                }
            }
            nodes.addAll(newEndpoints);
        }
    }
}
