package st.icemi.cbe.util

import files.ClassHelper
import java.net.URL
import java.io._
import io.Source
import pack.ClassPackage
import java.util.jar.{JarEntry, JarInputStream}
import java.util.zip.ZipEntry

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 9.2.2013
 * Time: 16:36
 */
object Loader {

  final private[this] def getZipEntries(jar: JarInputStream): Stream[ZipEntry] = {
    val head = Option(jar.getNextEntry)
    head.map(_ #:: { getZipEntries(jar) }).getOrElse(Stream.Empty)
  }

  def load(loadable: Loadable): ClassPackage = {
    val bytes = loadable.load()
    // Assume jar
    val cp = new ClassPackage()

    val jis = new JarInputStream(new ByteArrayInputStream(bytes))

    getZipEntries(jis).foreach(e => {
      if (e.getName.endsWith(".class")) {
        cp += ClassHelper.loadSingle(jis, 0)
      }
      else {
        val bis = new BufferedInputStream(jis)
        cp.addResource(e.getName, Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray)
      }
      jis.closeEntry()
    })

    val manifest = jis.getManifest
    if (manifest != null) {
      // TODO read manifest and store as resource in ClassPackage
    }

    cp
  }

  sealed abstract class Loadable {
    protected def getInputStream(): InputStream
    protected var cachedData: Array[Byte] = null

    protected def readBytes() = {
      val bis = new BufferedInputStream(getInputStream)
      Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
    }

    def load() = {
      if (cachedData == null)
        cachedData = readBytes()
      cachedData
    }

  }
  object Loadables {

    case class LoadableUrl(url: URL) extends Loadable {
      protected def getInputStream(): InputStream = url.openStream
    }
    case class LoadableFile(file: File) extends Loadable {
      protected def getInputStream(): InputStream = new FileInputStream(file)
    }

  }

}


