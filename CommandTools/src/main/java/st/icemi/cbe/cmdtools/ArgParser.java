package st.icemi.cbe.cmdtools;

import st.icemi.cbe.util.pack.ClassPackage;

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 30.1.2013
 * Time: 18:58
 * To change this template use File | Settings | File Templates.
 */
public class ArgParser {
    private String[] args;
    private int index = 0;

    public ArgParser(String[] args) {
        this.args = args;
    }

    public String forward() {
        if (index >= args.length) { // Note! Index will eventually equal to args.length due to ++ being a postfix.
            return null;
        }
        return args[index++];
    }

    public String current() {
        if (index >= args.length) {
            return null;
        }
        return args[index];
    }

    public String next() {
        if (index+1 >= args.length) {
            return null;
        }
        return args[index+1];
    }

}
