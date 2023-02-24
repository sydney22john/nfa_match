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
    GETTERS : trivial
     */
    public Set<Integer> getAcceptStates() {
        return acceptStates;
    }

    public String[] getAlphabet() {
        return alphabet;
    }

    public String getLambda() {
        return lambda;
    }
    public Integer getStartState() {
        return startState;
    }

    public Set<Integer> getStartStateAsSet() {
        Set<Integer> s = new TreeSet<>();
        s.add(startState);
        return s;
    }

    /*
    SETTERS : trivial
     */

    public void setAlphabet(String[] alphabet) {
        this.alphabet = alphabet;
    }

    public void setStartState(Integer startState) {
        this.startState = startState;
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

    public Set<Integer> getTransition(int fromState, String letter) {
        return getRow(fromState).get(alphabetMap.get(letter));
    }

    public Set<Integer> getLambdaTransition(int fromState) {
        return getTransition(fromState, lambda);
    }

    private ArrayList<Set<Integer>> getRow(int fromState) {
        return transitionTable.get(getStateMapping(fromState));
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
