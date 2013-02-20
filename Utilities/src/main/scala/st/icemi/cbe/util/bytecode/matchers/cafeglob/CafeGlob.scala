package st.icemi.cbe.util.bytecode.matchers.cafeglob

import org.objectweb.asm.tree.{AbstractInsnNode, MethodNode, ClassNode}
import st.icemi.cbe.util.glob.{PatternParsingException, InvalidPatternException, CharStream}
import scala.Array
import st.icemi.cbe.util.matchers.{Glob, GlobMatcher}
import st.icemi.cbe.util.bytecode.matchers.{BytecodeMatch, BytecodePatternMatcher}
import collection.mutable
import st.icemi.cbe.util.bytecode.OpcodeHelpers
import annotation.tailrec
import java.lang.UnsupportedOperationException

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 18:10
 *
 * Custom implementation of glob for usage in bytecode pattern finders.
 *
 * Basic pattern:
 * ALOAD{0} POP
 *
 * Custom syntax:
 *  * outside {}: match any number of instructions
 *  ? outside {}: match a single instruction
 *      {?}     : match a single operand
 *    {[0-3]}   : match a number that equals to or is between 0 and 3
 *
 *  { "glob inside ()" } : anything inside parenthesis is parsed as a regular glob. You need to wrap glob in double quotes. Regular glob syntax applies here!
 *
 * Whitespace is accepted and ignored pretty much everywhere.
 * In CafeGlob underscores are part of literals due to bytecode requirements. Numbers that follow underscore are part of the literal as well.
 *
 * Context-specific syntax:
 *  - Works only in specific methods. Throws an exception if used elsewhere
 *
 *      {^}     : replaced with current class' name. Is in bytecode package format (uses slashes insted of periods)
 *      {~}     : replaced with current method's name
 *      {$}     : replaced with current method's signature
 *
 * Example usage:
 *
 *  ALOAD{0} GETFIELD{*} INVOKEVIRTUAL{^, println, ("*String;")}
 *   - Matches a method call that looks somewhat like this.println(this.aStringField);
 *
 *
*/

object CafeGlob {

  private def parseOpcodeExpression(lexer:CafeGlobLexer, literal:String):CafeGlobExpr = {
    val opcode = OpcodeHelpers.getByName(literal)
    if (!opcode.isDefined) throw new PatternParsingException("Invalid opcode detected: " + literal)

    val operands = mutable.MutableList[CafeGlobOperand]()

    val lbracket = CafeGlobParserUtils.accept(lexer, TokenType.LeftCurlyBracket)
    if (lbracket.isDefined) {

      def skipComma() = CafeGlobParserUtils.accept(lexer, TokenType.Comma)

      @tailrec
      def traverseOperands():Unit =  {
        var mytoken:Option[CafeGlobOperand] = None
        lexer.nextToken() match {
          case t if t.ttype == TokenType.RightCurlyBracket => None

          case t if t.ttype == TokenType.Question => mytoken = Some(Operands.CafeGlobOperandQue()); skipComma
          case t if t.ttype == TokenType.Hat => mytoken = Some(Operands.CafeGlobOperandSelfClass()); skipComma

          case t if t.ttype == TokenType.Number => mytoken = Some(Operands.CafeGlobOperandNumber(t.symbol.toInt)); skipComma
          case t if t.ttype == TokenType.Literal => mytoken = Some(Operands.CafeGlobOperandLiteral(t.symbol)); skipComma
          case t if t.ttype == TokenType.Identifier => mytoken = Some(Operands.CafeGlobOperandIdentifier(t.symbol)); skipComma

          case t if t.ttype == TokenType.LeftBracket => {
            val normalGlob = CafeGlobParserUtils.expect(lexer, TokenType.Identifier)
            CafeGlobParserUtils.expect(lexer, TokenType.RightBracket)
            mytoken = Some(Operands.CafeGlobOperandGlob(Glob(normalGlob.get.symbol)))
          }

          case t:Token => throw new PatternParsingException("Invalid token type " + t.ttype + " detected inside operand brackets")
          case _ => throw new Exception("I have no idea why I got here")
        }

        if (mytoken.isDefined) {
          mytoken map (operand => operands += operand)
          traverseOperands()
        }
      }

      traverseOperands()

      //CafeGlobParserUtils.expect(lexer, TokenType.RightCurlyBracket)
    }

    new Expressions.CafeGlobExprInstruction(opcode.get, operands: _*)
  }

  def compile(pattern:String):List[CafeGlobExpr] = {
    val chain = mutable.MutableList[CafeGlobExpr]()
    val lexer = new CafeGlobLexer(pattern)

    var token = lexer.nextToken()
    while (token.ttype != TokenType.EOF) {

      token.ttype match {
        case TokenType.Star => chain += Expressions.CafeGlobExprStar()
        case TokenType.Literal => chain += parseOpcodeExpression(lexer, token.symbol)
        case _ => throw new PatternParsingException("Unexpected " + token.ttype + "=> " + token.symbol + " !")
      }

      token = lexer.nextToken()
    }

    return chain.toList
  }

  def apply(pattern:String):CafeGlobMatcher = new CafeGlobMatcher(compile(pattern))

  def main(args: Array[String]) {
    val wat = compile("ALOAD{0} GETFIELD{*} INVOKEVIRTUAL{^, println, (\"*String;\")}")
    println(wat.mkString(" =>\n"))
  }

}

class CafeGlobMatcher(exprs: List[CafeGlobExpr]) extends BytecodePatternMatcher {

  import st.icemi.cbe.util.bytecode.InsnListForeachabler._

  def search(cn: ClassNode, mn: MethodNode): List[BytecodeMatch] = {
    if (exprs.size == 0) {
      return Nil // We're not gonna find anything with 0 exprs so might as well return empty list here
    }

    val matches = collection.mutable.MutableList[BytecodeMatch]()

    val instructions = mn.instructions

    // Some implicit magic, lol

    val nodeTraverser:ItemTraverser[AbstractInsnNode] = instructions
    val exprTraverser:ItemTraverser[CafeGlobExpr] = exprs

    var findStart = None // This stores the node from which we have started matching
    var curMatches = mutable.MutableList[AbstractInsnNode]()

    for (insn <- instructions.iterator()) {
      val curNode = exprTraverser.cur()
      if (curNode == null) {

        // Build a match
        matches += new BytecodeMatch(0, 0, cn, mn)

        // Reset progress trackers
        exprTraverser.reset()
        curMatches.clear()

      }
      else {

        // A tuple (ShouldProgressNode, ShouldProgressExpr)
        // Aka     (DidWeFindSomething, Should go to next expression?)
        val progressData = curNode.matches(nodeTraverser, exprTraverser)

        if (!progressData._1) { // The expression doesn't match the node
          exprTraverser.reset()
          curMatches.clear()
        }
        else {

          curMatches += nodeTraverser.cur()

          nodeTraverser.next()

          if (progressData._2)
            exprTraverser.next()

        }
        //println(insn)

      }
    }

    //matches += BytecodeMatch(0, 0, cn, mn)

    return matches.toList
  }
}

/**
 *  Methods should return null instead of throwing IndexOOB exceptions. Null used instead of None because
 */
trait ItemTraverser[T] {
  def cur():T
  def peek():T
  def next():T
  def reset()

  def traverse():T = {
    val curi = cur()
    next()
    return curi
  }
}

class GlobContext(var cn:ClassNode, var mn:MethodNode)

abstract class CafeGlobOperand {
  def matches(value:Any, context:GlobContext, operandTraverser: ItemTraverser[CafeGlobOperand]) = false
}
object Operands {
  case class CafeGlobOperandSelfClass() extends CafeGlobOperand {
    override def matches(value: Any, context: GlobContext, operandTraverser: ItemTraverser[CafeGlobOperand]): Boolean = throw new UnsupportedOperationException("Self not yet implemented")
  }
  case class CafeGlobOperandQue() extends CafeGlobOperand {
    override def matches(value: Any, context: GlobContext, operandTraverser: ItemTraverser[CafeGlobOperand]): Boolean = true
  }
  case class CafeGlobOperandRange(min: Int, max: Int) extends CafeGlobOperand {
    override def matches(value: Any, context: GlobContext, operandTraverser: ItemTraverser[CafeGlobOperand]): Boolean = value match {
      case i:Int => i <= min && i <= max
      case _ => throw new UnsupportedOperationException(value + " passed to OperandRange")
    }
  }
  case class CafeGlobOperandGlob(matcher:GlobMatcher) extends CafeGlobOperand {
    override def matches(value: Any, context: GlobContext, operandTraverser: ItemTraverser[CafeGlobOperand]): Boolean = value match {
      case s:CharSequence => matcher.matches(s)
      case _ => throw new UnsupportedOperationException(value + " passed to OperandGlob")
    }
  }
  case class CafeGlobOperandLiteral(str:String) extends CafeGlobOperand {
    override def matches(value: Any, context: GlobContext, operandTraverser: ItemTraverser[CafeGlobOperand]): Boolean = value match {
      case s:CharSequence => s.equals(str)
      case _ => throw new UnsupportedOperationException(value + " passed to OperandLiteral")
    }
  }
  case class CafeGlobOperandIdentifier(str:String) extends CafeGlobOperand {
    override def matches(value: Any, context: GlobContext, operandTraverser: ItemTraverser[CafeGlobOperand]): Boolean = value match {
      case s:CharSequence => s.equals(str)
      case _ => throw new UnsupportedOperationException(value + " passed to OperandIdentifier")
    }
  }
  case class CafeGlobOperandNumber(num:Int) extends CafeGlobOperand {
    override def matches(value: Any, context: GlobContext, operandTraverser: ItemTraverser[CafeGlobOperand]): Boolean = value match {
      case i:Int => i == num
      case _ => throw new UnsupportedOperationException(value + " passed to OperandNumber")
    }
  }
}
abstract class CafeGlobExpr {
  /**
   *
   * @param nodeTraverser
   * @param exprTraverser
   * @return (did we find something?, should exprTraverser.next() should be called?). So 1st bool = does match? and 2nd bool = go to next expr?
   */
  def matches(nodeTraverser: ItemTraverser[AbstractInsnNode], exprTraverser: ItemTraverser[CafeGlobExpr]):(Boolean, Boolean) = (true, true)
}

object Expressions {

  case class CafeGlobExprInstruction(opcode: Int, operands: CafeGlobOperand*) extends CafeGlobExpr {
    override def matches(nodeTraverser: ItemTraverser[AbstractInsnNode], exprTraverser: ItemTraverser[CafeGlobExpr]): (Boolean, Boolean) = {

      val node = nodeTraverser.cur()
      if (node.getOpcode == opcode) {
        val nodeOperandsOpt:Option[List[Any]] = OpcodeHelpers.getOperands(node)

        if (nodeOperandsOpt.isDefined) {

          val nodeOperands = nodeOperandsOpt.get
          var myOperands:Array[CafeGlobOperand] = operands.toArray // TODO minor mem optimization opportunity

          if (myOperands.length < nodeOperands.size) {
            for (num <- (myOperands.length until nodeOperands.size))
              myOperands = myOperands :+ Operands.CafeGlobOperandQue()
          }

          val doAllMatch = (nodeOperands, myOperands).zipped forall( (nop, mop) => { mop.matches(nop, null, null) } )
          //println("DOALLMATCH: " + doAllMatch)
          if (!doAllMatch)
            return (false, true)
        }

        return (true, true)
      }

      return (false, true) // Didn't match. Second value doesn't matter here
    }
  }
  case class CafeGlobExprStar() extends CafeGlobExpr {
    override def matches(nodeTraverser: ItemTraverser[AbstractInsnNode], exprTraverser: ItemTraverser[CafeGlobExpr]): (Boolean, Boolean) = {
      val nextMatches = exprTraverser.peek().matches(nodeTraverser, exprTraverser)._1
      return (true, nextMatches)
    }
  }
  case class CafeGlobExprQue() extends CafeGlobExpr {
    override def matches(nodeTraverser: ItemTraverser[AbstractInsnNode], exprTraverser: ItemTraverser[CafeGlobExpr]): (Boolean, Boolean) = {
      return (true, true)
    }
  }

}