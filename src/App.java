package src;

import java.io.*;
import java.util.*;

public class App {

    // generic variables
    public static String line = "";
    public static ArrayList<String> sentences = new ArrayList<>();
    public static List<String> alphabet = new ArrayList<>();

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
        LinkedHashMap<String, String[]> transitionTable = getTransitionalTable();

        /**
         *              leitura
         * ---------------------------
         * estado |   0    |   1   |
         *   0    |   0    |  0,1  |
         *   1    |   2    |   2   |
         *   2    |   3    |   3   |
         *   3    |   x    |   x   |
         * 
        */

        getInitialStatesFromDFA(transitionTable);
        setNewStatesFromDFA(transitionTable);
 
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
            } else if (line.contains("<state")){
                // <state id="0" name="q0">&#13;
                String[] stateSplited = line.split(" ");
                // [0] <state       [1] id="0"      [2] name="q0">
    
                String id = stateSplited[1]; // id="0"
                id = id.substring(0, id.length() - 1); // id="0
                id = id.substring(4, id.length()); //0
    
                State state = new State();
                state.setID(id);
    
                line = ReadLine(br);
                // at this point, line can be 3 things: the closing tag </state>, the <initial/> tag or teh <final/> tag
                
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

    public static LinkedHashMap<String, String[]> getTransitionalTable(){
        LinkedHashMap<String, String[]> table = new LinkedHashMap<String, String[]>(); 
        String[] characters = alphabet.toArray(new String[alphabet.size()]);

        for (State s : NFA.getStates()) {
            int aux = 0;
            String from = s.getID();

            String[] toFromEachState = new String[alphabet.size()];

            for (String c : characters) {
                ArrayList<Transition> transitionsRead = NFA.getSpecificTransition(from, c);

                // all the to's from a state passing with a given read
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

            table.put(from, toFromEachState);
        }

        return table;
    }

    public static Boolean stateExists(String id){
        ArrayList<State> states = NFA.getStates();
        for (State state : states) if (state.getID() == id) return true;
        return false;
    }

    public static void getInitialStatesFromDFA(LinkedHashMap<String, String[]> transitionTable){
        ArrayList<State> states = new ArrayList<>();
        String[] characters = alphabet.toArray(new String[alphabet.size()]);

        for (String state : transitionTable.keySet()) {
            for (String c : characters) {
                String to = transitionTable.get(state)[Integer.parseInt(c)];
                String[] splitedTo = to.split(",");

                if (splitedTo.length == 1 && stateExists(state)){
                    State s = new State(state, NFA.getSpecificState(state).isInitial(), NFA.getSpecificState(state).isFinal(), false);
                    
                    // if (!states.contains(states.get(Integer.parseInt(s.getID())))) states.add(s);
                    if (AddState(s, states)) states.add(s);
                } else if (splitedTo.length > 1){
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

    public static void setNewStatesFromDFA(LinkedHashMap<String, String[]> transitionTable){
        
        for (State s : DFA.getStates()) {
            if (s.addToTable()){
                
            }
        }
    }
}
