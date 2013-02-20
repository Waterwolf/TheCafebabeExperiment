package st.icemi.cbe.util.pack

import java.applet.Applet
import org.objectweb.asm.tree.ClassNode
import st.icemi.cbe.util.files.ClassHelper
import java.io.{ByteArrayInputStream, InputStream}

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 10.2.2013
 * Time: 16:09
 */
class RunnablePackage(packs: ClassPackage*) {

  def findClassNode(name: String): ClassNode = {
    val bname = name.replace(".", "/")

    for (pack <- packs) {
      val cn = pack.findClass(bname)
      cn match {
        case Some(cn) => cn
        case None =>
      }
    }
    return null
  }

  def findPackageResource(name: String): Array[Byte] = {
    for (pack <- packs) {
      val res = pack.findResource(name)
      if (res != null)
        return res
    }
    return null
  }

  protected val classLoader = new ClassLoader() {

    override def findClass(name: String): Class[_] = {
      try {
        return findSystemClass(name);
      } catch {
        case _: Throwable =>
      }

      val fn = findClassNode(name)
      if (fn != null) {
        val bytes = ClassHelper.toBytes(fn, 0)
        return defineClass(fn.name, bytes, 0, bytes.length)
      }

      super.findClass(name)
    }

    override def loadClass(name: String): Class[_] = findClass(name)

    override def getResourceAsStream(name: String): InputStream = {

      val res = findPackageResource(name)
      if (res != null)
        return new ByteArrayInputStream(res)

      println("Resource " + name + " wasn't found normally!!")

      super.getResourceAsStream(name)
    }
  }

  def runAsApplication(mainClass: String) {
    throw new NotImplementedError()
  }
  def runAsApplet(mainClass: String, preInit: (Applet => Boolean) = null): Applet = {

    val applet = classLoader.loadClass(mainClass).newInstance().asInstanceOf[Applet]

    if (preInit != null) {
      if (!preInit(applet))
        return applet
    }

    applet.init()
    applet.start()

    applet
  }
}
