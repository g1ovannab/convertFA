package src;

import java.io.*;
import java.util.*;


public class App {

    // generic variables
    public static String line = "";
    public static ArrayList<String> sentences = new ArrayList<>();

    // NFA variables
    public static ArrayList<State> statesNFA = new ArrayList<>();
    public static ArrayList<Transition> transitionsNFA = new ArrayList<>();

    // FA variables
    public static ArrayList<State> statesDFA = new ArrayList<>();
    public static ArrayList<Transition> transitionsDFA = new ArrayList<>();

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
        do {
            if (line.contains("<?xml") || line.contains("<type>") || line.contains("<automaton>") || line.contains("<!--") || line.contains("</automaton>")) {
                line = ReadLine(br);
                continue;
            } else if (line.contains("<state")){
                // <state id="0" name="q0">&#13;
                String[] stateSplited = line.split(" ");
                // [0] <state       [1] id="0"      [2] name="q0">&#13;
    
                String id = stateSplited[1];
                id = id.substring(0, id.length() - 1); // id="0
                id = id.substring(4, id.length()); //0
    
                State state = new State();
                state.setID(Integer.parseInt(id));
    
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
                statesNFA.add(state);

            } else if (line.contains("<transition")){
                
                Transition transition = new Transition();
                line = ReadLine(br);
                
                do {
                    if (line.contains("<from>")) transition.setFrom(Integer.parseInt(line = line.replace("<from>", "").replace("</from>", "")));
                    else if (line.contains("<to>")) transition.setTo(Integer.parseInt(line = line.replace("<to>", "").replace("</to>", "")));
                    else if (line.contains("<read>")) transition.setRead(line = line.replace("<read>", "").replace("</read>", ""));
                    
                    line = ReadLine(br);
                } while (!line.contains("</transition>"));
                
                transitionsNFA.add(transition);
            }
            line = ReadLine(br);
        } while (!line.contains("</structure>"));
    }

    public static String ReadLine(BufferedReader br) throws IOException {
        String aux = br.readLine().replaceAll("\t", "");;
        if (aux.contains("&#13;")) aux = aux.replace("&#13;", "");
        return aux;
    }

    public static void ExportFiles() {

    }

    public static void ConvertFA() {

    }

}
