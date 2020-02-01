package draftengine.calculation

case class CalculationInputComponent[T](playerId: String, inputStatistics: Map[String, T])
