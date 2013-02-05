package st.icemi.cbe.util.pack.revisions

import st.icemi.cbe.util.pack.ClassPackage
import collection.mutable
import st.icemi.cbe.util.bytecode.matchers.{BytecodeMatch, BytecodePatternMatcher}
import st.icemi.cbe.util.bytecode.Searcher

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 5.2.2013
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
class RevisionPackage {
  private val revisions:mutable.MutableList[ClassPackage] = mutable.MutableList()

  def += (classPackage: ClassPackage) = revisions += classPackage

  def searchAll(matcher: BytecodePatternMatcher) = {
    val matches = collection.mutable.ListBuffer[BytecodeMatch]()
    revisions foreach (rev => matches ++= rev.search(matcher))
    matches.toList
  }

}
