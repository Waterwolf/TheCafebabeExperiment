package st.icemi.cbe.util.bytecode

import matchers.{BytecodePatternMatcher, BytecodeMatch}
import org.objectweb.asm.tree.{MethodNode, ClassNode}
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
object Searcher {
  def findMatches(matcher: BytecodePatternMatcher, nodes:Array[ClassNode]):Array[BytecodeMatch] = {
    val matches = collection.mutable.ListBuffer[BytecodeMatch]()

    for (node <- nodes) {
      for (mnode <- node.methods) {
        matches ++= matcher.search(node, mnode.asInstanceOf[MethodNode])
      }
    }

    return matches.toArray
  }
}
