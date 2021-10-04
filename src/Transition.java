package src;

public class Transition {
    private int from;
    private int to;
    private String read;

    public int getFrom() { return from; }
    public void setFrom(int f) { this.from = f; }

    public int getTo() { return to; }
    public void setTo(int t) { this.to = t; }

    public String getRead() { return read; }
    public void setRead(String r) { this.read = r; }

    public Transition(int f, int t, String r){
        this.from = f;
        this.to = t;
        this.read = r;
    }

    public Transition() {
        this.from = -1;
        this.to = -1;
        this.read = null;
    }
}
