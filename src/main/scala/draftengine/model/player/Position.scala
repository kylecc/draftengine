package draftengine.model.player

object Position extends Enumeration {

  type Position = Value

  val StartingPitcher = Value("SP")
  val ReliefPitcher = Value("RP")
  val Catcher = Value("C")
  val FirstBase = Value("1B")
  val SecondBase = Value("2B")
  val ThirdBase = Value("3B")
  val Shortstop = Value("SS")
  val LeftField = Value("LF")
  val CenterField = Value("CF")
  val RightField = Value("RF")
  val Outfield = Value("OF")
  val designatedHitter = Value("DH")

  def isStartingPitcher(position: Value): Boolean = position match {
    case Position.StartingPitcher => true
    case _ => false
  }

  def isReliefPitcher(position: Value): Boolean = position match {
    case Position.ReliefPitcher => true
    case _ => false
  }

  def isCatcher(position: Value): Boolean = position match {
    case Position.Catcher => true
    case _ => false
  }

  def isFirstBase(position: Value): Boolean = position match {
    case Position.FirstBase => true
    case _ => false
  }

  def isSecondBase(position: Value): Boolean = position match {
    case Position.SecondBase => true
    case _ => false
  }

  def isThirdBase(position: Value): Boolean = position match {
    case Position.ThirdBase => true
    case _ => false
  }

  def isShortstop(position: Value): Boolean = position match {
    case Position.Shortstop => true
    case _ => false
  }

  def isLeftField(position: Value): Boolean = position match {
    case Position.LeftField => true
    case _ => false
  }

  def isCenterField(position: Value): Boolean = position match {
    case Position.CenterField => true
    case _ => false
  }

  def isRightField(position: Value): Boolean = position match {
    case Position.RightField => true
    case _ => false
  }

  def isInfield(position: Value): Boolean = position match {
    case Position.Catcher | Position.FirstBase | Position.SecondBase | Position.ThirdBase | Position.Shortstop => true
    case _ => false
  }

  def isOutfield(position: Value): Boolean = position match {
    case Position.LeftField | Position.CenterField | Position.RightField | Position.Outfield => true
    case _ => false
  }

  def isDesignatedHitter(position: Value): Boolean = position match {
    case Position.designatedHitter => true
    case _ => false
  }

  def isHitter(position: Value): Boolean = position match {
    case Position.StartingPitcher | Position.ReliefPitcher => false
    case _ => true
  }

  def apply(pos: String): Position.Value = pos match {
    case "C" => Catcher
    case "1B" => FirstBase
    case "2B" => SecondBase
    case "3B" => ThirdBase
    case "SS" => Shortstop
    case "LF" => LeftField
    case "CF" => CenterField
    case "RF" => RightField
    case "OF" => Outfield
    case "P" => StartingPitcher
    case "SP" => StartingPitcher
    case "RP" => ReliefPitcher
    case _ => null
  }

}
