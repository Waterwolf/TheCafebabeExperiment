package st.icemi.cbe.util.matchers

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
object Regex {
  def apply(pattern:String):RegexMatcher = new RegexMatcher(pattern)
}
class RegexMatcher(pattern:String) extends PatternMatcher {
  def matches(seq: CharSequence): Boolean = (pattern.r findFirstIn seq) != None
}