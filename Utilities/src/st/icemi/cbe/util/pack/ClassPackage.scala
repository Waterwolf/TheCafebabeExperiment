package st.icemi.cbe.util.pack

import collection.mutable
import org.objectweb.asm.tree.ClassNode

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
class ClassPackage {
  private val classes:mutable.Map[String, ClassNode] = mutable.Map()

  def add(node:ClassNode) { classes +=(node.name -> node) }

}