package draftengine.data.loader.projections.mapper

import draftengine.model.player.PlayerProjection
import draftengine.model.statistics.{ProjectionsSource, SeasonStatistics, Statistic}
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row

import scala.util.Try

class ProjectionsDataRowMapperKyleRocBoiCo2020 extends ProjectionsDataFileBasedRowMapper[Row, PlayerProjection] {

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

  def getString(row: Row, index: Int): String = dataFormatter.formatCellValue(row.getCell(index))

  def getString(row: Row, colName: String): String = getString(row, getHeaderMapping(colName))

}
