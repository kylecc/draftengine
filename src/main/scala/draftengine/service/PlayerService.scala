package draftengine.service

import draftengine.data.loader.FileBasedDataLoader
import draftengine.model.player
import draftengine.model.player.{Player, PlayerProjection, PositionSet}

trait PlayerService {

  def getPlayer(playerId: String): Player

  def getPlayerByName(name: String): Option[Player]

}

class SimpleCachedPlayerService extends PlayerService {

  private lazy val (playerCacheById, playerCacheByName) = loadPlayerCache()

  override def getPlayer(playerId: String): Player = {
    playerCacheById.getOrElse(playerId, null)
  }

  override def getPlayerByName(name: String): Option[Player] = {
    val searchKey = SimpleCachedPlayerService.cleanseNameString(name)
    playerCacheByName.get(searchKey) match {
      case Some(players) if players.size > 1 => { System.err.println("Too many matching players"); None }
      case Some(players) => players.headOption
      case _ => None
    }
  }

  private def loadPlayerCache(): (Map[String, Player], Map[String, Seq[Player]]) = {
    val playerData = new FileBasedDataLoader().load2020ProjectionsSpreadsheet()
    val playerCacheById = SimpleCachedPlayerService.buildPlayerCacheById(playerData)
    val playerCacheByName = SimpleCachedPlayerService.buildPlayerCacheByName(playerData, playerCacheById)
    (playerCacheById, playerCacheByName)
  }

}

object SimpleCachedPlayerService {

  def main(args: Array[String]): Unit = {
    System.err.println(new SimpleCachedPlayerService().getPlayer("8700"))
  }

  def buildPlayerCacheById(playerData: Seq[(String, PlayerProjection, PositionSet)]): Map[String, Player] = {
    playerData.map {
      case (name, projection, positionSet) => projection.playerId -> player.Player(projection.playerId, name, positionSet)
    }.toMap
  }

  def buildPlayerCacheByName(
    playerData: Seq[(String, PlayerProjection, PositionSet)],
    playerCacheById: Map[String, Player]): Map[String, Seq[Player]] = {

    playerData.map {
      case (name, projection, _) => {
        val cleansedName = SimpleCachedPlayerService.cleanseNameString(name).toLowerCase
        cleansedName -> playerCacheById(projection.playerId)
      }
    }.groupBy {
      case (name, _) => name
    }.map {
      case (name, playersAndName) => name -> playersAndName.map {
        case (_, player) => player
      }
    }
  }

  def cleanseNameString(name: String): String = {
    name.toSeq.filter(c => Character.isAlphabetic(c) || Character.isSpaceChar(c)).toString().trim
  }

}