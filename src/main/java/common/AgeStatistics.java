package common;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AgeStatistics {

    public static List<Integer> generateAge(String filePath, int nSamples, String gender) {
        if (filePath.isEmpty()) {
            filePath = "data/poland_population_age_distribution.txt";
        }
        try {
            Path path = Paths.get(filePath);
            List<String> lines = Files.lines(path)
                    .skip(1)
                    .collect(Collectors.toList());
            List<Double> probabilities = new ArrayList<>();
            List<Integer> ages = new ArrayList<>();
            for (var line : lines) {
                var splitted = line.split("\t");
                var age = Integer.valueOf(splitted[0]);
                var males = Double.valueOf(splitted[2]);
                var females = Double.valueOf(splitted[3]);
                if (gender.equals("F")) {
                    probabilities.add(females);
                } else if (gender.equals("M")) {
                    probabilities.add(males);
                }
                ages.add(age);
            }
            var probs = probabilities.stream()
                    .mapToDouble(v -> v / probabilities.size())
                    .toArray();
            // Weighted sample of N values
            var dist = new EnumeratedIntegerDistribution(ages.stream()
                    .mapToInt(Integer::intValue)
                    .toArray(), probs);
            return Arrays.stream(dist.sample(nSamples))
                    .boxed()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
