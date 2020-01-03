package draftengine.drafter

import draftengine.data.loader.DataCreator
import draftengine.model.draft.{Draft, DraftBoard, StandardDraftRules}
import draftengine.service.CachedPlayerService

object SimpleDrafter {

  def main(args: Array[String]): Unit = {
    draft()
  }

  private def draft(): Unit = {

    val teams = DataCreator.createDummyTeams1()
    val draft = Draft("1", "Draft 1", teams, 10)
    val draftPicks = StandardDraftRules(draft).getDraftPicks()
    val draftBoard = DraftBoard(draft, draftPicks.toArray, new CachedPlayerService())
    draftBoard.select("8700")
    draftBoard.select("3137")
    draftBoard.select("10155")
    draftBoard.undo()
    draftBoard.select("10954")
    draftBoard.show()

  }

}
