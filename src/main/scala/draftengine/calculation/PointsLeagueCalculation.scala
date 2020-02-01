package draftengine.calculation

case class PointsLeagueCalculation(
  override val outputStatisticId: String,
  override val outputCalculator: CalculationInputComponent[Double] => Double)
  extends Calculation[Double](outputStatisticId, outputCalculator)