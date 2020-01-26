package draftengine.model.player

import draftengine.model.statistics.{SeasonStatistics, Statistics}

case class PlayerProjection(playerId: String, sourceId: String, statistics: Statistics)

object PlayerProjection {

  def empty(playerId: String): PlayerProjection = {
    val stats = SeasonStatistics(0, Seq.empty)
    PlayerProjection(playerId, null, stats)
  }

}