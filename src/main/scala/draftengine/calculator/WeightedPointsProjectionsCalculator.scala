package draftengine.calculator

class WeightedPointsProjectionsCalculator(val playerId: String, val projections: Seq[(Double, Double)]) {

  require(projections.map { case (weight, _) => weight }.sum == 1, "Projection weights must add up to 100%!")

  def calculate(): Double = {
    projections.map { case (weight, pointsProjection) =>
      weight * pointsProjection
    }.sum
  }

}

object WeightedPointsProjectionsCalculator {

  def main(args: Array[String]): Unit = {
    new WeightedPointsProjectionsCalculator("1", Seq((0.5, 1), (0.5, 1)))
  }

}