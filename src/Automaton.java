package src;

import java.util.ArrayList;

public class Automaton {
    private ArrayList<State> states;
    private ArrayList<Transition> transitions;

    public ArrayList<State> getStates() { return states; }
    public void setStates(ArrayList<State> s) { this.states = s; }

    public State getSpecificState(String id) {
        for (State s : states) if (s.getID().equals(id)) return s;
        return null;
    }

    public State getInitialState(){
        for (State s : states) if (s.isInitial()) return s;
        return null;
    }

    public Boolean stateExists(String id){
        for (State state : states) if (state.getID().equals(id)) return true;
        return false;
    }

    public Boolean addState(State state, ArrayList<State> states){
        if (states.size() > 0) {
            for (State s : states)  if (s.getID().equals(state.getID())) return false;
            return true;
        }
        else return true;
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
