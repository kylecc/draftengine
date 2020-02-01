package draftengine.calculation

abstract class Calculation[T](
  val outputStatisticId: String,
  val outputCalculator: CalculationInputComponent[T] => T)