import java.util.*;

public class DFA {
    private ArrayList<ArrayList<Integer>> transitionTable = new ArrayList<>();
    private Set<Integer> acceptStates = new TreeSet<>();
    private Map<String, Integer> alphabetMap = new HashMap<>();
    private Map<Integer, Integer> stateMap = new HashMap<>();
    private Integer startState;
    private String[] alphabet;

    public DFA() { }

    /*
    GETTERS : trivial
     */

    public Set<Integer> getAcceptStates() {
        return acceptStates;
    }

    public Integer getStartState() {
        return startState;
    }

    public String[] getAlphabet() {
        return alphabet;
    }

   /*
    SETTERS : trivial
     */

   public void addToAcceptStates(Integer state) {
       acceptStates.add(state);
   }

    public void setStartState(Integer startState) {
        this.startState = startState;
    }

    public void setAlphabet(String[] alphabet) {
        this.alphabet = alphabet;
    }
    /*
    transitionTable functions
     */

    public void setTransition(int fromState, int toState, String letter) {
        // if we haven't seen the state yet then we need to init it
        if (toState == -1) return;
        if (!stateExists(fromState)) {
            initTransitionRow(fromState);
        }
//        if (!stateExists(toState)) {
//            initTransitionRow(toState);
//        }
        transitionTable.get(getStateMapping(fromState)).set(alphabetMap.get(letter), toState);
    }

    public void initTransitionRow(int state) {
        // if state isn't in state map add it to the map
        int mappedState = getStateMapping(state);
        ArrayList<Integer> row = new ArrayList<>();
        for (int i = 0; i < alphabet.length; i++) {
            row.add(-1);
        }
        transitionTable.add(row);
    }

    public Integer getTransition(int fromState, String letter) {
        return getRow(fromState).get(alphabetMap.get(letter));
    }

    private ArrayList<Integer> getRow(int fromState) {
        return transitionTable.get(getStateMapping(fromState));
    }

    /*
    stateMap functions
     */

    private void setStateMapping(int state) {
        if (!stateMap.containsKey(state)) {
            stateMap.put(state, stateMap.size());
        }
    }

    private int getStateMapping(int state) {
        setStateMapping(state);
        return stateMap.get(state);
    }

    public boolean stateExists(int state) {
        return stateMap.containsKey(state);
    }

    /*
    alphabetMap functions
     */

    public void makeAlphabetMap() {
        for (int i = 0; i < alphabet.length; i++) {
            alphabetMap.put(alphabet[i], i);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < transitionTable.size(); i++) {
            str.append(acceptStates.contains(i) ? "+ " : "- ")
                    .append(i)
                    .append(" ");
            for (Integer transition : transitionTable.get(i)) {
                str.append(transition.toString()).append(" ");
            }
            str.append("\n");
        }

        return str.toString();
    }
}
