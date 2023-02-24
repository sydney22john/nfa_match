import java.util.*;
import java.util.stream.Collectors;

public class NFA {

    private ArrayList<ArrayList<Set<Integer>>> transitionTable = new ArrayList<>();
    // contains the mapped version of the integer
    private Set<Integer> acceptStates = new TreeSet<>();
    private Map<String, Integer> alphabetMap = new HashMap<>();
    private Map<Integer, Integer> stateMap = new HashMap<>();
    private Integer startState;
    private String lambda;
    private String[] alphabet;
    // all the unmapped states
    private Set<Integer> allStates = new TreeSet<>();

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
            allStates.add(state);
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
        if (stateExists(state)) return;
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
            int originalState = 0;
            for (Map.Entry<Integer, Integer> entry : stateMap.entrySet()) {
                if (entry.getValue() == i) {
                    originalState = entry.getKey();
                }
            }
            originalState = i;
            str.append(acceptStates.contains(i) ? "+ " : "- ")
                    .append(originalState)
                    .append(" ");
            for (Set<Integer> transition : transitionTable.get(i)) {
                str.append(transition.toString()).append(" ");
            }
            str.append("\n");
        }

        return str.toString();
    }

    public void makeStateMap(Scanner fileRead) {

    }

    public void renumber() {
        remakeMap();
        for (int i = 0; i < transitionTable.size(); i++) {
            ArrayList<Set<Integer>> row = transitionTable.get(i);
            for (int j = 0; j < row.size(); j++) {
                Set<Integer> newSet = row.get(j).stream()
                        .map(val -> stateMap.get(val))
                        .collect(Collectors.toSet());
                row.set(j, newSet);
            }
        }
        Map<Integer, Integer> newMap = new HashMap<>();
        for (int i = 0; i < stateMap.size(); i++) {
            newMap.put(i, i);
        }
        stateMap = newMap;
    }

    private void reorder(Map<Integer, Integer> reference) {
        ArrayList<ArrayList<Set<Integer>>> newTable = new ArrayList<>();
        for (int i = 0; i < transitionTable.size(); i++) {
            newTable.add(new ArrayList<>());
        }
        for (Integer state : allStates) {
            newTable.set(reference.get(state), transitionTable.get(stateMap.get(state)));
        }
//        for (Map.Entry<Integer, Integer> e : stateMap.entrySet()) {
//            newTable.set(e.getKey(), transitionTable.get(e.getValue()));
//        }
        transitionTable = newTable;
    }

    private void remakeMap() {
        HashMap<Integer, Integer> newMap =  new HashMap<>();
        for (Integer state : allStates) {
            // 0 -> 0, 11 -> 1, etc
            newMap.put(state, newMap.size());
            if (acceptStates.contains(stateMap.get(state))) {
                acceptStates.remove(stateMap.get(state));
                acceptStates.add(newMap.get(state));
            }
        }
        reorder(newMap);
        stateMap = newMap;
    }

}
