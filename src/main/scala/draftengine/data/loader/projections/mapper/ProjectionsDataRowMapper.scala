package draftengine.data.loader.projections.mapper

import org.apache.poi.ss.usermodel.DataFormatter

trait ProjectionsDataRowMapper[T, U] {

  val dataFormatter = new DataFormatter()

  def convertRowToProjection(row: T): Option[U]

  def setHeaderMapping(headerMapping: Map[String, Int]): Unit

  def getHeaderMapping: Map[String, Int] = Map.empty

}
