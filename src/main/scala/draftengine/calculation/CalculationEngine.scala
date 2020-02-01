package draftengine.calculation

trait CalculationEngine[T] {

  def calculate(): Seq[CalculationOutputComponent[T]]

}