package draftengine.service

import draftengine.drafter.loader.DraftFileBasedLoader
import draftengine.drafter.writer.DraftFileBasedWriter
import draftengine.model.draft.DraftBoard

trait DraftService {

  def loadDraft(draftId: String): DraftBoard

  def saveDraft(draftBoard: DraftBoard): Unit

}

class DraftFileBasedService(val playerService: PlayerService, val draftFileDirectory: String) extends DraftService {

  override def loadDraft(draftId: String): DraftBoard = {
    new DraftFileBasedLoader(draftId, playerService, draftFileDirectory).loadDraft()
  }

  override def saveDraft(draftBoard: DraftBoard): Unit = {
    new DraftFileBasedWriter(draftBoard.draft.id, draftBoard, draftFileDirectory).writeDraft()
  }

}
