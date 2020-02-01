package draftengine.calculation

abstract class Calculation[T](
  val outputStatisticId: String,
  val outputCalculator: CalculationInputComponent[T] => T)

case class PointsLeagueCalculation(
  override val outputStatisticId: String,
  override val outputCalculator: CalculationInputComponent[Double] => Double)
  extends Calculation[Double](outputStatisticId, outputCalculator)