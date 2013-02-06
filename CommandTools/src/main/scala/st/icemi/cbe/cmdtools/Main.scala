package st.icemi.cbe.cmdtools

import java.lang.String
import scala.Predef.String
import st.icemi.cbe.util.matchers.{Glob, RegexMatcher, PatternMatcher}
import st.icemi.cbe.util.files.{ClassHelper, FileFinder}
import java.io.File
import org.objectweb.asm.ClassReader
import st.icemi.cbe.util.bytecode.Searcher
import st.icemi.cbe.util.bytecode.matchers.cafeglob.CafeGlob
import org.objectweb.asm.tree.ClassNode
import st.icemi.cbe.util.bytecode.matchers.BytecodeMatch

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 5.2.2013
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
object Main {
  val config = new Config()
  val parser = new scopt.mutable.OptionParser("TheCafebabeExperiment", "1.x") {
    booleanOpt("r", "recursive", "Should target files be searched from subdirectories?", { v: Boolean => config.recursive = v })
    booleanOpt("debug", "Should debug mode be enabled?", { v: Boolean => config.debug = v })
    opt("t", "targ", "<filepattern>", "What kind of files should be searched? Uses glob.", { v: String => config.targetFiles = v })
    arg("<pattern>", "Bytecode pattern. Uses CafeGlob.", { (v:String) => config.bcPattern = v })

    // arglist("<file>...", "arglist allows variable number of arguments",
    //   { v: String => config.files = (v :: config.files).reverse })
  }

  def main(args: Array[String]) {

    if (parser.parse(List.fromArray(args))) {

      val fileMatcher: PatternMatcher = Glob(config.targetFiles)
      val files: Array[File] = FileFinder.find(new File("."), config.targetFiles, config.recursive)
      /*
      if (debug) {
        System.out.println("Globbed files:")
        for (f <- files) {
          System.out.println(" " + f.getName)
        }
        System.out.println("End globbed files")
      }
      */
      val classes: Array[ClassNode] = ClassHelper.loadBunch(files, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES)
      /*
      if (debug) {
        System.out.println("Loaded classes:")
        for (node <- classes) {
          System.out.println(" " + node.name)
        }
        System.out.println("End loaded classes")
      }
      */
      val matches: List[BytecodeMatch] = Searcher.findAllMatches(CafeGlob(config.bcPattern), classes:_*)

      for (amatch <- matches) {
        Console.println(amatch)
      }

    }
  }
}

class Config {
  var recursive: Boolean = false
  var debug: Boolean = false
  var targetFiles: String = "*.class"
  var bcPattern: String = ""
}