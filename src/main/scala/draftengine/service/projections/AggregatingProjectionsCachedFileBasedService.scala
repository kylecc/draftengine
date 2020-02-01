package draftengine.service.projections

import draftengine.data.loader.projections.mapper.{ProjectionsDataRowMapperKyleRocBoiCo2020, ProjectionsDataRowMapperStandard2020}
import draftengine.data.loader.projections.{ProjectionsDataFromCSVFileLoader, ProjectionsDataFromExcelFileLoader, ProjectionsDataLoader}
import draftengine.model.player.PlayerProjection
import draftengine.model.statistics.{ProjectionsSource, SeasonStatistics, Statistic}
import draftengine.service.positions.{PositionsService, SimplePositionsService}
import org.apache.poi.ss.usermodel.Row

class AggregatingProjectionsCachedFileBasedService(
  val projectionLoaders: Seq[(String, ProjectionsDataLoader[_, PlayerProjection])])
  extends ProjectionsService {

  private lazy val playerProjections = getPlayerProjections()
  private val year = 2020 // TODO: change?

  override def getPlayerProjections(playerId: String): Seq[PlayerProjection] = {
    val aggregatedProjection = new ProjectionOutputsCalculator(playerId, year, playerProjections).calculate()
    Seq(aggregatedProjection)
  }

  private def getPlayerProjections(): Map[String, Map[String, PlayerProjection]] = {
    projectionLoaders.map { case (source, loader) =>
      val projectionsByPlayerId = loader.load().map(p => p.playerId -> p).toMap
      source -> projectionsByPlayerId
    }.toMap
  }

}

class ProjectionOutputsCalculator(
  val playerId: String,
  val year: Int,
  val playerProjectionsBySource: Map[String, Map[String, PlayerProjection]]) {

  private val defaultProjectionsSource = ProjectionsSource.COMPOSITE

  def calculate(): PlayerProjection = {
    val allProjections = playerProjectionsBySource.flatMap { case (_, projectionsByPlayerId) =>
      projectionsByPlayerId.get(playerId)
    }.collect {
      case PlayerProjection(_, _, SeasonStatistics(_, stats)) =>
        stats.find(_.statisticId == Statistic.RocBoiCoPoints).map(_.value)
    }.collect {
      case Some(value) => value
    }
    val averageProjectionRbcPoints = allProjections.sum / allProjections.size
    val rbcPointsStat = Statistic(Statistic.RocBoiCoPoints, averageProjectionRbcPoints)
    val stats = SeasonStatistics(year, Seq(rbcPointsStat))
    PlayerProjection(playerId, defaultProjectionsSource, stats)
  }

}

object AggregatingProjectionsCachedFileBasedService {

  def main(args: Array[String]): Unit = {

    val projectionsLoaders = Seq(
      ProjectionsSource.KYLE -> getKyleProjectionsLoader(),
      ProjectionsSource.STEAMER -> getSteamerProjectionsLoader(),
      ProjectionsSource.ATC -> getATCProjectionsLoader())

    val projectionService = new AggregatingProjectionsCachedFileBasedService(projectionsLoaders)
    val res = projectionService.getPlayerProjections("19611")
    System.err.println(res)
  }

  private def getKyleProjectionsLoader(): ProjectionsDataLoader[Row, PlayerProjection] = {
    val (fileName, worksheetName) = ("C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx", "2020 projected")
    val positionsService = getPositionsService(fileName, worksheetName)
    val rowMapper = new ProjectionsDataRowMapperKyleRocBoiCo2020(positionsService)
    new ProjectionsDataFromExcelFileLoader(2020, fileName, worksheetName, rowMapper)
  }

  private def getSteamerProjectionsLoader(): ProjectionsDataLoader[String, PlayerProjection] = {
    val steamer2020FileName = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Steamer 2020 projections as of Jan 19 2020 - do not edit.csv"
    val rowMapper = new ProjectionsDataRowMapperStandard2020()
    new ProjectionsDataFromCSVFileLoader(2020, steamer2020FileName, rowMapper)
  }

  private def getATCProjectionsLoader(): ProjectionsDataLoader[String, PlayerProjection] = {
    val atc2020FileName = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\ATC 2020 projections as of Jan 25 2020 - do not edit.csv"
    val rowMapper = new ProjectionsDataRowMapperStandard2020()
    new ProjectionsDataFromCSVFileLoader(2020, atc2020FileName, rowMapper)
  }

  private def getPositionsService(fileName: String, worksheetName: String): PositionsService = {
    new SimplePositionsService(fileName, worksheetName)
  }

}