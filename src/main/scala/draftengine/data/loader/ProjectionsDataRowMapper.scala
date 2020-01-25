package draftengine.data.loader

import draftengine.model.player.{PlayerPositionSet, PlayerProjection, PositionSet}
import draftengine.model.statistics.{ProjectionsSource, SeasonStatistics, Statistic}
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.{DataFormatter, Row}

import scala.util.Try

trait ProjectionsDataRowMapper[T] {

  val dataFormatter = new DataFormatter()

  def convertRowToProjection(row: Row): Option[T]

  def setHeaderMapping(headerMapping: Map[String, Int]): Unit

  def getHeaderMapping: Map[String, Int] = Map.empty

  def getString(row: Row, index: Int): String = dataFormatter.formatCellValue(row.getCell(index))

  def getString(row: Row, colName: String): String = getString(row, getHeaderMapping(colName))

}

abstract class ProjectionsDataFileBasedRowMapper[T](
  var headerMapping: Map[String, Int] = Map.empty)
  extends ProjectionsDataRowMapper[T] {

  override def setHeaderMapping(headerMapping: Map[String, Int]): Unit = {
    this.headerMapping = headerMapping
  }

  override def getHeaderMapping: Map[String, Int] = headerMapping

}

class ProjectionsDataRowMapperKyleRocBoiCo2020 extends ProjectionsDataFileBasedRowMapper[PlayerProjection] {

  private val year = 2020
  private val playerIdColumnName = "playerid"
  private val rbc2020ProjectedPointsColumnName = "rbc2020-proj"
  private val projectionsSource = ProjectionsSource.KYLE

  override def convertRowToProjection(row: Row): Option[PlayerProjection] = {
    if (StringUtils.isEmpty(getString(row, 0))) {
      None
    } else {
      val playerId = getString(row, playerIdColumnName)
      val projectedPoints = Try(getString(row, rbc2020ProjectedPointsColumnName).toInt).getOrElse(0)
      val rocBoiCoProjection = Statistic(Statistic.ROC_BOI_CO_POINTS, projectedPoints)
      val projection = SeasonStatistics(year, Seq(rocBoiCoProjection))
      Some(PlayerProjection(playerId, projectionsSource, projection))
    }
  }

}

class ProjectionsDataPlayerPositionsLoader extends ProjectionsDataFileBasedRowMapper[PlayerPositionSet] {

  private val playerIdColumnName = "playerid"
  private val positionSetColumnName = "pos"

  override def convertRowToProjection(row: Row): Option[PlayerPositionSet] = {
    if (StringUtils.isEmpty(getString(row, 0))) {
      None
    } else {
      val playerId = getString(row, playerIdColumnName)
      val positionSetString = getString(row, positionSetColumnName)
      val positionSet = PositionSet(positionSetString)
      Some(PlayerPositionSet(playerId, positionSet))
    }
  }

}