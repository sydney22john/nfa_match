public class Main {
    public static void main(String[] args) {
        CommandLineArgs cmdArgs = new CommandLineArgs();
        cmdArgs.parseArgs(args);

        NFA nfa = Functions.readNFAFromFile(cmdArgs.getInputFile());
        System.out.println(nfa);

        DFA dfa = Functions.NFAToDFA(nfa);
        System.out.println(dfa);
    }
}