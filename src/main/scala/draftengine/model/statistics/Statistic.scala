package draftengine.model.statistics

case class Statistic(statisticId: String, value: Double)

object Statistic {

  val GAMES_PLAYED = "1"

  val ROC_BOI_CO_POINTS = "100"
  val ROC_BOI_CO_PER_GAME = "101"

}