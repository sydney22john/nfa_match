public class Main {
    public static void main(String[] args) {
        CommandLineArgs cmdArgs = new CommandLineArgs();
        cmdArgs.parseArgs(args);

        NFA nfa = Functions.readNFAFromFile(cmdArgs.getInputFile());

        DFA dfa = Functions.NFAToDFA(nfa);
        System.out.println("--- Original ---");
        System.out.println(dfa);

        Functions.optimize(dfa);

        System.out.println("--- Final ---");
        System.out.println(dfa);

        Functions.matchTokens(dfa, cmdArgs.getTokens());

        Functions.writeDFAToFile(dfa, cmdArgs.getOutputFile());
    }
}