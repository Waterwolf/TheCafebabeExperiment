package st.icemi.cbe.util.pack.revisions

import st.icemi.cbe.util.pack.ClassPackage
import st.icemi.cbe.util.bytecode.matchers.{BytecodePatternMatcher, BytecodeMatch}

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 8.2.2013
 * Time: 21:22
 */
class Revisions(packs: List[ClassPackage]) {
  def search(matcher: BytecodePatternMatcher) = packs.foreach(p => p.search(matcher).size > 0)
}
object Revisions {
  def apply(packs: ClassPackage*) = new Revisions(packs.toList)
}