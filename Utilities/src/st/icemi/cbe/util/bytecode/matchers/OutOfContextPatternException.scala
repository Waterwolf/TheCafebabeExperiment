package st.icemi.cbe.util.bytecode.matchers

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 18:32
 *
 * Thrown if pattern has context specific syntax but is used in non-context specific method.
 *
 */
class OutOfContextPatternException(message:String) extends Exception(message)
