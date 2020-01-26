package draftengine.data.loader

import draftengine.data.loader.projections.ProjectionsDataFromExcelFileLoader
import draftengine.data.loader.projections.mapper.ProjectionsDataRowMapperKyleRocBoiCo2020

trait ProjectionsDataLoader[T, U] {

  def load(): Seq[U]

}

object ProjectionsDataLoaderRunner {

  def main(args: Array[String]): Unit = {
    val rbc2020FileName = ("C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Spreadsheet.xlsx", "2020 projected")
    val (file, worksheet) = rbc2020FileName
    val rowMapper = new ProjectionsDataRowMapperKyleRocBoiCo2020()
    val projections = new ProjectionsDataFromExcelFileLoader(2020, file, worksheet, rowMapper).load()
    projections.foreach(System.err.println)
  }

}