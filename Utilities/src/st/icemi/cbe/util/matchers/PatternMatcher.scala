package st.icemi.cbe.util.matchers

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
trait PatternMatcher {
  def matches(seq:CharSequence):Boolean
}
