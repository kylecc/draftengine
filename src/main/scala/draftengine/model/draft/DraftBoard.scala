package draftengine.model.draft

import draftengine.model.player.Player
import draftengine.service.PlayerService

import scala.collection.mutable

case class DraftBoard(draft: Draft, draftPicks: Array[DraftPick], playerService: PlayerService) {

  private val teamsByIdCache = draft.teams.map { team => team.id -> team }.toMap
  private val selectedPlayerIds = new mutable.HashSet[String]()
  private var lastPickIndex = draftPicks.indexWhere(_.playerId.isEmpty)

  def select(playerId: String): Unit = {
    draftPicks(lastPickIndex) = draftPicks(lastPickIndex).copy(playerId = Some(playerId))
    selectedPlayerIds += playerId
    lastPickIndex += 1
  }

  def undo(): Unit = {
    lastPickIndex -= 1
    val lastPlayerPicked = draftPicks(lastPickIndex)
    draftPicks(lastPickIndex) = lastPlayerPicked.copy(playerId = None)
    lastPlayerPicked.playerId.foreach(selectedPlayerIds -=)
  }

  def show(): Unit = {
    draftPicks
      .collect {
        case DraftPick(Some(id), team) => System.err.println(s"${getOwner(team)} selected player: ${getPlayer(id).name}")
        case DraftPick(None, team) => System.err.println(s"${getOwner(team)} has not selected yet")
      }
  }

  def getNumberOfDraftPicks: Int = draftPicks.length

  private def getPlayer(playerId: String): Player = playerService.getPlayer(playerId)

  private def getOwner(teamId: String): String = teamsByIdCache(teamId).owner

}