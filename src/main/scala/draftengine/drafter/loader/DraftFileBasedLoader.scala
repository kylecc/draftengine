package draftengine.drafter.loader

import draftengine.model.draft.{Draft, DraftBoard, DraftPick, FantasyTeam}
import draftengine.service.{CachedPlayerService, PlayerService}

import scala.io.Source

class DraftFileBasedLoader(
  val draftId: String,
  val playerService: PlayerService,
  val draftFileDirectory: String) extends DraftLoader {

  override def loadDraft(): DraftBoard = {
    loadDraftFromDataFile()
  }

  private def loadDraftFromDataFile(): DraftBoard = {
    usingSourceFile(constructDraftDataFileName) { fileIterator =>
      val draft = createDraft(fileIterator)
      val draftPicks = createDraftPicks(fileIterator, draft.teams.size, draft.playersPerTeam)
      DraftBoard(draft, draftPicks.toArray, playerService)
    }
  }

  private def usingSourceFile[T](fileName: String)(f: Iterator[String] => T): T = {
    val source = Source.fromFile(fileName)
    val result = f(source.getLines())
    source.close()
    result
  }

  private def createDraft(fileIterator: Iterator[String]): Draft = {
    val fileDraftId = fileIterator.next()
    require(fileDraftId == draftId)
    val (draftName, numberOfTeams, playersPerTeam) = getDraftDetails(fileIterator)
    val fantasyTeams = createFantasyTeams(fileIterator, numberOfTeams)
    Draft(draftId, draftName, fantasyTeams, playersPerTeam)
  }

  private def getDraftDetails(fileIterator: Iterator[String]): (String, Int, Int) = {
    val draftName = fileIterator.next()
    val numberOfTeams = fileIterator.next().toInt
    val playersPerTeam = fileIterator.next().toInt
    (draftName, numberOfTeams, playersPerTeam)
  }

  private def createFantasyTeams(fileIterator: Iterator[String], numberOfTeams: Int): Seq[FantasyTeam] = {
    (1 to numberOfTeams).map { _ =>
      val arr = fileIterator.next().split(";")
      val (teamId, teamName, teamOwner) = (arr(0), arr(1), arr(2))
      FantasyTeam(teamId, teamName, teamOwner)
    }
  }

  private def createDraftPicks(fileIterator: Iterator[String], numTeams: Int, playersPerTeam: Int): Seq[DraftPick] = {
    (1 to numTeams).flatMap { _ =>
      (1 to playersPerTeam).map { _ =>
        fileIterator.next().split(",") match {
          case Array(playerId, teamId) if playerId.nonEmpty => DraftPick(Some(playerId), teamId)
          case Array(_, teamId) => DraftPick(None, teamId)
          case _ => throw new Exception("Invalid draft data file format")
        }
      }
    }
  }

  private def constructDraftDataFileName: String = {
    s"$draftFileDirectory/draft$draftId.txt"
  }

}

object DraftFileBasedLoader {

  def main(args: Array[String]): Unit = {
    new DraftFileBasedLoader("1", new CachedPlayerService(), ".").loadDraft().show()
  }

}