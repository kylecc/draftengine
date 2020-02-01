package draftengine.model.league

import java.util.concurrent.atomic.AtomicInteger

import draftengine.model.player.{Position, PositionSet}
import draftengine.model.statistics.Statistic

// TODO: probably make this modeling more sophisticated
case class LeagueRules(
  leagueRulesId: String,
  numberOfTeams: Int,
  statisticsPointsMap: Map[String, Double],
  numberOfPositionsMap: Map[PositionSet, Int])

object LeagueRules {

  private lazy val idCounter = new AtomicInteger()
  private val numberOfTeams2020 = 12

  import Position._
  import Statistic._

  def getRocBoiCoPointsRules(): LeagueRules = {
    val statsPointsMap = buildRocBoiCoStatsPointsMap()
    val numberOfPositionsMap = buildRocBoiCoNumberOfPositionsMap()
    LeagueRules(idCounter.incrementAndGet().toString, numberOfTeams2020, statsPointsMap, numberOfPositionsMap)
  }

  private def buildRocBoiCoStatsPointsMap(): Map[String, Double] = {
    Map(
      Singles -> 1d,
      Doubles -> 2d,
      Triples -> 3d,
      HomeRuns -> 5d,
      RunsScored -> 1d,
      RunsBattedIn -> 1d,
      StolenBases -> 2d,
      WalksHitter -> 1d,
      StrikeoutsHitter -> -1d)
  }

  private def buildRocBoiCoNumberOfPositionsMap(): Map[PositionSet, Int] = {
    Map(
      PositionSet(Catcher) -> 1,
      PositionSet(FirstBase) -> 1,
      PositionSet(SecondBase) -> 1,
      PositionSet(ThirdBase) -> 1,
      PositionSet(Shortstop) -> 1,
      PositionSet.middleInfield -> 1,
      PositionSet.cornerInfield -> 1,
      PositionSet(Outfield) -> 5,
    )
  }

}