package home.helper;
import java.util.Random;

public class CanvasHelper {
    public String name;
    public double xAxis;
    public double yAxis;

    private double fixedWidth = 1160.0;
    private double fixedHeight = 770.0;

    /**
     * the width of the canvas is 1200.0, the height is 800.0
     * in order to place all content inside the canvas,
     * the position should be a little smaller than the canvas
     *
     * @param name
     * @param currentDegree
     * @param maxDegree
     */
    public CanvasHelper(String name, int currentDegree, int minDegree, int maxDegree){
        this.name = name;
        grantPosition(currentDegree, minDegree, maxDegree);
    }

    private void grantPosition(int currentDegree, int minDegree,int maxDegree) {
        // in case minDegree is not 1
        currentDegree = currentDegree - minDegree + 1;
        maxDegree = maxDegree - minDegree + 1;

        int numOfSegments = maxDegree * 2;
        double widthOfSegment = fixedWidth / numOfSegments;
        double heightOfSegment = fixedHeight / numOfSegments;

        // for hub
        if (currentDegree == maxDegree) {
            this.xAxis = (currentDegree - 1) * widthOfSegment + Math.random() * (2 * widthOfSegment);
            this.yAxis = 10 + (currentDegree - 1) * heightOfSegment + Math.random() * (2 * heightOfSegment);
            return;
        }

        // for non hub
        Position pos = distributePosition();
        if (pos == Position.TOP || pos == Position.BOTTOM) {
            this.xAxis = (currentDegree - 1) * widthOfSegment + Math.random() * 2 * (maxDegree - currentDegree + 1) * widthOfSegment;
            if (pos == Position.TOP) {
                this.yAxis = 10 + (currentDegree - 1) * heightOfSegment + Math.random() * heightOfSegment;
                return;
            }
            if (pos == Position.BOTTOM) {
                this.yAxis = 10 + fixedHeight - (currentDegree - 1) * heightOfSegment - Math.random() * heightOfSegment;
                return;
            }
        }

        if (pos == Position.LEFT || pos == Position.RIGHT) {
            this.yAxis = 10 + currentDegree * heightOfSegment + Math.random() * 2 * (maxDegree - currentDegree) * heightOfSegment;
            if (pos == Position.LEFT) {
                this.xAxis = (currentDegree - 1) * widthOfSegment + Math.random() * widthOfSegment;
                return;
            }
            if (pos == Position.RIGHT) {
                this.xAxis = fixedWidth - (currentDegree - 1) * widthOfSegment - Math.random() * widthOfSegment;
            }
        }

    }

    private Position distributePosition() {
        int randomNum = new Random().nextInt((4 - 1) + 1) + 1;
        if (randomNum == 1) return Position.TOP;
        if (randomNum == 2) return Position.BOTTOM;
        if (randomNum == 3) return Position.LEFT;
        return Position.RIGHT;
    }
}

enum Position {
    TOP, BOTTOM, LEFT, RIGHT;
}