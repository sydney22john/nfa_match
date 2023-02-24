import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
        return nfa;
    }

    public static void setTransitionsFromFile(NFA nfa, Scanner fileReader) {
        while (fileReader.hasNextLine()) {
            String[] lineTokens = fileReader.nextLine().split("\\s+");
            int fromState = Integer.parseInt(lineTokens[1]);
            int toState = Integer.parseInt(lineTokens[2]);

            // setting the start state

            for (int i = 3; i < lineTokens.length; i++) {
                nfa.setTransition(fromState, toState, lineTokens[i]);
            }
            nfa.setAccept(fromState, lineTokens[0].equals("+"));
        }
        nfa.setStartState(0);
    }

    public static DFA NFAToDFA(NFA nfa) {

    }

}
