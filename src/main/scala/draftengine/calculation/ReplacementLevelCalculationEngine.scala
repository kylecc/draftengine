package draftengine.calculation

import draftengine.data.loader.projections.mapper.ProjectionsDataRowMapperKyleRocBoiCo2020
import draftengine.data.loader.projections.{ProjectionsDataFromExcelFileLoader, ProjectionsDataLoader}
import draftengine.model.league.LeagueRules
import draftengine.model.player.{Player, PlayerProjection, PositionSet}
import draftengine.model.statistics.{ProjectionsSource, SeasonStatistics, Statistic}
import draftengine.service.{PlayerService, SimpleCachedPlayerService}
import draftengine.service.positions.{PositionsService, SimplePositionsService}
import draftengine.service.projections.AggregatingProjectionsCachedFileBasedService
import org.apache.poi.ss.usermodel.Row

class ReplacementLevelCalculationEngine(
  val calculationContext: CalculationContext[Double],
  val playerService: PlayerService)
  extends CalculationEngine[Double] {

  override def calculate(): Seq[CalculationOutputComponent[Double]] = {
    performCalculation().map {
      case (playerId, outputStats) => CalculationOutputComponent(playerId, outputStats)
    }
  }

  private def performCalculation(): Seq[(String, Map[String, Double])] = {
    val numberOfPositionsPerTeam = getTeamPositionsForLeague()
    val positionsForEachTeam = numberOfPositionsPerTeam.keySet
    val playersForCalculation = getPlayersForCalculation()
    val playersForEachPosition = getPlayersEligibleAtEachPosition(positionsForEachTeam, playersForCalculation)
    val replacementLevelByPosition = findReplacementLevelByPosition(playersForEachPosition, numberOfPositionsPerTeam)
    calculatePointsAboveReplacement(playersForCalculation, replacementLevelByPosition)
  }

  private def calculatePointsAboveReplacement(
    playersForCalculation: Seq[Player],
    replacementLevelByPosition: Map[PositionSet, (Double, String)]): Seq[(String, Map[String, Double])] = {

    playersForCalculation.map {
      case Player(playerId, _, positionSet) => {
        val pointsAboveReplacement = replacementLevelByPosition.filter {
          case (pSet, _) => positionSet.isEligibleAt(pSet)
        }.map {
          case (_, (replacementLevelValue, _)) => {
            val playerInputProjection = getRocBoiCoPlayerPointsProjection(playerId)
            playerInputProjection - replacementLevelValue
          }
        }.max
        playerId -> Map(Statistic.RocBoiCoPointsAboveReplacement -> pointsAboveReplacement)
      }
    }
  }

  private def getRocBoiCoPlayerPointsProjection(playerId: String): Double = {
    calculationContext.playerInputStatistics.collect {
      case CalculationInputComponent(pId, inputStats) if pId == playerId => inputStats.getOrElse(Statistic.RocBoiCoPoints, 0d)
    }.headOption.getOrElse(0d)
  }

  private def findReplacementLevelByPosition(
    playersForEachPosition: Map[PositionSet, Seq[Player]],
    numberOfPositionsPerTeam: Map[PositionSet, Int]): Map[PositionSet, (Double, String)] = {

    playersForEachPosition.map {
      case (fantasyPosition, eligiblePlayers) => {
        val playerPoints = eligiblePlayers.map { player => player.id -> getRocBoiCoPointsForPlayer(player.id) }
        val playersSortedByPoints = playerPoints.sortBy { case (_, points) => points }
        val numStartingPlayers = numberOfPositionsPerTeam(fantasyPosition) * calculationContext.leagueRules.numberOfTeams
        val (_, replacementLevel) = playersSortedByPoints.splitAt(numStartingPlayers)
        fantasyPosition -> replacementLevel.headOption.map {
          case (playerId, points) => (points, playerService.getPlayer(playerId).name)
        }.getOrElse((0d, "Nobody"))
      }
    }
  }

  private def getRocBoiCoPointsForPlayer(playerId: String): Double = {
    calculationContext.playerInputStatistics.find {
      case CalculationInputComponent(pid, _) => pid == playerId
    }.map {
      case CalculationInputComponent(_, playerInputStats) => playerInputStats.getOrElse(Statistic.RocBoiCoPoints, 0d)
    }.getOrElse(0d)
  }

  private def getPlayersEligibleAtEachPosition(
    positionsForEachTeam: Set[PositionSet],
    playersForCalculation: Seq[Player]): Map[PositionSet, Seq[Player]] = {

    positionsForEachTeam.map { fantasyPosition =>
      fantasyPosition -> playersForCalculation.filter {
        case Player(_, _, playerEligiblePositions) => fantasyPosition.isEligibleAt(playerEligiblePositions)
      }
    }.toMap
  }

  private def getPlayersForCalculation(): Seq[Player] = {
    calculationContext.playerInputStatistics.map {
      case CalculationInputComponent(playerId, _) => playerService.getPlayer(playerId)
    }
  }

  private def getTeamPositionsForLeague(): Map[PositionSet, Int] = {
    calculationContext.leagueRules.numberOfPositionsMap
  }

}

object ReplacementLevelCalculationEngine {

  def main(args: Array[String]): Unit = {
    val projectionsLoaders = Seq(ProjectionsSource.KYLE -> getKyleProjectionsLoader())
    val projectionsService = new AggregatingProjectionsCachedFileBasedService(projectionsLoaders)
    val allPlayerProjections = projectionsService.getAllPlayerProjections(ProjectionsSource.KYLE)
    val leagueRules = LeagueRules.getRocBoiCoPointsRules()
    val playerInputStats = convertProjectionsToCalcInputFormat(allPlayerProjections)
    val calculationContext = CalculationContext(playerInputStats, leagueRules, Seq.empty)
    val playerService = new SimpleCachedPlayerService()
    val res = new ReplacementLevelCalculationEngine(calculationContext, playerService).calculate()
    res.foreach(System.err.println)
  }

  private def convertProjectionsToCalcInputFormat(
    allPlayerProjections: Seq[PlayerProjection]): Seq[CalculationInputComponent[Double]] = {

    allPlayerProjections.map {
      case PlayerProjection(playerId, _, statistics: SeasonStatistics) => {
        val rbcPointsProjection = statistics.statistics.collect {
          case Statistic(statId, value) if statId == Statistic.RocBoiCoPoints => statId -> value
        }.toMap
        CalculationInputComponent(playerId, rbcPointsProjection)
      }
    }
  }

  private def getKyleProjectionsLoader(): ProjectionsDataLoader[Row, PlayerProjection] = {
    val (fileName, worksheetName) = ("C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx", "2020 projected")
    val positionsService = getPositionsService(fileName, worksheetName)
    val rowMapper = new ProjectionsDataRowMapperKyleRocBoiCo2020(positionsService)
    new ProjectionsDataFromExcelFileLoader(2020, fileName, worksheetName, rowMapper)
  }

  private def getPositionsService(fileName: String, worksheetName: String): PositionsService = {
    new SimplePositionsService(fileName, worksheetName)
  }

}