public class Solution {
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

    private boolean isExist;
    private double x1;
    private double x2;
}

