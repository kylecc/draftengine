package draftengine.drafter.writer

import java.io.PrintWriter

import draftengine.drafter.loader.DraftFileBasedLoader
import draftengine.model.draft.DraftBoard
import draftengine.service.SimpleCachedPlayerService

class DraftFileBasedWriter(
  val draftId: String,
  val draftBoard: DraftBoard,
  val draftFileDirectory: String) extends DraftWriter {

  lazy val writer = new PrintWriter(constructDraftDataFileName)

  override def writeDraft(): Unit = {
    writeDraftBoardDataToFile()
  }

  private def writeDraftBoardDataToFile(): Unit = {
    writeDraftId()
    writeDraftName()
    writeNumberOfTeams()
    writeNumberOfPlayersPerTeam()
    writeTeamDetails()
    writeDraftPicks()
    writer.close()
  }

  private def writeDraftId(): Unit = {
    writer.println(draftId)
  }

  private def writeDraftName(): Unit = {
    writer.println(draftBoard.draft.name)
  }

  private def writeNumberOfTeams(): Unit = {
    writer.println(draftBoard.draft.teams.size)
  }

  private def writeNumberOfPlayersPerTeam(): Unit = {
    writer.println(draftBoard.draft.playersPerTeam)
  }

  private def writeTeamDetails(): Unit = {
    draftBoard.draft.teams.foreach { team =>
      val teamDetailStr = Seq(team.id, team.name, team.owner).mkString(";")
      writer.println(teamDetailStr)
    }
  }

  private def writeDraftPicks(): Unit = {
    draftBoard.draftPicks.foreach { draftPick =>
      val draftPickStr = Seq(draftPick.playerId.getOrElse(""), draftPick.fantasyTeamId).mkString(",")
      writer.println(draftPickStr)
    }
  }

  private def constructDraftDataFileName: String = {
    s"$draftFileDirectory/draft$draftId.txt"
  }

}

object DraftFileBasedWriter {

  def main(args: Array[String]): Unit = {
    val draftFileDirectory = "."
    val draftBoard1 = new DraftFileBasedLoader("2", new SimpleCachedPlayerService(), draftFileDirectory).loadDraft()
    val draftBoard2 = draftBoard1.copy(draft = draftBoard1.draft.copy(id = "3"))
    new DraftFileBasedWriter("3", draftBoard2, draftFileDirectory).writeDraft()
  }

}