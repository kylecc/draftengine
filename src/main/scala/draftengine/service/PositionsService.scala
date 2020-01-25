package draftengine.service

import draftengine.data.loader.{ProjectionsDataFromExcelFileLoader, ProjectionsDataPlayerPositionsLoader}
import draftengine.model.player.{PlayerPositionSet, PositionSet}

trait PositionsService {

  def getPositionsForPlayer(playerId: String): PositionSet

}

class SimplePositionsService(
  val positionsFileName: String,
  val worksheetName: String)
  extends PositionsService {

  private val positionSetCache = buildPositionSetCache()

  override def getPositionsForPlayer(playerId: String): PositionSet = {
    positionSetCache.get(playerId).getOrElse(PositionSet.empty)
  }

  private def buildPositionSetCache(): Map[String, PositionSet] = {
    val rowMapper = new ProjectionsDataPlayerPositionsLoader()
    val positions = new ProjectionsDataFromExcelFileLoader(2020, positionsFileName, worksheetName, rowMapper).load()
    positions.map { case PlayerPositionSet(playerId, posSet) => playerId -> posSet }.toMap
  }

}

object SimplePositionsService {

  def main(args: Array[String]): Unit = {
    val positionsFileName = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx"
    val worksheetName = "2020 projected"
    System.err.println(new SimplePositionsService(positionsFileName, worksheetName).getPositionsForPlayer("17678"))
  }

}