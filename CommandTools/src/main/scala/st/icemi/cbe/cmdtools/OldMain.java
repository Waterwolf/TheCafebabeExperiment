package st.icemi.cbe.cmdtools;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import st.icemi.cbe.util.bytecode.Searcher;
import st.icemi.cbe.util.bytecode.matchers.BytecodeMatch;
import st.icemi.cbe.util.bytecode.matchers.cafeglob.CafeGlob;
import st.icemi.cbe.util.files.ClassHelper;
import st.icemi.cbe.util.files.FileFinder;
import st.icemi.cbe.util.matchers.Glob;
import st.icemi.cbe.util.matchers.PatternMatcher;
import st.icemi.cbe.util.matchers.RegexMatcher;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 30.1.2013
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
public class OldMain {
    public static void main(String[] args) {
        parseArgs(args);
    }

    public static void error(String s) {
        System.err.println(s);
    }

    public static void print(String s) {
        System.out.println(s);
    }

    public static boolean parseArgs(String[] args) {
/*
        ArgParser parser = new ArgParser(args);

        String pri = parser.forward();
        if ("help".equals(pri)) {
            print("Possible commands:");
            print("");
            print("help - show this text");
            print("findpattern [flags] [pattern] - searches for a bytecode pattern in given files");
            print("  Possible flags:");
            print("  -t [pattern]   Target filename. Uses glob rules");
            print("  -r   Enable recursive search");
            return true;
        }
        else if ("findpattern".equals(pri)) {
            String glob = "*.class";
            boolean recursive = false;
            boolean useregex = false;
            boolean debug = false;

            String flag;
            while ((flag = parser.current()) != null && flag.startsWith("-")) {
                parser.forward(); // we looked into future with parser.next() so we'll just emit it for now
                if (flag.equals("-t")) {
                    glob = parser.forward();
                }
                else if (flag.equals("-r")) {
                    recursive = true;
                }
                else if (flag.equals("-reg")) {
                    useregex = true;
                }
                else if (flag.equals("-debug")) {
                    debug = true;
                }
            }

            String bcpattern = parser.forward();

            if (debug)
                System.out.println("Findpattern: " + glob + " " + recursive + "  " + bcpattern + " " + useregex);

            PatternMatcher matcher = useregex ? new RegexMatcher(bcpattern) : Glob.apply(bcpattern);

            File[] files = FileFinder.find(new File("."), glob, recursive);
            if (debug) {
                System.out.println("Globbed files:");
                for (File f : files) {
                    System.out.println(" " + f.getName());
                }
                System.out.println("End globbed files");
            }

            ClassNode[] classes = ClassHelper.loadBunch(files, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES); // We don't care about others than pure insns right now
            if (debug) {
                System.out.println("Loaded classes:");
                for (ClassNode node : classes) {
                    System.out.println(" " + node.name);
                }
                System.out.println("End loaded classes");
            }

            BytecodeMatch[] matches = Searcher.findMatches(CafeGlob.apply(bcpattern), classes);
            //System.out.println("Matches #" + matches.length);
            for (BytecodeMatch match : matches) {
                System.out.println(match);
            }


            return true;
        }

        error("No arguments were given. Type \"help\" to get list of commands.");
        return false;
        */
        return false;
    }


}
