import java.util.*;

public class DFA {
    private ArrayList<ArrayList<Integer>> transitionTable = new ArrayList<>();
    private Set<Integer> acceptStates = new TreeSet<>();
    private Map<String, Integer> alphabetMap = new HashMap<>();
    private Map<Integer, Integer> stateMap = new HashMap<>();
    private Integer startState;
    private String[] alphabet;

    public DFA() { }
}
