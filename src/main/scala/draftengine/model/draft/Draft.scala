package draftengine.model.draft

case class Draft(id: String, name: String, teams: Seq[FantasyTeam], playersPerTeam: Int)
