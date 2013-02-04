package st.icemi.cbe.util.glob

import collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: wolf
 * Date: 1.2.2013
 * Time: 18:02
 * To change this template use File | Settings | File Templates.
 */
case class CharStream(charArray:Array[Char]) {

  private def chars(index:Int): Char = if (charArray.length > index) charArray(index) else 0.asInstanceOf[Char]

  var pos = 0
  def peek:Char = chars(pos)
  def consume:Char = chars(getPosAndIncrement)
  def first_? = (pos==0)
  def remaining_? = (pos<charArray.length)
  def getPosAndIncrement = {
    val rec = pos
    pos = rec + 1
    rec
  }

  val positionStack = mutable.Stack[Int]()
  def popPosition() = pos = positionStack.pop()
  def pushPosition() = positionStack.push(pos)

  override def toString:String = {
    val b = new StringBuilder
    b.append("<<")
    for(i <- 0 until charArray.length) {
      if(i==pos)
        b.append("<").append(chars(i)).append('>')
      else
        b.append(chars(i))
    }
    b.append(">>")
    b.toString()
  }
}
