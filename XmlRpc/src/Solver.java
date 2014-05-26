public class Solver implements ISolver {
    public Solution solve(double a, double b, double c) throws Exception {
//        throw new Exception("Server!!!");

        if(a == 0)
        {
            if(b == 0)
                return Solution.DoesntExist();
            return new Solution(-c/b, -c/b);
        }

        double D = b*b - 4*a*c;
        if(D<0)
            return Solution.DoesntExist();
        double x1 = (-b + Math.sqrt(D))/(2*a);
        double x2 = (-b - Math.sqrt(D))/(2*a);
        return new Solution(x1, x2);
    }
}
