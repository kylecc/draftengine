package draftengine.calculator.projections

import draftengine.data.loader.projections.{ProjectionsDataFromCSVFileLoader, ProjectionsDataFromExcelFileLoader}
import draftengine.data.loader.projections.mapper.{ProjectionsDataRowMapperKyleRocBoiCo2020, ProjectionsDataRowMapperSteamer2020}
import draftengine.model.player.PlayerProjection
import draftengine.model.statistics.{SeasonStatistics, Statistic}
import draftengine.service.SimpleCachedPlayerService

class ProjectionsDifferenceCalculator(
  val playerIds: Seq[String],
  val firstProjectionSet: Map[String, PlayerProjection],
  val secondProjectionSet: Map[String, PlayerProjection]) {

  def calculateDifferences(): Seq[(String, Int)] = {
    playerIds.flatMap { playerId =>
      val firstProjection = firstProjectionSet.getOrElse(playerId, PlayerProjection.empty(playerId))
      val secondProjection = secondProjectionSet.getOrElse(playerId, PlayerProjection.empty(playerId))
      val rbcPointsFirst = getRbcPoints(firstProjection)
      val rbcPointsSecond = getRbcPoints(secondProjection)
      (rbcPointsFirst, rbcPointsSecond) match {
        case (first, second) if first != 0 && second != 0 => {
          val pointsDifference = rbcPointsFirst - rbcPointsSecond
          Some(playerId -> pointsDifference)
        }
        case _ => None
      }
    }
  }

  private def getRbcPoints(playerProjection: PlayerProjection): Int = {
    val rbcPointsProjected = playerProjection match {
      case PlayerProjection(_, _, SeasonStatistics(_, statistics)) => {
        statistics.find(_.statisticId == Statistic.ROC_BOI_CO_POINTS).map(_.value)
      }
      case _ => None
    }
    rbcPointsProjected.getOrElse(0.0).toInt
  }

}

object ProjectionsDifferenceCalculator {

  def main(args: Array[String]): Unit = {
    val kyleProjections = getKyleProjections().map(p => p.playerId -> p).toMap
    val steamerProjections = getSteamerProjections().map(p => p.playerId -> p).toMap
    val playerIds = (kyleProjections.keySet ++ steamerProjections.keySet).toSeq
    val res = new ProjectionsDifferenceCalculator(playerIds, kyleProjections, steamerProjections).calculateDifferences()
    val playerService = new SimpleCachedPlayerService()
    res
      .sortBy { case (_, diff) => Math.abs(diff) * -1 }
      .map { case (playerId, diff) => playerService.getPlayer(playerId).name -> diff }
      .foreach(System.err.println)
  }

  private def getKyleProjections(): Seq[PlayerProjection] = {
    val rbc2020FileName = ("C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx", "2020 projected")
    val (file, worksheet) = rbc2020FileName
    val rowMapper = new ProjectionsDataRowMapperKyleRocBoiCo2020()
    new ProjectionsDataFromExcelFileLoader(2020, file, worksheet, rowMapper).load()
  }

  private def getSteamerProjections(): Seq[PlayerProjection] = {
    val steamer2020FileName = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Steamer 2020 projections as of Jan 19 2020 - do not edit.csv"
    val rowMapper = new ProjectionsDataRowMapperSteamer2020()
    new ProjectionsDataFromCSVFileLoader(2020, steamer2020FileName, rowMapper).load()
  }

}