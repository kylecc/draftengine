package draftengine.drafter.loader

import draftengine.model.draft.DraftBoard

trait DraftLoader {

  def loadDraft(): DraftBoard

}
