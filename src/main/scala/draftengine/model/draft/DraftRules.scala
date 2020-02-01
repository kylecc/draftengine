package draftengine.model.draft

trait DraftRules {

  def getDraftPicks(): Seq[DraftPick]

}

case class SnakeDraftRules(draft: Draft) extends DraftRules {

  override def getDraftPicks(): Seq[DraftPick] = {
    val evens = draft.teams
    val odds = draft.teams.reverse
    (0 until draft.playersPerTeam).flatMap {
      _ % 2 match {
        case 0 => DraftRules.createDraftPicks(evens)
        case _ => DraftRules.createDraftPicks(odds)
      }
    }
  }

}

case class StandardDraftRules(draft: Draft) extends DraftRules {

  override def getDraftPicks(): Seq[DraftPick] = {
    (0 until draft.playersPerTeam).flatMap(_ => DraftRules.createDraftPicks(draft.teams))
  }

}

object DraftRules {

  private[model] def createDraftPicks(teams: Seq[FantasyTeam]) = teams.map(t => DraftPick(None, t.id))

}