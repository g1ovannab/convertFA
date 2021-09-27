package src;

public class State {
    private int id;
    private boolean initialS;
    private boolean finalS;

    public int getID() { return id; }
    public void setID(int id) { this.id = id; }

    public boolean isInitial() { return initialS; }
    public void setInitial(boolean i) { this.initialS = i; }

    public boolean isFinal() { return finalS; }
    public void setFinal(boolean f) { this.finalS = f; }


    public State(int id, boolean i, boolean f){
        this.id = id;
        this.initialS = i;
        this.finalS = f;
    }


    public State() {
        this.id = -1;
        this.initialS = false;
        this.finalS = false;
    }
}