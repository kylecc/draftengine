package draftengine.service.positions

import draftengine.model.player.PositionSet

trait PositionsService {

  def getPositionsForPlayer(playerId: String): PositionSet
  def getPositionsForAllPlayers(leagueId: String): Map[String, PositionSet]

}
