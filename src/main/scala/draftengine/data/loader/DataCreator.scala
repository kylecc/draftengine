package draftengine.data.loader

import draftengine.model.draft.{Draft, DraftPick, DraftResults, FantasyTeam}
import draftengine.model.player.{Player, PlayerProjection, PositionSet}

object DataCreator {

  def createDummyDraft1(): Draft = {
    val teams = createDummyTeams1()
    Draft("draft1", "First Dummy Draft", teams, 3)
  }

  def createDummyTeams1(): Seq[FantasyTeam] = {
    Seq(
      FantasyTeam("1", "Team 1", "Owner 1"),
      FantasyTeam("2", "Team 2", "Owner 2"),
      FantasyTeam("3", "Team 3", "Owner 3")
    )
  }

  def createDummyDraftResults1(): DraftResults = {
    val selections = Seq(
      DraftPick(Some("1"), "1"),
      DraftPick(Some("2"), "2"),
      DraftPick(Some("3"), "3"),
      DraftPick(Some("4"), "3"),
      DraftPick(Some("5"), "2"),
      DraftPick(Some("6"), "1"),
      DraftPick(Some("7"), "1"),
      DraftPick(Some("8"), "2"),
      DraftPick(Some("9"), "3")
    )
    DraftResults("1", "draft1", selections)
  }

  def createPlayers1(): Seq[Player] = {

    import draftengine.model.player.Position._

    Seq(
      Player("1", "Player 1", PositionSet(StartingPitcher)),
      Player("2", "Player 2", PositionSet(SecondBase)),
      Player("3", "Player 3", PositionSet(Shortstop)),
      Player("4", "Player 4", PositionSet(StartingPitcher)),
      Player("5", "Player 5", PositionSet(LeftField)),
      Player("6", "Player 6", PositionSet(CenterField)),
      Player("7", "Player 7", PositionSet(StartingPitcher)),
      Player("8", "Player 8", PositionSet(StartingPitcher)),
      Player("9", "Player 9", PositionSet(FirstBase)),
      Player("10", "Player 10", PositionSet(StartingPitcher)),
      Player("11", "Player 11", PositionSet(FirstBase)),
      Player("12", "Player 12", PositionSet(SecondBase)),
      Player("13", "Player 13", PositionSet(LeftField, RightField)),
      Player("14", "Player 14", PositionSet(StartingPitcher)),
      Player("15", "Player 15", PositionSet(StartingPitcher)),
      Player("16", "Player 16", PositionSet(CenterField)),
      Player("17", "Player 17", PositionSet(ThirdBase)),
      Player("18", "Player 18", PositionSet(CenterField)),
      Player("19", "Player 19", PositionSet(StartingPitcher)),
      Player("20", "Player 20", PositionSet(FirstBase, RightField)),
      Player("21", "Player 21", PositionSet(StartingPitcher)),
      Player("22", "Player 22", PositionSet(Shortstop, ThirdBase)),
      Player("23", "Player 23", PositionSet(LeftField)),
      Player("24", "Player 24", PositionSet(StartingPitcher)),
      Player("25", "Player 25", PositionSet(LeftField))
    )

  }

  def createPlayerProjections1(): Seq[PlayerProjection] = {
    Seq(
      PlayerProjection("1", 500),
      PlayerProjection("2", 501),
      PlayerProjection("3", 490),
      PlayerProjection("4", 498),
      PlayerProjection("5", 495),
      PlayerProjection("6", 494),
      PlayerProjection("7", 502),
      PlayerProjection("8", 493),
      PlayerProjection("9", 492),
      PlayerProjection("10", 485),
      PlayerProjection("11", 487),
      PlayerProjection("12", 492),
      PlayerProjection("13", 484),
      PlayerProjection("14", 485),
      PlayerProjection("15", 475),
      PlayerProjection("16", 474),
      PlayerProjection("17", 473),
      PlayerProjection("18", 452),
      PlayerProjection("19", 469),
      PlayerProjection("20", 479),
      PlayerProjection("21", 478),
      PlayerProjection("22", 499),
      PlayerProjection("23", 498),
      PlayerProjection("24", 372),
      PlayerProjection("25", 461)
    )
  }

}
