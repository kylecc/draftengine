package draftengine.data.loader.projections

import java.io.File

import draftengine.data.loader.projections.mapper.ProjectionsDataRowMapper
import org.apache.poi.ss.usermodel.{DataFormatter, Row, WorkbookFactory}

import scala.collection.JavaConverters._

class ProjectionsDataFromExcelFileLoader[U](
  val year: Int,
  val dataFileName: String,
  val worksheetName: String,
  val rowMapper: ProjectionsDataRowMapper[Row, U])
  extends ProjectionsDataLoader[Row, U] {

  override def load(): Seq[U] = {
    val workbook = WorkbookFactory.create(new File(dataFileName))
    val worksheet = workbook.getSheet(worksheetName)
    val rowIterator = worksheet.iterator().asScala
    val headerRow = rowIterator.next()
    val headerMapping = ProjectionsDataFromExcelFileLoader.getHeaderMapping(headerRow, rowMapper.dataFormatter)
    rowMapper.setHeaderMapping(headerMapping)
    val result = rowIterator.flatMap(rowMapper.convertRowToProjection).toSeq
    workbook.close()
    result
  }

}

object ProjectionsDataFromExcelFileLoader {

  def getHeaderMapping(headerRow: Row, dataFormatter: DataFormatter): Map[String, Int] = {
    headerRow.cellIterator()
      .asScala
      .zipWithIndex
      .map { case (cell, i) => dataFormatter.formatCellValue(cell) -> i }
      .toMap
  }

}