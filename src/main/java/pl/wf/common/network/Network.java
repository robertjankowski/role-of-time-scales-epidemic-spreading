package pl.wf.common.network;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.SupplierUtil;
import pl.wf.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Network {

    private static Layer barabasiAlbertModel(int n, int m) {
        var gen = new BarabasiAlbertGraphGenerator<Integer, DefaultEdge>(m, m, n);
        Supplier<DefaultEdge> sEdge = DefaultEdge::new;
        Layer g = new Layer(SupplierUtil.createIntegerSupplier(), sEdge, false);
        gen.generateGraph(g);
        return g;
    }

    private static void addRandomlyEdges(Layer g, int nEdges) {
        var counter = 0;
        while (counter < nEdges) {
            var node = Utils.getRandomSetElement(g.vertexSet());
            var connected = g.edgesOf(node)
                    .stream()
                    .map(g::getEdgeTarget)
                    .collect(Collectors.toSet());
            var unconnected = Utils.setDifference(g.vertexSet(), connected);
            if (unconnected.isEmpty()) {
                continue;
            }
            var newNode = Utils.getRandomSetElement(unconnected);
            if (g.containsEdge(node, newNode) || newNode.equals(node)) {
                continue;
            }
            g.addEdge(node, newNode);
            counter++;
        }
    }

    private static Layer powerlawClusterGraph(int n, int m, double p) {
        var gen = new PowerlawClusterGraphGenerator<Integer, DefaultEdge>(m, m, n, p);
        Supplier<DefaultEdge> sEdge = DefaultEdge::new;
        Layer g = new Layer(SupplierUtil.createIntegerSupplier(), sEdge, false);
        gen.generateGraph(g);
        return g;
    }

    /**
     * Create bilayer network with additional `additional_virtual_links` in virtual layer.
     *
     * @param size                   Network size
     * @param additionalVirtualLinks Number of additional edges in virtual layer
     * @param m                      the number of random edges to add for each new node
     * @param p                      probability of adding a triangle after adding a random edge
     * @return two layers, epidemic and virtual one
     */
    public static Pair<Layer, Layer> createBilayerNetwork(int size, int additionalVirtualLinks, int m, double p) {
        Layer l1;
        if (p == 0) {
            l1 = barabasiAlbertModel(size, m);
        } else {
            l1 = powerlawClusterGraph(size, m, p);
        }
        var l2 = (Layer) l1.clone();
        addRandomlyEdges(l2, additionalVirtualLinks);
        return new Pair<>(l1, l2);
    }

    public static List<Integer> getDegrees(Layer l, List<Integer> nodeMapping) {
        var vertices = l.vertexSet().stream().sorted().collect(Collectors.toList());
        List<Integer> degrees = new ArrayList<>();
        for (Integer n : nodeMapping) {
            var currentNode = vertices.get(n);
            var currentDegree = l.degreeOf(currentNode);
            degrees.add(currentDegree);
        }
        return degrees;
    }

    public static List<Integer> getDegrees(Layer l) {
        var vertices = l.vertexSet().stream().sorted().collect(Collectors.toList());
        var nodeMapping = getDefaultNodeMapping(vertices.size());
        return getDegrees(l, nodeMapping);
    }

    public static double pearsonCorrelation(Pair<Layer, Layer> layers, List<Integer> nodeMapping) {
        var l1 = layers.getFirst();
        var l2 = layers.getSecond();

        var d1 = getDegrees(l1).stream().mapToDouble(i -> i).toArray();
        var d2 = getDegrees(l2, nodeMapping).stream().mapToDouble(i -> i).toArray();
        var d1Mean = Arrays.stream(d1).average().getAsDouble();
        var d2Mean = Arrays.stream(d2).average().getAsDouble();

        StandardDeviation sd = new StandardDeviation();
        var sd1 = sd.evaluate(d1);
        var sd2 = sd.evaluate(d2);

        double d12Mean = 0;
        for (int i = 0; i < d1.length; i++)
            d12Mean += d1[i] * d2[i];
        d12Mean /= d1.length;
        var top = d12Mean - d1Mean * d2Mean;
        var bottom = sd1 * sd2;
        return top / bottom;
    }

    public static List<Integer> createNodeMapping(int size, boolean degreeCorrelated) {
        var mapping = getDefaultNodeMapping(size);
        if (!degreeCorrelated)
            Collections.shuffle(mapping);
        return mapping;
    }

    public static List<Integer> getDefaultNodeMapping(int size) {
        return IntStream.range(0, size).boxed().collect(Collectors.toList());
    }
}
