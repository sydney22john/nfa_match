import javax.lang.model.type.IntersectionType;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Functions {

    public static NFA readNFAFromFile(String fileName) {
        NFA nfa = new NFA();
        try {
            File file = new File(fileName);
            if (file.length() == 0) {
                System.out.println("File is empty or doesn't exist");
                System.exit(1);
            }
            Scanner fileReader = new Scanner(file);
            String[] firstLineTokens = fileReader.nextLine().split("\\s+");

            // setting attributes alphabet attributes
            String[] alphabet = new String[firstLineTokens.length - 1];
            int numOfStates = Integer.parseInt(firstLineTokens[0]);
            String lambda = firstLineTokens[1];
            System.arraycopy(firstLineTokens, 2, alphabet, 0, firstLineTokens.length - 2);
            alphabet[alphabet.length - 1] = lambda;

            nfa.setLambda(lambda);
            nfa.setAlphabet(alphabet);
            nfa.makeAlphabetMap();

            setTransitionsFromFile(nfa, fileReader);

            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        nfa.renumber();
        return nfa;
    }

    public static void setTransitionsFromFile(NFA nfa, Scanner fileReader) {
        while (fileReader.hasNextLine()) {
            String[] lineTokens = fileReader.nextLine().split("\\s+");
            if (lineTokens.length < 3) continue;
            int fromState = Integer.parseInt(lineTokens[1]);
            int toState = Integer.parseInt(lineTokens[2]);

            // setting the start state

            if (lineTokens.length == 3) {
                nfa.initTransitionRow(fromState);
            }
            for (int i = 3; i < lineTokens.length; i++) {
                nfa.setTransition(fromState, toState, lineTokens[i]);
            }
            nfa.setAccept(fromState, lineTokens[0].equals("+"));
        }
        nfa.setStartState(0);
    }

    public static DFA NFAToDFA(NFA nfa) {
        HashSet<Set<Integer>> seenSets = new HashSet<>();
        DFA dfa = new DFA();
        dfa.setAlphabet(Arrays.copyOfRange(nfa.getAlphabet(), 0, nfa.getAlphabet().length - 1));
        dfa.makeAlphabetMap();

        SetToIntMapping setMapping = new SetToIntMapping();

        Stack<Set<Integer>> L = new Stack<>();
        Set<Integer> A = nfa.getAcceptStates();
        Set<Integer> i = nfa.getStartStateAsSet();
        Set<Integer> B = followLambda(nfa, i);

        dfa.initTransitionRow(setMapping.getSetMapping(B));
        dfa.setStartState(setMapping.getSetMapping(B));

        if (doSetsIntersect(A, B)) {
            dfa.addToAcceptStates(setMapping.getSetMapping(B));
        }

        L.push(B);

        while (!L.empty()) {
            Set<Integer> S = L.pop();
            seenSets.add(S);

            for (String c : dfa.getAlphabet()) {

                Set<Integer> R = followLambda(nfa, followChar(nfa, S, c));
                dfa.setTransition(setMapping.getSetMapping(S), setMapping.getSetMapping(R), c);
                if (!R.isEmpty() && !dfa.stateExists(setMapping.getSetMapping(R))) {
                    dfa.initTransitionRow(setMapping.getSetMapping(R));
                    if (doSetsIntersect(A, R)) {
                        dfa.addToAcceptStates(setMapping.getSetMapping(R));
                    }
                    L.push(R);
                }
            }
        }

        return dfa;
    }

    public static Set<Integer> followLambda(NFA nfa, Set<Integer> nfaSubset) {
        Stack<Integer> M = new Stack<>();
        for (Integer state : nfaSubset) {
            M.push(state);
        }

        while (!M.empty()) {
            int t = M.pop();
            Set<Integer> lambdaTransitions = nfa.getLambdaTransition(t);
            for (Integer toState : lambdaTransitions) {
                if (!nfaSubset.contains(toState)) {
                    nfaSubset.add(toState);
                    M.push(toState);
                }
            }
        }
        return nfaSubset;
    }

    public static Set<Integer> followChar(NFA nfa, Set<Integer> nfaSubset, String letter) {
        Set<Integer> F = new TreeSet<>();
        for (Integer state : nfaSubset) {
            F.addAll(nfa.getTransition(state, letter));
        }

        return F;
    }

    public static boolean doSetsIntersect(Set<Integer> A, Set<Integer> B) {
        Set<Integer> temp = new TreeSet<>(A);
        temp.retainAll(B);
        return temp.size() > 0;
    }

    public static void optimize(DFA dfa) {
        mergeStatesWrapper(dfa);
        System.out.println("--- After Merge ---");
        System.out.println(dfa);

        removeUnreachableStates(dfa);
        System.out.println("--- After Unreachable ---");
        System.out.println(dfa);

        removeDeadStates(dfa);
        System.out.println("--- After Dead ---");
        System.out.println(dfa);
    }

    private static void removeDeadStates(DFA dfa) {
        Set<Integer> reachableStates = new TreeSet<>();
        Queue<Integer> toSearch = new LinkedList<>();
        toSearch.addAll(dfa.getAcceptStates());
        Set<Integer> statesInQueue = new TreeSet<>(toSearch);

        while (!toSearch.isEmpty()) {
            int currentState = toSearch.poll();
            reachableStates.add(currentState);
            Set<Integer> reachableFrom = dfa.reachableStatesFrom(currentState);

            for (Integer state : reachableFrom) {
                if (!reachableStates.contains(state) && !statesInQueue.contains(state)) {
                    statesInQueue.add(state);
                    toSearch.add(state);
                }
            }
        }
        Set<Integer> complement = dfa.reachableStatesComplement(reachableStates);
        dfa.remapStateMap(-1, complement);
        dfa.reassignStateValues(complement);
        dfa.deleteDuplicates(complement);
    }

    /*
    Idea: breadth first search on the graph, any states that I don't see are pruned
     */
    private static void removeUnreachableStates(DFA dfa) {
        Set<Integer> reachableStates = new TreeSet<>();
        Queue<Integer> toSearch = new LinkedList<>();
        toSearch.add(dfa.getStartState());
        Set<Integer> statesInQueue = new TreeSet<>(toSearch);

        while (!toSearch.isEmpty()) {
            int currentState = toSearch.poll();
            reachableStates.add(currentState);
            Set<Integer> toStates = dfa.getAllStateTransitions(currentState);

            for (Integer state : toStates) {
                if (!reachableStates.contains(state) && !statesInQueue.contains(state)) {
                    statesInQueue.add(state);
                    toSearch.add(state);
                }
            }
        }
        Set<Integer> complement = dfa.reachableStatesComplement(reachableStates);
        dfa.remapStateMap(-1, complement);
        dfa.reassignStateValues(complement);
        dfa.deleteDuplicates(complement);
    }

    public static void mergeStatesWrapper(DFA dfa) {
        while (true) {
            if (!mergeStates(dfa)) break;
        }
    }

    public static boolean mergeStates(DFA dfa) {
        Set<Set<Integer>> M = new HashSet<>();
        Stack<Set<Integer>> L_set = new Stack<>();
        Stack<Stack<String>> L_alpha = new Stack<>();

        L_set.push(dfa.getAcceptStates());
        L_alpha.push(dfa.getAlphabetStack());

        L_set.push(dfa.nonAcceptingStates());
        L_alpha.push(dfa.getAlphabetStack());

        while (!L_set.empty()) {
            Set<Integer> S = L_set.pop();
            Stack<String> C = L_alpha.pop();

            ArrayList<Set<Integer>> partitionedStates = partitionStates(S, C.pop(), dfa);
            for (int i = 0; i < partitionedStates.size(); i++) {
                Set<Integer> X_i = partitionedStates.get(i);
                if (X_i.size() > 1) {
                    if (C.empty()) {
                        M.add(X_i);
                    } else {
                        L_set.push(X_i);
                        L_alpha.push((Stack<String>) C.clone());
                    }
                }
            }
        }
        if (M.isEmpty()) return false;
        Set<Integer> removedIndices = new TreeSet<>();
        for (Set<Integer> subset : M) {
            int stateToKeep = (Integer) subset.toArray()[0];
            subset.remove(stateToKeep);
            removedIndices.addAll(subset);
            dfa.remapStateMap(stateToKeep, subset);

            if (dfa.startStateInSet(subset)) {
                dfa.setStartState(stateToKeep);
            }
        }
        dfa.reassignStateValues(removedIndices);
        dfa.deleteDuplicates(removedIndices);
        return true;
    }

    private static ArrayList<Set<Integer>> partitionStates(Set<Integer> S, String c, DFA dfa) {
        Map<Integer, Set<Integer>> partitionedStates = new TreeMap<>();
        for (int state : S) {
            int toState = dfa.getTransition(state, c);
            if (!partitionedStates.containsKey(toState)) {
                partitionedStates.put(toState, new TreeSet<>());
            }
            partitionedStates.get(toState).add(state);
        }
        ArrayList<Set<Integer>> ans = new ArrayList<>();
        for (Set<Integer> value : partitionedStates.values()) {
            ans.add(value);
        }
        return ans;
    }
}