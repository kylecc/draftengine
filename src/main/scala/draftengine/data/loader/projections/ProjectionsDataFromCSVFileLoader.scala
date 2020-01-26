package draftengine.data.loader.projections

import draftengine.data.loader.ProjectionsDataLoader
import draftengine.data.loader.projections.mapper.{ProjectionsDataRowMapper, ProjectionsDataRowMapperSteamer2020}

import scala.io.Source

class ProjectionsDataFromCSVFileLoader[T <: String, U](
  val year: Int,
  val dataFileName: String,
  val rowMapper: ProjectionsDataRowMapper[String, U])
  extends ProjectionsDataLoader[String, U] {

  override def load(): Seq[U] = {
    val source = Source.fromFile(dataFileName)
    val rowIterator = source.getLines()
    val headerRow = rowIterator.next()
    val headerMapping = ProjectionsDataFromCSVFileLoader.getHeaderMapping(headerRow)
    rowMapper.setHeaderMapping(headerMapping)
    val result = rowIterator.flatMap(rowMapper.convertRowToProjection).toList
    source.close()
    result
  }

}

object ProjectionsDataFromCSVFileLoader {

  def getHeaderMapping(headerRow: String): Map[String, Int] = {
    headerRow.split(",")
      .zipWithIndex
      .map { case (colName, i) => colName -> i }
      .toMap
  }

  def main(args: Array[String]): Unit = {
    val steamer2020FileName = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Steamer 2020 projections as of Jan 19 2020 - do not edit.csv"
    val rowMapper = new ProjectionsDataRowMapperSteamer2020()
    val projections = new ProjectionsDataFromCSVFileLoader(2020, steamer2020FileName, rowMapper).load()
    projections.foreach(System.err.println)
  }

}