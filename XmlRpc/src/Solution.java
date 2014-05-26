import java.io.Serializable;

public class Solution implements Serializable {
    public Solution(double x1, double x2) {
        this.x1 = x1;
        this.x2 = x2;
        isExist = true;
    }

    private Solution() {
        isExist = false;
        this.x1 = 0;
        this.x2 = 0;
    }

    public static Solution DoesntExist() {
        return new Solution();
    }

    public boolean isExist() {
        return isExist;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public boolean isExist;
    public double x1;
    public double x2;
}

