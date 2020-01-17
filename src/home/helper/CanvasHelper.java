package home.helper;

public class CanvasHelper {
    public String name;
    public double xAxis;
    public double yAxis;

    public CanvasHelper(String name){
        this.name = name;
        this.xAxis = Math.random() * 1160;
        this.yAxis = 10 + Math.random() * 770;
    }

    public void setXAxis(double rate) {
        this.xAxis = Math.random() * 1160;
    }

    public void setYAxis(double scope) {
        this.yAxis = 10 + Math.random() * 770;
    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) return false;
//        if (!(obj instanceof CanvasHelper)) return false;
//        CanvasHelper other = (CanvasHelper) obj;
//        if(!this.name.equals(other.name)) return false;
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 1;
//        hash = 31*hash + name.hashCode();
//        return hash;
//    }
}
