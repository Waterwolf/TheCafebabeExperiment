package st.icemi.cbe.cmdtools

import st.icemi.cbe.util.matchers.{Glob, RegexMatcher, PatternMatcher}
import st.icemi.cbe.util.files.{ClassHelper, FileFinder}
import java.io.File
import org.objectweb.asm.ClassReader
import st.icemi.cbe.util.bytecode.Searcher
import st.icemi.cbe.util.bytecode.matchers.cafeglob.CafeGlob
import org.objectweb.asm.tree.ClassNode
import st.icemi.cbe.util.bytecode.matchers.BytecodeMatch
import st.icemi.cbe.util.pack.ClassPackage

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
    opt("d", "debug", "Should debug mode be enabled?", { config.debug = true })
    opt("c", "count", "Print only the count of matches.", { config.printCount = true })
    opt("t", "targ", "<filepattern>", "What kind of files should be searched? Uses glob.", { v: String => config.targetFiles = v })
    opt("root", "<folder", "The file to start searching from.", { v: String => config.targetFiles = v })
    arg("<pattern>", "Bytecode pattern. Uses CafeGlob.", { (v:String) => config.bcPattern = v })

    // arglist("<file>...", "arglist allows variable number of arguments",
    //   { v: String => config.files = (v :: config.files).reverse })
  }

  def main(args: Array[String]) {

    if (parser.parse(List.fromArray(args))) {

      val searchRoot = new File(config.searchRoot)

      val fileMatcher: PatternMatcher = Glob(config.targetFiles)
      val files: Array[File] = FileFinder.find(searchRoot, config.targetFiles, config.recursive)

      if (config.debug) {
        println(s"Searched from ${searchRoot.getAbsolutePath} using pattern ${config.targetFiles} ")

        println(" == Globbed files: == ")

        files.foreach(f => println(f.getName))

        println(" == End of globbed files == ")
      }

      val classes: ClassPackage = ClassHelper.loadBunch(files, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES)

      if (config.debug) {
        println(" == Loaded classes: == ")

        classes.foreach(cn => println(cn.name))

        println(" == End of loaded classes == ")
      }

      val matches: List[BytecodeMatch] = Searcher.findAllMatches(CafeGlob(config.bcPattern), classes)

      if (config.debug) {
        Console.println(matches.size + " matches found:")
      }

      if (config.printCount && !config.debug) {
        println(matches.size)
      }
      else {

        for (amatch <- matches) {
          Console.println(amatch)
        }

      }

    }
  }
}

class Config {
  var printCount = false
  var recursive: Boolean = false
  var debug: Boolean = false
  var targetFiles: String = "*.class"
  var bcPattern: String = ""
  var searchRoot = "."
}