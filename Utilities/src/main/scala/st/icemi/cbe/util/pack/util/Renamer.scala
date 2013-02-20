package st.icemi.cbe.util.pack.util

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.{Opcodes, ClassVisitor}
import st.icemi.cbe.util.pack.ClassPackage
import org.objectweb.asm.commons.{RemappingClassAdapter, Remapper, SimpleRemapper}

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 17.2.2013
 * Time: 15:08
 */
object Renamer {
  def rename(pack: ClassPackage, clsRenamer: (String => String) = null, methodRenamer: (String => String) = null, fieldRenamer: (String => String) = null) = {
    val renamer = new Renamer(clsRenamer, methodRenamer, fieldRenamer)

    pack.foreach(cn => {
      val ncn = new ClassNode()
      val rca = new RemappingClassAdapter(cn, renamer)
      ncn.accept(rca)

      pack.replace(cn, ncn)
    })

    pack.updateClassIndices()
  }
}
class Renamer(clsRenamer: (String => String), methodRenamer: (String => String), fieldRenamer: (String => String)) extends Remapper {

  private def printAndReturn[T](obj: T):T = {
    println(obj)
    obj
  }

  override def map(p1: String): String = {
    if (clsRenamer != null) {
      val renamed = clsRenamer(p1)
      println(p1 + " -> " + renamed)
      return renamed
    }
    printAndReturn(super.map(p1))
  }

  override def mapFieldName(p1: String, p2: String, p3: String): String = {
    if (fieldRenamer != null) {
      return fieldRenamer(p1)
    }
    printAndReturn(super.mapFieldName(p1, p2, p3))
  }

  override def mapMethodName(p1: String, p2: String, p3: String): String = {
    if (methodRenamer != null) {
      return methodRenamer(p1)
    }
    printAndReturn(super.mapMethodName(p1, p2, p3))
  }
}