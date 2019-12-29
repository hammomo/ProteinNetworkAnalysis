package logic;

public class Node {
    private String name;

    public Node() {
        this.name = "";
    }

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Node)) return false;
        Node other = (Node) obj;
        if(!this.name.equals(other.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31*hash + name.hashCode();
        return hash;
    }


}
