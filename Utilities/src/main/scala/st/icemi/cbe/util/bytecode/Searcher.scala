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
  def findMatches(matcher: BytecodePatternMatcher, node:ClassNode):List[BytecodeMatch] = {
    val matches = collection.mutable.ListBuffer[BytecodeMatch]()
    node.methods foreach (mnode => matches ++= matcher.search(node, mnode.asInstanceOf[MethodNode]))
    return matches.toList
  }
  def findAllMatches(matcher: BytecodePatternMatcher, nodes:ClassNode*):List[BytecodeMatch] = {
    val matches = collection.mutable.ListBuffer[BytecodeMatch]()
    nodes foreach (node => matches ++= findMatches(matcher, node))
    return matches.toList
  }
}
