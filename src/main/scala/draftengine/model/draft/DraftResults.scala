package draftengine.model.draft

case class DraftResults(id: String, draftId: String, selections: Seq[DraftPick])

case class DraftPick(playerId: Option[String], fantasyTeamId: String)