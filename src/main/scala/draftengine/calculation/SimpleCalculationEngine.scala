package draftengine.calculation

import draftengine.model.league.LeagueRules
import draftengine.model.player.Player
import draftengine.model.statistics.Statistic
import draftengine.service.{PlayerService, SimpleCachedPlayerService}

class SimpleCalculationEngine[T](
  val calculationContext: CalculationContext[T],
  val playerService: PlayerService)
  extends CalculationEngine[T] {

  override def calculate(): Seq[CalculationOutputComponent[T]] = {
    val calculationOutputs = performCalculations()
    calculationOutputs.map {
      case (playerId, playerOutputs) => CalculationOutputComponent(playerId, playerOutputs)
    }.toSeq
  }

  private def performCalculations(): Map[String, Map[String, T]] = {
    val playersForCalculation = getPlayersForCalculation()
    val playersAndInputs = alignPlayersWithInputs(playersForCalculation)
    val calculations = getCalculations()
    getCalculationsForAllPlayers(playersAndInputs, calculations)
  }

  private def getCalculationsForAllPlayers(
    playersAndInputs: Seq[(Player, CalculationInputComponent[T])],
    calculations: Seq[Calculation[T]]) = {

    playersAndInputs.map {
      case (player, playerInputs) => {
        val outputs = calculations.map {
          case calculation => calculation.outputStatisticId -> calculation.outputCalculator(playerInputs)
        }.toMap
        player.id -> outputs
      }
    }.toMap
  }

  private def alignPlayersWithInputs(players: Seq[Player]): Seq[(Player, CalculationInputComponent[T])] = {
    players.map {
      case p: Player => calculationContext.playerInputStatistics.find {
        case CalculationInputComponent(playerId, _) => p.id == playerId
      }.map {
        case playerInputs => (p, playerInputs)
      }
    }.collect {
      case Some(playerAndInputs) => playerAndInputs
    }
  }

  private def getPlayersForCalculation(): Seq[Player] = {
    calculationContext.playerInputStatistics.map {
      case CalculationInputComponent(playerId, _) => playerService.getPlayer(playerId)
    }
  }

  private def getCalculations(): Seq[Calculation[T]] = {
    calculationContext.outputStatisticsCalculators
  }

}

object SimpleCalculationEngine {

  private val randomPlayerId = "10155"

  def main(args: Array[String]): Unit = {
    run()
  }

  private def run(): Unit = {
    val inputStats = getPlayerInputStatistics()
    val leagueRules = LeagueRules.getRocBoiCoPointsRules()
    val calculators = getOutputStatisticsCalculatorsHitters()
    val calculationContext = CalculationContext(inputStats, leagueRules, calculators)
    val playerService = new SimpleCachedPlayerService()
    val calculationEngine = new SimpleCalculationEngine[Double](calculationContext, playerService)
    val results = calculationEngine.calculate()
    results.foreach(System.err.println)
  }

  private def getPlayerInputStatistics(): Seq[CalculationInputComponent[Double]] = {
    val playerStats = Map(
      Statistic.GamesPlayed -> 10d,
      Statistic.Singles -> 12d,
      Statistic.Doubles -> 6d)
    Seq(
      CalculationInputComponent(randomPlayerId, playerStats)
    )
  }

  private def getOutputStatisticsCalculatorsHitters(): Seq[Calculation[Double]] = {
    Seq(PointsLeagueCalculation(Statistic.RocBoiCoPoints, rbcPoints2020HitterCalculator))
  }

  private def getOutputStatisticsCalculatorsPitchers(): Seq[Calculation[Double]] = {
    Seq(PointsLeagueCalculation(Statistic.RocBoiCoPoints, rbcPoints2020PitcherCalculator))
  }

  private def rbcPoints2020HitterCalculator: CalculationInputComponent[Double] => Double = {
    case CalculationInputComponent(_, inputStatistics) => {
      val singles = inputStatistics.getOrElse(Statistic.Singles, 0d)
      val doubles = inputStatistics.getOrElse(Statistic.Doubles, 0d)
      val triples = inputStatistics.getOrElse(Statistic.Triples, 0d)
      val homeRuns = inputStatistics.getOrElse(Statistic.HomeRuns, 0d)
      val runs = inputStatistics.getOrElse(Statistic.RunsScored, 0d)
      val rbis = inputStatistics.getOrElse(Statistic.RunsBattedIn, 0d)
      val walks = inputStatistics.getOrElse(Statistic.WalksHitter, 0d)
      val strikeouts = inputStatistics.getOrElse(Statistic.StrikeoutsHitter, 0d)
      val stolenBases = inputStatistics.getOrElse(Statistic.StolenBases, 0d)
      val rbcPoints = singles + (2 * doubles) + (3 * triples) +
        (5 * homeRuns) + runs + rbis + walks - strikeouts + (2 * stolenBases)
      rbcPoints
    }
  }

  private def rbcPoints2020PitcherCalculator: CalculationInputComponent[Double] => Double = {
    case CalculationInputComponent(_, inputStatistics) => {
      val inningsPitched = inputStatistics.getOrElse(Statistic.InningsPitched, 0d)
      val outsRecorded = getOutsRecorded(inningsPitched)
      val wins = inputStatistics.getOrElse(Statistic.Wins, 0d)
      val hitsAllowed = inputStatistics.getOrElse(Statistic.HitsAllowed, 0d)
      val earnedRunsAllowed = inputStatistics.getOrElse(Statistic.EarnedRunsAllowed, 0d)
      val walksAllowed = inputStatistics.getOrElse(Statistic.WalksAllowed, 0d)
      val strikeouts = inputStatistics.getOrElse(Statistic.StrikeoutsPitcher, 0d)
      val completeGames = inputStatistics.getOrElse(Statistic.CompleteGames, 0d)
      val shutouts = inputStatistics.getOrElse(Statistic.Shutouts, 0d)
      val qualityStarts = inputStatistics.getOrElse(Statistic.QualityStarts, 0d)
      val noHitters = inputStatistics.getOrElse(Statistic.NoHitters, 0d)
      val perfectGames = inputStatistics.getOrElse(Statistic.PerfectGames, 0d)
      val saves = inputStatistics.getOrElse(Statistic.Saves, 0d)
      val blownSaves = inputStatistics.getOrElse(Statistic.BlownSaves, 0d)
      val rbcPoints = outsRecorded - hitsAllowed - (2 * earnedRunsAllowed) - walksAllowed +
        strikeouts + (2 * wins) + (4 * qualityStarts) + (3 * completeGames) + (5 * shutouts) +
        (7 * noHitters) + (7 * perfectGames) + (5 * saves) - (3 * blownSaves)
      rbcPoints
    }
  }

  private def getOutsRecorded(inningsPitched: Double): Double = {
    val (wholeInnings, remainder) = inningsPitched.toString.split(".") match {
      case Array() => (inningsPitched, 0d)
      case Array(whole, extra) => (whole.toDouble, extra.toDouble)
    }
    (3 * wholeInnings) + remainder
  }

}