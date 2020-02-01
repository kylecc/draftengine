package draftengine.service.projections

import draftengine.model.player.PlayerProjection

trait ProjectionsService {

  def getPlayerProjections(playerId: String): Seq[PlayerProjection]
  def getAllPlayerProjections(sourceId: String): Seq[PlayerProjection]

}
