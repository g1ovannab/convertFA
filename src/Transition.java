package src;

public class Transition {
    private String from;
    private String to;
    private String read;

    public String getFrom() { return from; }
    public void setFrom(String f) { this.from = f; }

    public String getTo() { return to; }
    public void setTo(String t) { this.to = t; }

    public String getRead() { return read; }
    public void setRead(String r) { this.read = r; }

    public Transition(String f, String t, String r){
        this.from = f;
        this.to = t;
        this.read = r;
    }

    public Transition() {
        this.from = null;
        this.to = null;
        this.read = null;
    }
}
