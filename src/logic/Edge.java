package logic;

public class Edge {
    private Node[] nodes = new Node[2];

    public Edge(Node node1, Node node2) {
        this.nodes[0] = node1;
        this.nodes[1] = node2;
    }

    public Node[] getNodes() {
        return nodes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        if (nodes[0].equals(other.nodes[0]) && nodes[1].equals(other.nodes[1])) return true;
        else if (nodes[1].equals(other.nodes[0]) && nodes[0].equals(other.nodes[1])) return true;
        else return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31*hash + nodes[0].hashCode() + nodes[1].hashCode();
        return hash;
    }

    public boolean contains(Node node) {
        for (Node i: nodes) if (i.equals(node)) return true;
        return false;
    }
}
