package pro1;

public class Fraction {
    private long n;
    private long d;

    public Fraction(long n, long d) {
        this.n = n;
        this.d = d;
    }

    public String toString() {
        return n + "/"+ d;
    }
}
