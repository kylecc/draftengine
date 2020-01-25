package draftengine.model.player

import draftengine.model.statistics.Statistics

case class PlayerProjection(playerId: String, sourceId: String, statistics: Statistics)