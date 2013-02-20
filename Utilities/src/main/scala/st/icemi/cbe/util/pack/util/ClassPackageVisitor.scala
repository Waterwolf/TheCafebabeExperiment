package st.icemi.cbe.util.pack.util

import st.icemi.cbe.util.pack.ClassPackage
import org.objectweb.asm.tree.ClassNode

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 17.2.2013
 * Time: 15:09
 */
trait ClassPackageVisitor {
  def visit(pack: ClassPackage)
}
trait PackageNodeVisitor extends ClassPackageVisitor {
  override def visit(pack: ClassPackage) {
    pack foreach (visitNode)
  }

  def visitNode(node: ClassNode)
}