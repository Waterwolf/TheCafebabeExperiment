package st.icemi.cbe.util.bytecode.matchers

import org.objectweb.asm.tree.{MethodNode, ClassNode}

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
case class BytecodeMatch(startIndex:Int, endIndex:Int, cls:ClassNode, method:MethodNode) {
  override def toString: String = "from #" + startIndex + " to #" + endIndex + " in " + cls.name + "." + method.name + method.desc
}
