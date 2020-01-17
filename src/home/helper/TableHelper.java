package home.helper;

import javafx.beans.property.SimpleIntegerProperty;

public class TableHelper {
    public final SimpleIntegerProperty degree;
    public final SimpleIntegerProperty num;

    public TableHelper(int degree, int num) {
        this.degree = new SimpleIntegerProperty(degree);
        this.num = new SimpleIntegerProperty(num);
    }

    public int getDegree() {
        return degree.get();
    }

    public int getNum() {
        return num.get();
    }
}
