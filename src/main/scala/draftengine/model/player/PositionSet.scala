package draftengine.model.player

import draftengine.model.player.Position.Position

case class PositionSet(positions: Set[Position]) {

  def isPitcher: Boolean = {
    positions.exists(p => Position.isStartingPitcher(p) || Position.isReliefPitcher(p))
  }

  def isHitter: Boolean = {
    positions.exists(Position.isHitter)
  }

  def isCatcher: Boolean = positions.exists(Position.isCatcher)

  def isFirstBase: Boolean = positions.exists(Position.isFirstBase)

  def isSecondBase: Boolean = positions.exists(Position.isSecondBase)

  def isThirdBase: Boolean = positions.exists(Position.isThirdBase)

  def isShortstop: Boolean = positions.exists(Position.isShortstop)

  def isLeftField: Boolean = positions.exists(Position.isLeftField)

  def isCenterField: Boolean = positions.exists(Position.isCenterField)

  def isRightField: Boolean = positions.exists(Position.isRightField)

  def isMiddleInfield: Boolean = {
    positions.exists(p => Position.isSecondBase(p) || Position.isShortstop(p))
  }

  def isCornerInfield: Boolean = {
    positions.exists(p => Position.isFirstBase(p) || Position.isThirdBase(p))
  }

  def isInfield: Boolean = positions.exists(Position.isInfield)

  def isOutfield: Boolean = positions.exists(Position.isOutfield)

  def isEmpty: Boolean = positions.isEmpty

}

object PositionSet {

  def apply(pos: Position.Value*): PositionSet = PositionSet(pos.toSet)

  def apply(pos: String): PositionSet = PositionSet(pos.split(" ").map(Position.apply).toSet)

  def cornerInfield: PositionSet = PositionSet(Position.FirstBase, Position.ThirdBase)

  def middleInfield: PositionSet = PositionSet(Position.SecondBase, Position.Shortstop)

  def empty = PositionSet(Set.empty[Position])

}