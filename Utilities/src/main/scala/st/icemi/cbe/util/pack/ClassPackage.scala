package st.icemi.cbe.util.pack

import collection.mutable
import org.objectweb.asm.tree.ClassNode
import st.icemi.cbe.util.bytecode.matchers.{BytecodeMatch, BytecodePatternMatcher}
import st.icemi.cbe.util.bytecode.Searcher

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
class ClassPackage(name:String = "default") {
  private val classes:mutable.Map[String, ClassNode] = mutable.Map()

  def +=(node:ClassNode) = classes +=(node.name -> node)
  def foreach(mfunc:(ClassNode => Unit)) = classes foreach (p => mfunc(p._2))

  def search(matcher: BytecodePatternMatcher) = {
    val matches = collection.mutable.ListBuffer[BytecodeMatch]()
    foreach (node => matches ++= Searcher.findMatches(matcher, node))
    matches
  }

}