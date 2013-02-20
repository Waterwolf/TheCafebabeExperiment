package st.icemi.cbe.util.bytecode.matchers

import org.objectweb.asm.tree.{ClassNode, MethodNode}

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
trait BytecodePatternMatcher {
  def search(cn:ClassNode, mn:MethodNode):List[BytecodeMatch]
}
