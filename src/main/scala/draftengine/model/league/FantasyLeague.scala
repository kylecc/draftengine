package draftengine.model.league

case class FantasyLeague(
  fantasyLeagueId: String,
  leagueName: String,
  leagueCommissionerId: String,
  draftId: Option[String]) extends League