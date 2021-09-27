package src;

import java.util.ArrayList;

public class Transition {
    private int from;
    private int to;
    private ArrayList<String> read;

    public int getFrom() { return from; }
    public void setFrom(int f) { this.from = f; }

    public int getTo() { return to; }
    public void setTo(int t) { this.to = t; }

    public ArrayList<String> getRead() { return read; }
    public void setRead(ArrayList<String> r) { this.read = r; }

    public Transition(int f, int t, ArrayList<String> r){
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
