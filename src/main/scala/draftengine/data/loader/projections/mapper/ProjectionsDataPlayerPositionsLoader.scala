package draftengine.data.loader.projections.mapper

import draftengine.model.player.{PlayerPositionSet, PositionSet}
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.Row

class ProjectionsDataPlayerPositionsLoader extends ProjectionsDataFileBasedRowMapper[Row, PlayerPositionSet] {

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

  def getString(row: Row, index: Int): String = dataFormatter.formatCellValue(row.getCell(index))

  def getString(row: Row, colName: String): String = getString(row, getHeaderMapping(colName))

}
