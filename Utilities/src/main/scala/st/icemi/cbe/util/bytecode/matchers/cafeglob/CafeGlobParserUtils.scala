package st.icemi.cbe.util.bytecode.matchers.cafeglob

import st.icemi.cbe.util.glob.PatternParsingException

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 4.2.2013
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
object CafeGlobParserUtils {
  def expect(lexer: CafeGlobLexer, ttype: TokenType): Option[Token] = lexer.nextToken() match {
      case t if ttype == t.ttype => Some(t)
      case t:Token => throw new PatternParsingException("Expected " + ttype + ". Got " + t.ttype + "src/main")
    }

  def accept(lexer: CafeGlobLexer, ttype: TokenType): Option[Token] = lexer.peekToken() match {
    case t if ttype == t.ttype => Some(lexer.nextToken()) // nextToken() should always be same as peekToken (TODO make a test for this) so this handily puts us forward and returns identical token
    case _ => None
  }
}
