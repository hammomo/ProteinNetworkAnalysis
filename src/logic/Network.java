package logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Network {
    Set<Node> nodes;
    Set<Edge> edges;
    Map<Node, Integer> fullDistribution = new HashMap<Node, Integer>();

    public Network() {
        this.nodes = new HashSet<Node>();
        this.edges = new HashSet<Edge>();
    }

    public static void main(String[] args) {
        Network test = new Network();
        test.createNetworkFromFile("PPInetwork.txt");
        Set<Node> nodes = test.findHubs();
        nodes.forEach(node -> System.out.println(node.getName()));
        test.saveDegreeDistribution("test.txt");
    }

    /**
     * Just a test function
     * print all the nodes and edges, as well as their counts
     */
    public void testFunction() {
        System.out.println("Total nodes: " + nodes.size());
        nodes.forEach(node -> System.out.print(node.getName() + " "));
        System.out.println("\nTotal edges: " + edges.size());
        edges.forEach(edge -> System.out.println(edge.getNodes()[0].getName() + " " + edge.getNodes()[1].getName() + "****"));
    }

    /**
     * Import thw whole network from a file
     * @param filename
     */
    public void createNetworkFromFile(String filename) {
        Path file = Paths.get(filename);
        try(BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] twoNodes = line.split("\t");
                for (String str: twoNodes) {
                    nodes.add(new Node(str));
                }
                Edge tmp = new Edge(new Node(twoNodes[0]), new Node(twoNodes[1]));
                addDegreesToMap(tmp, new Node[]{new Node(twoNodes[0]), new Node(twoNodes[1])});
                edges.add(tmp);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Import protein interaction manually
     */
    public void addInteraction() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Please input one node:");
            Node node1 = new Node(reader.readLine());
            System.out.println("Please input another node:");
            Node node2 = new Node(reader.readLine());
            Edge edge = new Edge(node1, node2);
            if (edges.contains(edge)) {
                System.out.println("Interaction already exists! " + node1.getName() + " " + node2.getName());
                return;
            }
            addDegreesToMap(edge, new Node[]{node1, node2});
            edges.add(edge);
            nodes.add(node1);
            nodes.add(node2);
            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * For all added interaction, calculate the degrees for the related Nodes
     * @param edge
     * @param twoNodes
     */
    public void addDegreesToMap(Edge edge, Node[] twoNodes) {
        if (!edges.contains(edge)) {
            for (Node node: twoNodes) {
                fullDistribution.put(node, fullDistribution.containsKey(node)? fullDistribution.get(node) + 1: 1);
            }
        }
    }

    /**
     * get degree for one node
     * @param node
     * @return
     */
    public int degreeOfNode(Node node) {
        if (!nodes.contains(node)) return 0;
        return fullDistribution.get(node);
    }

    /**
     * get the average degree for the whole network
     * @return
     */
    public double averageDegree() {
        int nodeSize = fullDistribution.size();
        int sum = 0;
        for (Node node: nodes) {
            sum += degreeOfNode(node);
        }
        return (double) sum/nodeSize;
    }

    /**
     * find the hubs which contain the highest degree
     * @return
     */
    public Set<Node> findHubs() {
        List<Integer> list = degreeReverseSorter();
        int maxDegree = list.get(0);
        Set<Node> hubs = new HashSet<Node>();
        fullDistribution.forEach((key, val) -> {
            if (val == maxDegree) hubs.add(key);
        });
        return hubs;
    }

    /**
     * Save degree distribution to an ou
     * @param filename
     */
    public void saveDegreeDistribution(String filename) {
        Map<Integer, Integer> degreeOfNodes = new HashMap<Integer, Integer>();
        List<Integer> allDegrees = degreeReverseSorter();
        allDegrees.forEach(degree -> degreeOfNodes.put(degree, 0));
        fullDistribution.forEach((key, val) -> degreeOfNodes.put(val, degreeOfNodes.get(val) + 1));
        Path file = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write("degree\tnumber of nodes\n");
            for (int degree: allDegrees) {
                writer.write(degree + "\t" + degreeOfNodes.get(degree) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<Integer> degreeReverseSorter() {
        List<Integer> allDegrees = fullDistribution.values().stream().collect(Collectors.toSet())
                                                            .stream().collect(Collectors.toList());
        Collections.sort(allDegrees, Collections.reverseOrder());
        return allDegrees;
    }
}
