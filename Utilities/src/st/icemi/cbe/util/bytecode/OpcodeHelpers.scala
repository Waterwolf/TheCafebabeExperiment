package st.icemi.cbe.util.bytecode

import org.objectweb.asm.Opcodes
import collection.mutable
import org.objectweb.asm.tree.{MethodInsnNode, FieldInsnNode, InsnNode, AbstractInsnNode}
import java.lang.reflect.Field

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
object OpcodeHelpers {

  private var tmpMappings = mutable.Map[String, Int]()

  private def filterFields(field: Field): Boolean = field.getName match {
    case n if n.startsWith("ASM") => false
    case n if n.startsWith("ACC_") => false
    case n if n.startsWith("V1_") => false
    case n if n.startsWith("T_") => false
    case n if n.startsWith("F_") => false
    case n if n.startsWith("H_") => false
    case _ => true
  }

  {
    for (opfield <- classOf[Opcodes].getFields().filter(filterFields)) {
      if (opfield.getType() == classOf[Int]) {
        val value = opfield.getInt(null)
        val name = opfield.getName
        tmpMappings += (name -> value)

        //println(name + " " + value)
      }
    }
  }

  val nameMappings:Map[String, Int] = tmpMappings.map(kv => (kv._1, kv._2)).toMap
  val idMappings:Map[Int, String] = nameMappings map (_.swap)

  def getByName(name:String):Option[Int] = nameMappings.get(name)
  def getByOpcode(op:Int):Option[String] = idMappings.get(op)

  def getOperands(node:AbstractInsnNode):Option[List[Any]] =
    node match {
      case x:FieldInsnNode => Some(List(x.owner, x.name, x.desc))
      case x:MethodInsnNode => Some(List(x.owner, x.name, x.desc))
      case _ => None
    }

}
