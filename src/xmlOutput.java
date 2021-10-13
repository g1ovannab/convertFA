package src;

public class xmlOutput {
    private final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
    private final String type = "<type>fa</type>";
    private final String automaton = "<automaton>";
    private final String state = "</state>";
    private final String transitionO = "<transition>";
    private final String transitionC = "</transition>";
    private final String initialS = "<initial/>";
    private final String finalS = "<final/>";

    public String XML() { return xml; }
    public String type() { return type; }
    public String automaton() { return automaton; }
    public String state() { return state; }
    public String openTransition() { return transitionO; }
    public String closeTransition() { return transitionC; }
    public String initialS() { return initialS; }
    public String finalS() { return finalS; }
}
