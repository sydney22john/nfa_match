import java.util.*;
import java.util.stream.Collectors;

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
                str.append(transition == -1 ? "E" : transition).append(" ");
            }
            str.append("\n");
        }

        return str.toString();
    }

    public Stack<String> getAlphabetStack() {
        Stack<String> alpha = new Stack<>();
        for (int i = alphabet.length - 1; i >= 0; i--) {
            alpha.push(alphabet[i]);
        }
        return alpha;
    }

    public Set<Integer> nonAcceptingStates() {
        Set<Integer> allStates = new TreeSet<>();
        for (int i = 0; i < transitionTable.size(); i++) {
            allStates.add(i);
        }
        allStates.removeAll(acceptStates);
        return allStates;
    }

    public boolean startStateInSet(Set<Integer> subset) {
        return subset.contains(startState);
    }

    public void remapStateMap(int stateToKeep, Set<Integer> subset) {
        for (Integer state : subset) {
            stateMap.replace(state, stateToKeep);
        }
    }

    public void reassignStateValues(Set<Integer> removedIndices) {
        Map<Integer, Integer> toDecrement = new HashMap<>();
        for (Map.Entry<Integer, Integer> e : stateMap.entrySet()) {
            int finalI = e.getKey();
            int decrementValue = (int) removedIndices.stream().filter(val -> val < finalI).count();
            if (decrementValue != 0 && !removedIndices.contains(finalI)) {
                toDecrement.put(e.getKey(), e.getValue() - decrementValue);
            }
        }
        for (Map.Entry<Integer, Integer> e : toDecrement.entrySet()) {
            if (acceptStates.contains(e.getKey())) {
                acceptStates.remove(e.getKey());
                acceptStates.add(e.getValue());
            }
            stateMap.replace(e.getKey(), e.getValue());
        }
        for (ArrayList<Integer> row : transitionTable) {
            row.replaceAll(key -> key != -1 ? stateMap.get(key) : -1);
        }
    }

    public void deleteDuplicates(Set<Integer> removedIndices) {
        Integer[] toRemoveArray = new Integer[removedIndices.size()];
        toRemoveArray = removedIndices.toArray(toRemoveArray);
        // removing the duplicate rows
        for (int i = toRemoveArray.length - 1; i >= 0; i--) {
            transitionTable.remove((int) toRemoveArray[i]);
        }
        stateMap.clear();
        for (int i = 0; i < transitionTable.size(); i++) {
            stateMap.put(i, i);
        }
    }

    public Set<Integer> reachableStatesFrom(int currentState) {
        Set<Integer> reachableFrom = new TreeSet<>();
        for (int i = 0; i < transitionTable.size(); i++) {
            ArrayList<Integer> row = transitionTable.get(i);
            if (row.contains(currentState)) {
                reachableFrom.add(i);
            }
        }
        return reachableFrom;
    }

    public Set<Integer> reachableStatesComplement(Set<Integer> reachableStates) {
        Set<Integer> difference = new TreeSet<>();
        for (int i = 0; i < transitionTable.size(); i++) { difference.add(i); }
        difference.removeAll(reachableStates);
        return difference;
    }

    public Set<Integer> getAllStateTransitions(int currentState) {
        return transitionTable.get(stateMap.get(currentState)).stream()
                .filter(val -> val >= 0)
                .collect(Collectors.toSet());
    }
}
