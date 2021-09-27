package src;

import java.io.*;
import java.util.*;


public class App {
    public static void main(String[] args) throws Exception {
        ImportFiles();

        ConvertFA();

        ExportFiles();
    }

    public static void ImportFiles() throws IOException {
        /* NFA Region */
        File nfa = new File("rcv-folder/nfa.jff");
        FileReader frNFA = new FileReader(nfa);
        BufferedReader brNFA = new BufferedReader(frNFA);

        String line = brNFA.readLine();

        ArrayList<State> states = new ArrayList<>();
        ArrayList<Transition> transitions = new ArrayList<>();

        do {
            ReadNFA(line, brNFA, states, transitions);
        } while (line != null);
        brNFA.close();


        /* Sentences Region */
        File sentencesFile = new File("rcv-folder/sentences.txt");
        FileReader frS = new FileReader(sentencesFile);
        BufferedReader brS = new BufferedReader(frS);

        line = brS.readLine();

        ArrayList<String> sentences = new ArrayList<>();

        // the sentences will need to be separated by a \n (enter)
        do {
            sentences.add(line);
        } while (line != null);
        brS.close();
        
    }

    public static ArrayList<String> readRead(String read) {
        ArrayList<String> reads = new ArrayList<>();
        String[] readsSplited = read.replace("<read>", "").replace("</read>&#13;", "").split(",");
        for (String split : readsSplited) reads.add(split);

        return reads;
    }

    public static void ReadNFA(String line, BufferedReader br, ArrayList<State> states, ArrayList<Transition> transitions) throws IOException{
        if (line.startsWith("<state")){
            // <state id="0" name="q0">&#13;
            String[] stateSplited = line.split(" ");
            // [0] <state       [1] id="0"      [2] name="q0">&#13;

            String id = stateSplited[1];
            id = id.substring(0, id.length() - 1); // id="0
            id = id.substring(0, 4); //0


            State state = new State();
            state.setID(Integer.parseInt(id));

            line = br.readLine();
            // this can be 3 things: the closing tag </state>, the <initial/> tag or teh <final/> tag
            
            // if it isn't the closing tag, we need to discover what is the next tag:
            while (!line.startsWith("</state>")){
                if (line.startsWith("<initial/>")) state.setInitial(true);
                else if (line.startsWith("<final/>")) state.setFinal(true);
                
                line = br.readLine();
            }

            states.add(state);
        } else if (line.startsWith("<transition")){
            Transition transition = new Transition();

            do {
                if (line.startsWith("<from>")) transition.setFrom(Integer.parseInt(line.replace("<from>", "").replace("</from>&#13;", "")));
                else if (line.startsWith("<to>")) transition.setTo(Integer.parseInt(line.replace("<to>", "").replace("</to>&#13;", "")));
                else if (line.startsWith("<read>")) transition.setRead(readRead(line));

                line = br.readLine();
            } while (!line.startsWith("</transition>"));
            
            transitions.add(transition);
        }

        line = br.readLine();
    }

    public static void ExportFiles() {

    }

    public static void ConvertFA() {

    }

}
