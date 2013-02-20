package st.icemi.cbe.util.bytecode.matchers.cafeglob

import st.icemi.cbe.util.glob.{InvalidPatternException, CharStream}

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 3.2.2013
 * Time: 20:07
 * To change this template use File | Settings | File Templates.
 */
class CafeGlobLexer(cglob: String) {

  private val EOFChar = 0.asInstanceOf[Char]

  private val cs = new CharStream(cglob.toCharArray)

  private def isWhiteSpace(c:Char) = c == ' ' || c == '\t' || c == '/' || c == '\n' || c == '\r'

  /**
   *  Best coding EU 2013 (c) Official
   * @return
   */
  def peekToken():Token = {
    cs.pushPosition()
    val token = nextToken()
    cs.popPosition()
    token
  }

  def nextToken():Token = {
    var c = cs.consume
    while (isWhiteSpace(c)) {
      c = cs.consume
    }

    import TokenType._

    c match {
      case _ if Character.isLetter(c) => {
        val builder = new StringBuilder()
        builder.append(c)

        var underscored = false

        while (Character.isLetter(cs.peek) || cs.peek == '_' || (underscored && Character.isDigit(cs.peek))) {
          if (cs.peek == '_') {
            underscored = true
          }
          else
            underscored = false // Constantly falsify this because
          builder.append(cs.consume)
        }
        return new Token(Literal, builder.toString)
      }

      case '"' => {
        val builder = new StringBuilder()

        var backSlashed = false
        while (cs.peek != '"' || backSlashed) {
          if (cs.peek == '\\' && !backSlashed) {
            backSlashed = true
            cs.consume
          }
          else {
            builder.append(cs.consume)
            backSlashed = false
          }
        }
        cs.consume
        return new Token(Identifier, builder.toString)
      }

      case _ if Character.isDigit(c) => {
        val builder = new StringBuilder()
        builder.append(c)
        while (Character.isDigit(cs.peek) || cs.peek == '.')
          builder.append(cs.consume)
        return new Token(Number, builder.toString)
      }

      case '{' => return new Token(LeftCurlyBracket, "{")
      case '}' => return new Token(RightCurlyBracket, "}")
      case ',' => return new Token(Comma, ",")

      case '*' => return new Token(Star, "*")
      case '?' => return new Token(Question, "?")

      case '^' => return new Token(Hat, "^")

      case '(' => return new Token(LeftBracket, "(")
      case ')' => return new Token(RightBracket, ")")

      case '-' => return new Token(Hyphen, "-")
      case EOFChar => return new Token(EOF, "")
      case _ => throw new InvalidPatternException("Unexpected char " + c)
    }
  }

}

abstract class TokenType // I have no idea what am I doing
object TokenType {
  case object EOF extends TokenType
  case object Literal extends TokenType
  case object Hyphen extends TokenType
  case object Number extends TokenType
  case object Identifier extends TokenType
  case object Star extends TokenType
  case object Question extends TokenType
  case object Hat extends TokenType
  case object LeftCurlyBracket extends TokenType
  case object RightCurlyBracket extends TokenType
  case object LeftBracket extends TokenType
  case object RightBracket extends TokenType
  case object Comma extends TokenType
}
/*
object TokenType extends Enumeration {
  type TokenType = Value
  val Literal = Value("Literal")
  val EOF = Value("EOF")
  val Identifier, Star, Question, LeftCurlyBracket, RightCurlyBracket, LeftBracket, RightBracket, LeftSquareBracket, RightSquareBracket, Hyphen = Value
}*/

class Token(val ttype: TokenType, val symbol:String) {
  override def toString():String = ttype + "  (" + symbol + ")"
}