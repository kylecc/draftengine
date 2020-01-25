package draftengine.model.statistics

case class PlayerStatistics(playerId: String, projections: Statistics)

object PlayerStatistics {

  def main(args: Array[String]): Unit = {
    // example
    val statisticsYear = 2019
    val stats = Seq(Statistic("8", 37)) // 37 home runs
    val seasonStats2019 = SeasonStatistics(statisticsYear, stats)
    PlayerStatistics("1", seasonStats2019)
  }

}