package draftengine.data.loader

import java.io.File

import draftengine.model.player.{PlayerProjection, PositionSet}
import org.apache.commons.lang3.StringUtils
import org.apache.poi.ss.usermodel.{DataFormatter, Row, WorkbookFactory}

import scala.collection.JavaConverters._
import scala.util.Try

@deprecated("move to more generic API", "")
class FileBasedDataLoader {

  private val spreadsheetFile = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx"
  private val dataFormatter = new DataFormatter()

  def main(args: Array[String]): Unit = {
    load2020Projections()
  }

  def load2020ProjectionsSpreadsheet(): Seq[(String, PlayerProjection, PositionSet)] = {
    val workbook = WorkbookFactory.create(new File(spreadsheetFile))
    val worksheet = workbook.getSheet("2020 projected")
    val data = worksheet.iterator().asScala.drop(1).collect(mapRowToObject).toSeq
    workbook.close()
    data
  }

  private def load2020Projections(): Seq[(String, PlayerProjection, PositionSet)] = {
    val playerProjections = load2020ProjectionsSpreadsheet()
    playerProjections.foreach(System.err.println)
    playerProjections
  }

  private val mapRowToObject: PartialFunction[Row, (String, PlayerProjection, PositionSet)] = {
    case row if !StringUtils.isEmpty(getString(row, 0)) => convertRowToObject(row)
  }

  private def convertRowToObject(row: Row): (String, PlayerProjection, PositionSet) = {
    val playerName = getString(row, 0)
    val playerId = getString(row, 2)
    val positionSet = PositionSet(getString(row, 1))
    val projectedPoints = Try(getString(row, 4).toInt).getOrElse(0)
    val playerProjection = PlayerProjection(playerId, null, null)
    (playerName, playerProjection, positionSet)
  }

  def getString(row: Row, index: Int): String = {
    dataFormatter.formatCellValue(row.getCell(index))
  }

}

object FileBasedDataLoader {

  def main(args: Array[String]): Unit = {
    new FileBasedDataLoader().load2020Projections()
  }

}