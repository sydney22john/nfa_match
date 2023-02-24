import java.util.*;

public class NFA {

    private ArrayList<ArrayList<Set<Integer>>> transitionTable = new ArrayList<>();
    private Set<Integer> acceptStates = new TreeSet<>();
    private Map<String, Integer> alphabetMap = new HashMap<>();
    private Map<Integer, Integer> stateMap = new HashMap<>();
    private Integer startState;
    private String lambda;
    private String[] alphabet;

    public NFA() { }


    /*
    alphabet
     */
    public String[] getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(String[] alphabet) {
        this.alphabet = alphabet;
    }


    public Integer getStartState() {
        return startState;
    }

    public void setStartState(Integer startState) {
        this.startState = startState;
    }

    public String getLambda() {
        return lambda;
    }

    public void setLambda(String lambda) {
        this.lambda = lambda;
    }


    /*
    alphabetMap functions
     */

    public void makeAlphabetMap() {
        for (int i = 0; i < alphabet.length; i++) {
            alphabetMap.put(alphabet[i], i);
        }
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

    private boolean stateExists(int state) {
        return stateMap.containsKey(state);
    }


    /*
    transitionTable functions
     */

    public void setTransition(int fromState, int toState, String letter) {
        // if we haven't seen the state yet then we need to init it
        if (!stateExists(fromState)) {
            initTransitionRow(fromState);
        }
        if (!stateExists(toState)) {
            initTransitionRow(toState);
        }
        transitionTable.get(getStateMapping(fromState)).get(alphabetMap.get(letter)).add(toState);
    }

    public void initTransitionRow(int state) {
        // if state isn't in state map add it to the map
        int mappedState = getStateMapping(state);
        ArrayList<Set<Integer>> row = new ArrayList<>();
        for (int i = 0; i < alphabet.length; i++) {
            row.add(new TreeSet<>());
        }
        transitionTable.add(row);
    }


    /*
    acceptStates functions
     */

    public void setAccept(int fromState, boolean isAccept) {
        if (isAccept) {
            acceptStates.add(getStateMapping(fromState));
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < transitionTable.size(); i++) {
            str.append(acceptStates.contains(i) ? "+ " : "- ")
                    .append(i)
                    .append(" ");
            for (Set<Integer> transition :
                    transitionTable.get(i)) {
                str.append(transition.toString()).append(" ");
            }
            str.append("\n");
        }

        return str.toString();
    }
}
