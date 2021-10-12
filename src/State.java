package src;

public class State {
    private String id;
    private boolean initialS;
    private boolean finalS;
    private boolean addToTable;

    public String getID() { return id; }
    public void setID(String id) { this.id = id; }

    public boolean isInitial() { return initialS; }
    public void setInitial(boolean i) { this.initialS = i; }

    public boolean isFinal() { return finalS; }
    public void setFinal(boolean f) { this.finalS = f; }

    public boolean addToTable() { return addToTable; }
    public void setAdd(boolean add) { this.addToTable = add; }

    public State(String id, boolean i, boolean f, boolean add){
        this.id = id;
        this.initialS = i;
        this.finalS = f;
        this.addToTable = add;
    }

    public State() {
        this.id = "";
        this.initialS = false;
        this.finalS = false;
        this.addToTable = false;
    }
}