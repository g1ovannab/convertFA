package src;

import java.util.ArrayList;

public class Sentence {
    private ArrayList<String> sentences;
    private ArrayList<Boolean> results;

    public ArrayList<String> getSentences() { return sentences; }
    public void setSentences(ArrayList<String> s) { this.sentences = s; }

    public ArrayList<Boolean> getResults() { return results; }
    public void setResults(ArrayList<Boolean> r) { this.results = r; }

    public Sentence(ArrayList<String> s, ArrayList<Boolean> r){
        this.sentences = s;
        this.results = r;
    }

    public Sentence(){
        this.sentences = null;
        this.results = null;
    }
}
