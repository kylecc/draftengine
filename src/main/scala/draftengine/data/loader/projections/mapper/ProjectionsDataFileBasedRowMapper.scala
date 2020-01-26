package draftengine.data.loader.projections.mapper

abstract class ProjectionsDataFileBasedRowMapper[T, U](
  var headerMapping: Map[String, Int] = Map.empty)
  extends ProjectionsDataRowMapper[T, U] {

  override def setHeaderMapping(headerMapping: Map[String, Int]): Unit = {
    this.headerMapping = headerMapping
  }

  override def getHeaderMapping: Map[String, Int] = headerMapping

}