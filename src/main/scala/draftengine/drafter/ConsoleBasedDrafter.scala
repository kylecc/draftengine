package draftengine.drafter

import draftengine.model.draft.DraftBoard
import draftengine.model.player.Player
import draftengine.service.{CachedPlayerService, DraftFileBasedService}

import scala.io.StdIn.readLine

object ConsoleBasedDrafter {

  private val playerService = new CachedPlayerService()
  private val draftService = new DraftFileBasedService(playerService, ".")

  def main(args: Array[String]): Unit = {
    draft()
  }

  private def draft(): Unit = {
    printHello()
    val draftId = getUserInput()
    val draftBoard = loadDraft(draftId)
    System.err.println(s"Draft board: $draftBoard")
    System.err.println("Select a player:")
    val selectedPlayer = parsePlayerInput(getUserInput())
    selectedPlayer.foreach(player => draftBoard.select(player.id))
    draftService.saveDraft(draftBoard)
  }

  private def loadDraft(draftId: String): DraftBoard = {
    draftService.loadDraft(draftId)
  }

  private def getUserInput(): String = readLine()

  private def parsePlayerInput(input: String): Option[Player] = input match {
    case id if isNumericId(id) => Some(playerService.getPlayer(id))
    case playerName => playerService.getPlayerByName(playerName)
  }

  private def isNumericId(id: String): Boolean = id.lastOption.exists(Character.isDigit)

  private def printHello(): Unit = {
    println("Welcome to DraftEngine 0.1!")
    println("Please enter a draft ID.")
  }

}
