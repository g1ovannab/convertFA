package src;

import java.io.*;
import java.util.*;

public class App{

    // generic variables
    public static String line = "";
    public static List<String> alphabet = new ArrayList<>();
    public static String[] symbolsOfReading;
    public static LinkedHashMap<String, String[]> transitionTable = new LinkedHashMap<>();
    public static LinkedHashMap<String, String> statesEquivalents = new LinkedHashMap<>();

    // NFA variable(s)
    public static Automaton NFA = new Automaton();

    // FA variable(s)
    public static Automaton DFA = new Automaton();

    // sentence(s) variable(s)
    public static Sentence sentences = new Sentence();

    public static void main(String[] args) throws Exception {
        importFiles();
        convertFA();
        checkSentences();
        exportFiles();

        System.out.println("Check files_out folder to see the NFA converted to DFA, and the results of the sentences given.");
    }

    // basic/other methods
    public static void addReadToAlphabet(String read){
        String[] split = read.split(",");
        if (split.length == 1){
            String character = split[0];
            if (!alphabet.contains(character)) alphabet.add(character);
        } else if (split.length > 1) for (String c : split) if (!alphabet.contains(c)) alphabet.add(c);
    }

    public static void checkSentences(){
        ArrayList<Boolean> results = new ArrayList<>();

        for (String sentence : sentences.getSentences()) {

            State initialState = DFA.getInitialState();
            String nextState = initialState.getID();
            ArrayList<String> sentenceSplited = getSplittedString(sentence);

            Boolean accepted = false;

            for (int i = 0; i < sentenceSplited.size(); i++) {                
                String letter = sentenceSplited.get(i);
                
                for (int j = 0; j < symbolsOfReading.length; j++) {
                    String readOfState = transitionTable.get(nextState)[j].replace(",", "");

                    if (!readOfState.equals("") && symbolsOfReading[j].equals(letter)) {
                        nextState = readOfState;
                        break;
                    }
                }
                
                if (i == sentenceSplited.size() - 1){
                    State state = DFA.getSpecificState(nextState);
                    if (state.isFinal()) accepted = true;
                }
            }
            
            if (accepted) results.add(true);
            else results.add(false);
        }

        sentences.setResults(results);
    }
    
    public static void convertFA(){ 
        Arrays.sort(symbolsOfReading);

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

        setTransitionalTable();

        setBasicStatesToDFA();

        setNewStatesToDFA();

        setNewInitialsAndFinals();

        setNewTransitionsToDFA();
    }

    // file methods
    public static void exportFiles() throws IOException {
        /* DFA Region */
        File dfa = new File("files_out/dfa.jff");
        FileWriter fwDFA = new FileWriter(dfa);
        BufferedWriter bwDFA = new BufferedWriter(fwDFA);

        writeDFA(bwDFA);
        bwDFA.close();



        /* Sentences Result Region */
        File sentencesR = new File("files_out/sentences.txt");
        FileWriter fwSR = new FileWriter(sentencesR);
        BufferedWriter bwSR = new BufferedWriter(fwSR);

        writeSentenceResult(bwSR);
        bwSR.close();
    }

    public static void importFiles() throws IOException {
        /* NFA Region */
        File nfa = new File("rcv-folder/nfa.jff");
        FileReader frNFA = new FileReader(nfa);
        BufferedReader brNFA = new BufferedReader(frNFA);

        line = readLine(brNFA);
        readNFA(brNFA);
        brNFA.close();



        /* Sentences Region */
        File sentences = new File("rcv-folder/sentences.txt");
        FileReader frS = new FileReader(sentences);
        BufferedReader brS = new BufferedReader(frS);

        line = brS.readLine();
        readSentences(brS);
        brS.close();
    }
    
    // read methods
    public static void readNFA(BufferedReader br) throws IOException{        
        ArrayList<State> states = new ArrayList<>();
        ArrayList<Transition> transitions = new ArrayList<>();
        
        do {
            if (line.contains("<?xml") || line.contains("<type>") || line.contains("<automaton>") || line.contains("<!--") || line.contains("</automaton>")) {
                line = readLine(br);
                continue;
            } else if (line.contains("<state")){ // <state id="0" name="q0">
                String[] stateSplited = line.split(" ");
                // [0] <state       [1] id="0"      [2] name="q0">
    
                String id = stateSplited[1]; // id="0"
                id = id.substring(0, id.length() - 1); // id="0
                id = id.substring(4, id.length()); //0
    
                State state = new State();
                state.setID(id);
    
                line = readLine(br);
                // at this point, line can be 3 things: the closing tag </state>, the <initial/> tag or the <final/> tag
                
                while (!line.contains("</state>")){
                    if (line.contains("<x>") || line.contains("<y>")) {
                        line = readLine(br);
                        continue;
                    } else if (line.contains("<initial/>")) state.setInitial(true);
                    else if (line.contains("<final/>")) state.setFinal(true);

                    line = readLine(br);
                }
                states.add(state);

            } else if (line.contains("<transition")){
                Transition transition = new Transition();
                line = readLine(br);
                
                do {
                    if (line.contains("<from>")) transition.setFrom(line = line.replace("<from>", "").replace("</from>", ""));
                    else if (line.contains("<to>")) transition.setTo(line = line.replace("<to>", "").replace("</to>", ""));
                    else if (line.contains("<read>")) {
                        String read = line.replace("<read>", "").replace("</read>", "").replace(" ", "");
                        transition.setRead(read);
                        addReadToAlphabet(read);
                    }
                    line = readLine(br);
                } while (!line.contains("</transition>"));

                transitions.add(transition);
            }
            line = readLine(br);
        } while (!line.contains("</structure>"));

        NFA.setStates(states);
        NFA.setTransitions(transitions);

        symbolsOfReading = alphabet.toArray(new String[alphabet.size()]);
    }

    public static void readSentences(BufferedReader br) throws IOException {
        ArrayList<String> s = new ArrayList<>();
        do {
            s.add(line);
            line = br.readLine();
        } while (line != null);
        sentences.setSentences(s);
    }

    // set methods
    public static void setBasicStatesToDFA(){
        ArrayList<State> states = new ArrayList<>();

        for (String state : transitionTable.keySet()) {
            for (int i = 0; i < symbolsOfReading.length; i++){
                // this 'to' variable, keeps the state(s) where we go after we read the symbol
                String to = transitionTable.get(state)[i];
                String[] splitedTo = to.split(",");

                if (splitedTo.length == 1 && NFA.stateExists(state)){
                    State s = new State(state, NFA.getSpecificState(state).isInitial(), NFA.getSpecificState(state).isFinal(), false);
                    if (DFA.addState(s, states)) states.add(s);
                } else if (splitedTo.length > 1){
                    // so far, ours states were only 1 digit
                    State s = new State(to.replace(",", ""), false, false, true);
                    states.add(s);
                }
            }
        }
        DFA.setStates(states);
    }

    public static void setNewInitialsAndFinals(){
        for (State stateDFA : DFA.getStates()) {
            String idDFA = stateDFA.getID();
            
            for (State stateNFA : NFA.getStates()){
                String idNFA = stateNFA.getID();

                if (stateNFA.isInitial() && idDFA == idNFA){
                    stateDFA.setInitial(true);
                } if (stateNFA.isFinal() && idDFA.contains(idNFA)){
                    stateDFA.setFinal(true);
                }
            }
        }
    }
    
    public static void setNewStatesToDFA(){
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
                        if (!DFA.stateExists(string)) {
                            statesDFA.add(new State(string, false, false, true));
                            moreStatesToAdd = true;
                        }
                    }
                    break;
                    // we need a way to update the variable 'moreStatesToAdd' to false, otherwise it'll be in a loop
                } else {
                    moreStatesToAdd = false;
                    for (State state : DFA.getStates()) {
                        if (state.addToTable()) {
                            moreStatesToAdd = true;
                            break;
                        }
                    }
                }
            }
        } while (moreStatesToAdd);
    }

    public static void setNewTransitionsToDFA(){
        ArrayList<Transition> transitions = new ArrayList<>();

        for (State state : DFA.getStates()) {
            String from = state.getID();

            for (int i = 0; i <symbolsOfReading.length; i++){
                String to = transitionTable.get(from)[i].replace(",", "");
                String read = symbolsOfReading[i];

                Transition transition = new Transition(from, to, read);

                if (!to.equals("")) transitions.add(transition);
            }
        }
        DFA.setTransitions(transitions);
    }

    public static void setTransitionalTable(){
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

    // write methods 
    public static void writeDFA(BufferedWriter bw) throws IOException{
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        bw.write("<structure>");
        bw.newLine();
        bw.write("\t<type>fa</type>");
        bw.newLine();
        bw.write("\t<automaton>");
        bw.newLine();

        // List of states
        writeStates(bw);
        
        // List of transitions
        writeTransitions(bw);

        bw.write("\t</automaton>");
        bw.newLine();
        bw.write("</structure>");
        bw.newLine();

        bw.close();
    }

    public static void writeSentenceResult(BufferedWriter bw) throws IOException{
        for (int i = 0; i < sentences.getSentences().size(); i++) {
            bw.write("Sentence = " + sentences.getSentences().get(i) + " was ");
            if (sentences.getResults().get(i) == true) bw.write("accepted.");
            else bw.write("NOT accepted.");
            bw.newLine();
        }
    }

    public static void writeStates(BufferedWriter bw) throws IOException{
        int howManyStatesNFA = NFA.getStates().size();

        for (State state : DFA.getStates()) {
            String id;

            if (state.getID().length() > 1) {
                id = Integer.toString(howManyStatesNFA);
                bw.write("\t\t<state id=\"" + id + "\" name=\"q" + id +"\">");
                bw.newLine();

                statesEquivalents.put(state.getID(), id);
                howManyStatesNFA++;
            } else {
                id = state.getID();
                bw.write("\t\t<state id=\"" + id + "\" name=\"q" + id +"\">");
                bw.newLine();
            }

            if (state.isInitial()) {
                bw.write("\t\t\t<initial/>");
                bw.newLine();
            }
            if (state.isFinal()) {
                bw.write("\t\t\t<final/>");
                bw.newLine();
            }

            bw.write("\t\t</state>");
            bw.newLine();
        }
    }

    public static void writeTransitions(BufferedWriter bw) throws IOException{
        for (Transition transition : DFA.getTransitions()) {
            String from = transition.getFrom();
            String to = transition.getTo();
            String read = transition.getRead().replace(",", "");

            bw.write("\t\t<transition>");
            bw.newLine();

            if (from.length() > 1) from = statesEquivalents.get(from);
            if (to.length() > 1) to = statesEquivalents.get(to);

            bw.write("\t\t\t<from>" + from + "</from>");
            bw.newLine();
            bw.write("\t\t\t<to>" + to + "</to>");
            bw.newLine();
            bw.write("\t\t\t<read>" + read + "</read>");
            bw.newLine();

            bw.write("\t\t</transition>");
            bw.newLine();
        }
    }
    
    // functions
    public static ArrayList<String> getSplittedString(String string){
        ArrayList<String> result = new ArrayList<>();
        for (String str : string.split("")) result.add(str);
        return result;
    }

    public static String readLine(BufferedReader br) throws IOException {
        String aux = br.readLine().replaceAll("\t", "");;
        if (aux.contains("&#13;")) aux = aux.replace("&#13;", "");
        return aux;
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
