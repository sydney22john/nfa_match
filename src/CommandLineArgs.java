import java.util.ArrayList;
import java.util.Arrays;

public class CommandLineArgs {
    public String getOutputFile() {
        return outputFile;
    }

    public String[] getTokens() {
        return tokens;
    }

    public String getInputFile() {
        return inputFile;
    }

    private String inputFile;
    private String outputFile;

    private String[] tokens;

    public CommandLineArgs() {
    }

    public void parseArgs(String[] args) {
        if (validCMDArgs(args)) {
            inputFile = args[0];
            outputFile = args[1];

            tokens = new String[args.length - 2];

            System.arraycopy(args, 2, tokens, 0, args.length - 2);
        }
    }

    public void print() {
        System.out.println(inputFile);
        System.out.println(outputFile);
        System.out.println(Arrays.toString(tokens));
    }

    private boolean validCMDArgs(String[] args) {
        if (args.length < 3) {
            System.out.println("Invalid Command Line Arguments");
//            for (String arg :
//                    args) {
//                System.out.println(arg);
//            }
            System.out.println("Usage:");
            System.out.println("<inputfilename> <outputfilename> tokens: '*' '*' ... '*'");
            System.exit(1);
        }
        return true;
    }

}
