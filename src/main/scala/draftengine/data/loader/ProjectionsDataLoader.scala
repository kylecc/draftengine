package draftengine.data.loader

import java.io.File

import org.apache.poi.ss.usermodel.{DataFormatter, Row, WorkbookFactory}

import scala.collection.JavaConverters._

trait ProjectionsDataLoader[T] {

  def load(): Seq[T]

}

class ProjectionsDataFromExcelFileLoader[T](
  val year: Int,
  val dataFileName: String,
  val worksheetName: String,
  val rowMapper: ProjectionsDataRowMapper[T])
  extends ProjectionsDataLoader[T] {

  override def load(): Seq[T] = {
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

object ProjectionsDataLoaderRunner {

  private val rbc2020FileName = ("C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx", "2020 projected")

  def main(args: Array[String]): Unit = {
    val (file, worksheet) = rbc2020FileName
    val rowMapper = new ProjectionsDataRowMapperKyleRocBoiCo2020()
    val projections = new ProjectionsDataFromExcelFileLoader(2020, file, worksheet, rowMapper).load()
    projections.foreach(System.err.println)
  }

}