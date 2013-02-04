package st.icemi.cbe.util.files

import java.io.File
import st.icemi.cbe.util.matchers.{PatternMatcher, GlobMatcher, Glob}

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
object FileFinder {

  private def listFiles(f: File, matcher: PatternMatcher): Array[File] = {
    f.listFiles().filter(f => matcher.matches(f.getName))
  }
  private def recursiveListFiles(f: File, matcher:PatternMatcher): Array[File] = {
    val allfiles = f.listFiles
    val these = allfiles.filter(f => matcher.matches(f.getName))
    these ++ allfiles.filter(_.isDirectory).flatMap(recursiveListFiles(_, matcher))
  }

  def find(root:File, pattern:String, recursive:Boolean):Array[File] = {
    val matcher = Glob(pattern)
    if (recursive)
      recursiveListFiles(root, matcher)
    else
      listFiles(root, matcher)
  }
}
