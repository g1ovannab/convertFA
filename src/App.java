package src;

import java.io.*;
import java.util.*;

public class App{

    // generic variables
    public static String line = "";
    public static ArrayList<String> sentences = new ArrayList<>();
    public static List<String> alphabet = new ArrayList<>();
    public static LinkedHashMap<String, String[]> transitionTable = new LinkedHashMap<>();

    // NFA variable(s)
    public static Automaton NFA = new Automaton();

    // FA variable(s)
    public static Automaton DFA = new Automaton();

    public static void main(String[] args) throws Exception {
        ImportFiles();

        ConvertFA();

        ExportFiles();
    }

    public static void ExportFiles() {

    }

    public static void ConvertFA(){ 
        String[] symbolsOfReading = alphabet.toArray(new String[alphabet.size()]);
        Arrays.sort(symbolsOfReading);

        getTransitionalTable(symbolsOfReading);

        /**
         *              read
         * ---------------------------
         * state  |   0    |   1   |
         *   0    |   0    |  0,1  |
         *   1    |   2    |   2   |
         *   2    |   3    |   3   |
         *   3    |   x    |   x   |
         * 
        */

        getInitialStatesFromDFA(symbolsOfReading);

        setNewStatesFromDFA(symbolsOfReading);

    }

    public static void ImportFiles() throws IOException {
        /* NFA Region */
        File nfa = new File("rcv-folder/nfa.jff");
        FileReader frNFA = new FileReader(nfa);
        BufferedReader brNFA = new BufferedReader(frNFA);

        line = ReadLine(brNFA);
        ReadNFA(brNFA);
        brNFA.close();



        /* Sentences Region */
        File sentencesFile = new File("rcv-folder/sentences.txt");
        FileReader frS = new FileReader(sentencesFile);
        BufferedReader brS = new BufferedReader(frS);

        line = brS.readLine();
        ReadSentences(brS);
        brS.close();
    }

    public static void ReadSentences(BufferedReader br) throws IOException {
        do {
            sentences.add(line);
            line = br.readLine();
        } while (line != null);
    }

    public static void ReadNFA(BufferedReader br) throws IOException{        
        ArrayList<State> states = new ArrayList<>();
        ArrayList<Transition> transitions = new ArrayList<>();
        
        do {
            if (line.contains("<?xml") || line.contains("<type>") || line.contains("<automaton>") || line.contains("<!--") || line.contains("</automaton>")) {
                line = ReadLine(br);
                continue;
            } else if (line.contains("<state")){ // <state id="0" name="q0">
                String[] stateSplited = line.split(" ");
                // [0] <state       [1] id="0"      [2] name="q0">
    
                String id = stateSplited[1]; // id="0"
                id = id.substring(0, id.length() - 1); // id="0
                id = id.substring(4, id.length()); //0
    
                State state = new State();
                state.setID(id);
    
                line = ReadLine(br);
                // at this point, line can be 3 things: the closing tag </state>, the <initial/> tag or the <final/> tag
                
                while (!line.contains("</state>")){
                    if (line.contains("<x>") || line.contains("<y>")) {
                        line = ReadLine(br);
                        continue;
                    } else if (line.contains("<initial/>")) state.setInitial(true);
                    else if (line.contains("<final/>")) state.setFinal(true);

                    line = ReadLine(br);
                }

                states.add(state);

            } else if (line.contains("<transition")){
                Transition transition = new Transition();
                line = ReadLine(br);
                
                do {
                    if (line.contains("<from>")) transition.setFrom(line = line.replace("<from>", "").replace("</from>", ""));
                    else if (line.contains("<to>")) transition.setTo(line = line.replace("<to>", "").replace("</to>", ""));
                    else if (line.contains("<read>")) {
                        String read = line.replace("<read>", "").replace("</read>", "").replace(" ", "");
                        transition.setRead(read);
                        AddReadToAlphabet(read);
                    }
                    line = ReadLine(br);
                } while (!line.contains("</transition>"));
                
                transitions.add(transition);
            }
            line = ReadLine(br);
        } while (!line.contains("</structure>"));

        NFA.setStates(states);
        NFA.setTransitions(transitions);
    }

    public static String ReadLine(BufferedReader br) throws IOException {
        String aux = br.readLine().replaceAll("\t", "");;
        if (aux.contains("&#13;")) aux = aux.replace("&#13;", "");
        return aux;
    }

    public static void AddReadToAlphabet(String read){
        String[] split = read.split(",");

        if (split.length == 1){
            String character = split[0];
            if (!alphabet.contains(character)) alphabet.add(character);
        } else if (split.length > 1) for (String c : split) if (!alphabet.contains(c)) alphabet.add(c);
    }

    public static void getTransitionalTable(String[] symbolsOfReading){
        for (State states : NFA.getStates()) {
            int aux = 0;
            String from = states.getID();

            /* this represents literally the transition where the state start for each symbol
            of the alphabet */
            String[] toFromEachState = new String[alphabet.size()];

            for (String symbol : symbolsOfReading) {
                /* this keeps the transition(s) where we get the 'from' state, and the symbol that he
                reads */
                ArrayList<Transition> transitionsRead = NFA.getSpecificTransition(from, symbol);

                // all the to's from a state passing with a given read
                /* an exemple would be if we have 2 states (0 and 1). the state 0 reads a & b in a loop, 
                and reads b to go to state 1. this variable shows that when we read 1, we can stay either in 
                state 0 or go to state 1.*/
                String allTo = "";

                for (Transition t : transitionsRead) {
                    String to = "";
                    if (t != null) to = t.getTo();
                    allTo = allTo.concat(to + ",");
                }

                if (allTo.endsWith(",")) allTo = allTo.substring(0, allTo.length() - 1);
                toFromEachState[aux] = allTo;
                aux++;
            }
            /* as in the exemple given before, a table row would look like this:
            
            | state |  reads  |
            |       | a |  b  |
            |   0   | 0 | 0,1 |
            */
            transitionTable.put(from, toFromEachState);
        }
    }

    public static Boolean stateExists(String id, Automaton FA){
        ArrayList<State> states = FA.getStates();
        for (State state : states) if (state.getID() == id) return true;
        return false;
    }

    public static void getInitialStatesFromDFA(String[] symbolsOfReading){
        ArrayList<State> states = new ArrayList<>();

        for (String state : transitionTable.keySet()) {
            for (int i = 0; i < symbolsOfReading.length; i++){
                // this 'to' variable, keeps the state(s) where we go after we read the symbol
                String to = transitionTable.get(state)[i];
                String[] splitedTo = to.split(",");

                if (splitedTo.length == 1 && stateExists(state, NFA)){
                    State s = new State(state, NFA.getSpecificState(state).isInitial(), NFA.getSpecificState(state).isFinal(), false);
                    if (AddState(s, states)) states.add(s);
                } else if (splitedTo.length > 1){
                    // so far, ours states were onlyu 1 digit
                    State s = new State(to.replace(",", ""), false, false, true);
                    states.add(s);
                }
            }
        }
        DFA.setStates(states);
    }

    public static Boolean AddState(State state, ArrayList<State> states){
        if (states.size() > 0) {
            for (State s : states)  if (s.getID().equals(state.getID())) return false;
            return true;
        }
        else return true;
    }

    public static void setNewStatesFromDFA(String[] symbolsOfReading){
        Boolean moreStatesToAdd = false;

        do {
            for (State statesSoFar : DFA.getStates()) {
                if (statesSoFar.addToTable()){
                    // we need to split to make the math of states 'to'
                    String[] splitedStates = statesSoFar.getID().split("");

                    /* this represents literally the transition where the state start for each symbol
                    of the alphabet */
                    String[] toFromEachState = new String[alphabet.size()];
                    
                    int aux = 0;
                    for (int i = 0; i < symbolsOfReading.length; i++){
                        String transitionNewState = "";

                        /* for each symbol, and for each "internal" state, we will concat the new states */
                        for (String state : splitedStates) {
                            transitionNewState = transitionNewState.concat(transitionTable.get(state)[i] + ","); //[0]
                        }
                        
                        /* if the states are 0 and 02, the result will be 02, once its a union */
                        transitionNewState = unionStates(transitionNewState);

                        toFromEachState[aux] = transitionNewState;
                        aux++;
                    }

                    /* the new variable 'toFromEachState' will 'sound' like:
                    | state |   reads  |
                    |       | a  |  b  |
                    |   01  | 02 | 012 |
                    */ 
                    
                    ArrayList<State> statesDFA = DFA.getStates();
                    // update the state to not add to the transition table once we already did
                    for (int i = 0; i < statesDFA.size(); i++) {
                        if (statesDFA.get(i).getID().equals(statesSoFar.getID())) statesDFA.get(i).setAdd(false); 
                    }

                    transitionTable.put(statesSoFar.getID(), toFromEachState);

                    // add the new state to DFA
                    for (String string : toFromEachState) {
                        if (!stateExists(string, DFA)) {
                            statesDFA.add(new State(string, false, false, true));
                            moreStatesToAdd = true;
                        }
                    }

                    // we need a way to update the variable 'moreStatesToAdd' to false, otherwise it'll be in a loop
                }
            }
        } while (moreStatesToAdd);
    }

    public static String unionStates(String transitionOfNewState){
        String[] transition = transitionOfNewState.split("");
        ArrayList<String> aux = new ArrayList<>();

        for (String t : transition) {
            if (!t.equals(",")) {
                if (aux.size() == 0) aux.add(t);
                else if (aux.size() > 0 && !aux.contains(t)) aux.add(t);
            }
        }
        
        String unionOfTransitions = "";
        for (String string : aux) unionOfTransitions = unionOfTransitions.concat(string);

        return unionOfTransitions;
    }
}
