package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestClass {
    public static void main(String[] args) {
        List<Node> nodes = new ArrayList<Node>();
        Path file = Paths.get("PPInetwork.txt");
        try(BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] twoNodes = line.split("\t");
                if (twoNodes[0].equals(twoNodes[1])) System.out.println(twoNodes[0]);
                for (String str: twoNodes) {
                    if (nodes.indexOf(new Node(str)) == -1) {
                        nodes.add(new Node(str));
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Total nodes: " + nodes.size());
        nodes.forEach(node -> System.out.print(node.getName() + " "));
    }
}
