package st.icemi.cbe.util.bytecode

import java.util
import matchers.cafeglob.ItemTraverser
import org.objectweb.asm.tree.{InsnList, AbstractInsnNode}

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 3.2.2013
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
object InsnListForeachabler {
  implicit class IteratorToForeach[T](val it:util.ListIterator[T]) {
    def foreach(func: T => Unit) {
      while (it.hasNext) {
        func(it.next())
      }
    }
  }

  implicit class ListToTraverser[T](val list:List[T]) extends ItemTraverser[T] {
    private var index = 0

    private def getElement(index:Int):T = if (0 <= index && index < list.size) list(index) else null.asInstanceOf[T]

    def cur(): T = getElement(index)
    def peek(): T = getElement(index+1)
    def next(): T = {
      index += 1
      getElement(index)
    }
    def reset() = index = 0
  }

  implicit class InsnListToTraverser(val instructions:InsnList) extends ItemTraverser[AbstractInsnNode] {
    private var index = 0

    def cur(): AbstractInsnNode = instructions.get(index)
    def peek(): AbstractInsnNode = instructions.get(index+1)
    def next(): AbstractInsnNode = {
      index += 1
      instructions.get(index)
    }
    def reset() = index = 0
  }

}
