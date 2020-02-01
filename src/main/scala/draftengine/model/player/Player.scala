package draftengine.model.player

case class Player(id: String, name: String, positionSet: PositionSet)

object Dummy {

  def main(args: Array[String]): Unit = {

    val (first, second) = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).splitAt(4)
    System.err.println(first)
    System.err.println(second)

  }

}