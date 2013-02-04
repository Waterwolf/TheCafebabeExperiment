package st.icemi.cbe.util.files

import java.io.{FileInputStream, File}
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
object ClassHelper {
  def loadSingle(f:File, flags:Int):ClassNode = {
    val cr = new ClassReader(new FileInputStream(f))
    val cn = new ClassNode()
    cr.accept(cn, flags)
    return cn
  }
  def loadBunch(f:Array[File], flags:Int):Array[ClassNode] = {
    val arr = new Array[ClassNode](f.length)
    f.zipWithIndex foreach { case (el, i) => arr(i) = loadSingle(el, flags) }
    return arr
  }
}
