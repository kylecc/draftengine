package draftengine.data.analyzer

import draftengine.data.loader.FileBasedDataLoader
import draftengine.model.player.{PlayerProjection, PositionSet}

class TopProjectedPlayersAtEachPositionAnalyzer(val data: Seq[(String, PlayerProjection, PositionSet)]) {

  def analyze(): Unit = {
    TopProjectedPlayersAtEachPositionAnalyzer.standardPositions.foreach {
      case (ps, label) => printDetails(ps, label)
    }
  }

  private def printDetails(positionSetFilter: PositionSet => Boolean, label: String): Unit = {
    val top = data
      .filter { case (_, _, positionSet) => positionSetFilter(positionSet) }
      .take(5)
    System.err.println(s"Top 5 projected $label:")
    top.foreach(System.err.println)
  }

}

object TopProjectedPlayersAtEachPositionAnalyzer {

  def main(args: Array[String]): Unit = {
    val data2020 = new FileBasedDataLoader().load2020ProjectionsSpreadsheet()
    new TopProjectedPlayersAtEachPositionAnalyzer(data2020).analyze()
  }

  val standardPositions: Seq[(PositionSet => Boolean, String)] = Seq(
    ((p: PositionSet) => p.isCatcher, "catchers"),
    ((p: PositionSet) => p.isFirstBase, "first basemen"),
    ((p: PositionSet) => p.isSecondBase, "second basemen"),
    ((p: PositionSet) => p.isThirdBase, "third basemen"),
    ((p: PositionSet) => p.isShortstop, "shortstops"),
    ((p: PositionSet) => p.isOutfield, "outfielders"),
    ((p: PositionSet) => p.isPitcher, "pitchers")
  )

}