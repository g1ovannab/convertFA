package src;

import java.util.ArrayList;

public class Automaton {
    private ArrayList<State> states;
    private ArrayList<Transition> transitions;

    public ArrayList<State> getStates() { return states; }
    public void setStates(ArrayList<State> s) { this.states = s; }

    public State getSpecificState(String id) {
        for (State s : states) if (s.getID() == id) return s;
        return null;
    }

    public ArrayList<Transition> getTransitions() { return transitions; }
    public void setTransitions(ArrayList<Transition> t) { this.transitions = t; }

    public ArrayList<Transition> getSpecificTransition(String from, String read) {
        ArrayList<Transition> ts = new ArrayList<>();
        for (Transition t : transitions) if (t.getFrom().equals(from) && t.getRead().contains(read)) ts.add(t);
        return ts;
    }

    public Automaton(ArrayList<State> s, ArrayList<Transition> t){
        this.states = s;
        this.transitions = t;
    }

    public Automaton() {
        this.states = null;
        this.transitions = null;
    }
}
