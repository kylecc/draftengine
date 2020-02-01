package draftengine.calculation

case class CalculationOutputComponent[T](playerId: String, statistics: Map[String, T])
