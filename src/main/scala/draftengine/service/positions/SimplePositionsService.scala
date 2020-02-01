package draftengine.service.positions

import draftengine.data.analyzer.TopProjectedPlayersAtEachPositionAnalyzer
import draftengine.data.loader.projections.ProjectionsDataFromExcelFileLoader
import draftengine.data.loader.projections.mapper.ProjectionsDataPlayerPositionsLoader
import draftengine.model.player.{PlayerPositionSet, PositionSet}

class SimplePositionsService(
  val positionsFileName: String,
  val worksheetName: String)
  extends PositionsService {

  private val positionSetCache = buildPositionSetCache()

  override def getPositionsForPlayer(playerId: String): PositionSet = {
    positionSetCache.get(playerId).getOrElse(PositionSet.empty)
  }

  override def getPositionsForAllPlayers(leagueId: String): Map[String, PositionSet] = positionSetCache

  private def buildPositionSetCache(): Map[String, PositionSet] = {
    val rowMapper = new ProjectionsDataPlayerPositionsLoader()
    val positions = new ProjectionsDataFromExcelFileLoader(2020, positionsFileName, worksheetName, rowMapper).load()
    positions.map { case PlayerPositionSet(playerId, posSet) => playerId -> posSet }.toMap
  }

}

object SimplePositionsService {

  def main(args: Array[String]): Unit = {
    getPositionsForRandomPlayer()
    groupPlayersByPosition()
  }

  private def getPositionsForRandomPlayer(): Unit = {
    val positionsFileName = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx"
    val worksheetName = "2020 projected"
    System.err.println(new SimplePositionsService(positionsFileName, worksheetName).getPositionsForPlayer("17678"))
  }

  private def groupPlayersByPosition(): Unit = {
    val positionsFileName = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx"
    val worksheetName = "2020 projected"
    val positionsService = new SimplePositionsService(positionsFileName, worksheetName)
    val leagueId = ""
    val allMappedPlayersAndPositions = positionsService.getPositionsForAllPlayers(leagueId).collect {
      case entry @ (_, positionSet) if !positionSet.isEmpty => entry
    }
    val positionFilters = TopProjectedPlayersAtEachPositionAnalyzer.standardPositions
    positionFilters.map { case (positionFilter, description) =>
      description -> allMappedPlayersAndPositions.filter { case (_, positionSet) =>
        positionFilter(positionSet)
      }.size
    }.foreach(System.err.println)
  }

}