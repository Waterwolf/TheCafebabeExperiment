package st.icemi.cbe.util.pack

import collection.mutable
import org.objectweb.asm.tree.ClassNode
import st.icemi.cbe.util.bytecode.matchers.{BytecodeMatch, BytecodePatternMatcher}
import st.icemi.cbe.util.bytecode.Searcher
import java.io.{FileOutputStream, File}
import java.util.jar.{JarEntry, JarOutputStream}
import st.icemi.cbe.util.files.ClassHelper

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
class ClassPackage(name:String = "default") {

  private val classes: mutable.MutableList[ClassNode] = mutable.MutableList()
  //private val classes: mutable.Map[String, ClassNode] = mutable.Map()
  private val resources: mutable.Map[String, Array[Byte]] = mutable.Map()

  //def +=(node: ClassNode) = classes +=(node.name -> node)
  def +=(node: ClassNode) = classes += node
  def replace(node: ClassNode, withNode: ClassNode) = classes.foldLeft(0) { (idx, x) =>
      if (x == node) {
        classes(idx) = withNode
      }
      idx+1
    }

  /**
   * Adds classes & resources from provided ClassPackage to this ClassPackage. Classes with same name might result in undefined behavior.
   * @param pack
   * @return
   */
  def +=(pack: ClassPackage) = {
    classes ++= pack.classes
    resources ++= pack.resources
  }

  /**
   *  Loops through ClassNodes (not resources)
   * @param mfunc
   */
  def foreach(mfunc:(ClassNode => Unit)) = classes foreach (p => mfunc(p))

  def search(matcher: BytecodePatternMatcher) = {
    val matches = collection.mutable.ListBuffer[BytecodeMatch]()
    foreach (node => matches ++= Searcher.findMatches(matcher, node))
    matches
  }

  def findClass(s: String) = classes.find(p => p.name.equals(s))
  def apply(s: String) = findClass(s)

  def findResource(s: String) = resources(s)

  def addResource(s: String, bytes: Array[Byte]) = resources += (s -> bytes)

  def saveAsJar(f: File) {
    val jos = new JarOutputStream(new FileOutputStream(f))

    classes.foreach(cn => {
      val bytes = ClassHelper.toBytes(cn, 0)
      jos.putNextEntry(new JarEntry(cn.name + ".class"))
      jos.write(bytes)
      jos.closeEntry()
    })

    resources.foreach(p => {
      jos.putNextEntry(new JarEntry(p._1))
      jos.write(p._2)
      jos.closeEntry()
    })

    jos.close()
  }

  def clonePack() = {
    val cp = new ClassPackage()

    foreach (cn => {
      val ncn = new ClassNode()
      ncn.accept(cn)
      cp += ncn
    })

    resources.foreach(p => {
      cp.addResource(p._1, p._2)
    })

    cp
  }

  /**
   *  Invalidates all caches
   */
  def updateClassIndices() {

  }

  def printContents() {
    println("Classes:")
    foreach (cn => println(cn.name))
    println("Resources:")
    resources.foreach(p => {
      println(p._1)
    })
  }

}