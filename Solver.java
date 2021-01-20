import java.util.*;
import java.util.regex.*;
import java.util.function.*;
import java.util.concurrent.atomic.*;
import java.io.*;

public class Solver {
    
    public static final int MAX_SIZE = 9;
    
    private double[][] costs;
    private int size;
    /* Works no matter what order the input is in,
     * or if there are blank/invalid lines,
     * or if an edge is unspecified (and is considered maximum cost)
     */
    public void parseCosts(Scanner scanner) {
        HashMap<String, Integer> ids = new HashMap<String, Integer>();
        Pattern pattern = Pattern.compile("^(\\w+) to (\\w+) = (.+)$");
        
        AtomicInteger cityCount = new AtomicInteger();
        Function<String, Integer> adder = (s) -> cityCount.getAndIncrement();
        
        costs = new double[MAX_SIZE][MAX_SIZE];
        // an array of maximum size is created, instead of the actual size
        // a second pass or other data structure would be necessary otherwise
        for (int i = 0; i < costs.length; i++) {
            Arrays.fill(costs[i], Integer.MAX_VALUE);
            costs[i][i] = 0;
        }
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                double cost = Double.POSITIVE_INFINITY;
                try {
                    cost = Double.parseDouble(matcher.group(3));
                } catch (NumberFormatException e) {
                    continue;
                }
                String city1 = matcher.group(1);
                String city2 = matcher.group(2);
                
                int id1 = ids.computeIfAbsent(city1, adder);
                int id2 = ids.computeIfAbsent(city2, adder);
                
                costs[id1][id2] = costs[id2][id1] = cost;
            }
        }
        
        size = cityCount.get();
    }
    
    private double bruteForceHelper(int current, HashSet<Integer> unvisited) {
        if (unvisited.size() == 1) return 0;
        
        unvisited.remove(current);
        List<Integer> toVisit = List.copyOf(unvisited);
        
        double min = Integer.MAX_VALUE;
        for (int city : toVisit) {
            min = Math.min(min, costs[current][city] + bruteForceHelper(city, unvisited));
        }
        
        unvisited.add(current);
        return min;
    }
    
    public double bruteForce() {
        HashSet<Integer> unvisited = new HashSet<Integer>();
        for (int i = 0; i < size; i++) unvisited.add(i);
        
        double min = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            min = Math.min(min, bruteForceHelper(i, unvisited));
        }
        return min;
    }
    
    public Solver(Scanner scanner) {
        parseCosts(scanner);
        //System.out.println(Arrays.deepToString(costs).replace("],", "],\n"));
    }
    
    public static void main(String[] args) {
        Scanner scanner = null;
        if (args.length > 0) {
            try {
                scanner = new Scanner(new File(args[0]));
            } catch (IOException e) {
                System.err.println("File " + args[0] + " not found.");
                System.exit(404);
            }
        } else {
            scanner = new Scanner(System.in);
        }
        
        Solver solver = new Solver(scanner);
        System.out.println(solver.bruteForce());
    }
    
}