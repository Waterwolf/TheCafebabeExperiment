package st.icemi.cbe.util.files

import java.io.{InputStream, FileInputStream, File}
import org.objectweb.asm.{ClassWriter, ClassReader}
import org.objectweb.asm.tree.ClassNode
import st.icemi.cbe.util.pack.ClassPackage

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
object ClassHelper {
  def loadSingle(is: InputStream, flags: Int): ClassNode = {
    val cr = new ClassReader(is)
    val cn = new ClassNode()
    cr.accept(cn, flags)
    return cn
  }
  def loadSingle(f:File, flags:Int):ClassNode = {
    return loadSingle(new FileInputStream(f), flags)
  }
  def loadBunch(f:Array[File], flags:Int): ClassPackage = {
    val cp = new ClassPackage()
    f foreach { el => cp += loadSingle(el, flags) }
    return cp
  }

  def toBytes(node: ClassNode, flags: Int) = {
    val writer = new ClassWriter(flags)
    node.accept(writer)
    writer.toByteArray
  }
}
