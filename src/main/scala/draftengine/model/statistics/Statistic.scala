package draftengine.model.statistics

case class Statistic(statisticId: String, value: Double)

object Statistic {

  val GamesPlayed = "0"

  val Singles = "1"
  val Doubles = "2"
  val Triples = "3"
  val HomeRuns = "4"
  val RunsScored = "5"
  val RunsBattedIn = "6"
  val StolenBases = "7"
  val CaughtStealing = "8"
  val WalksHitter = "9"
  val StrikeoutsHitter = "10"
  val AtBats = "11"
  val PlateAppearances = "12"
  val HitByPitch = "13"
  val SacrificeFly = "14"
  val SacrificeBunt = "15"

  val GamesStarted = "16"
  val InningsPitched = "17"
  val Wins = "18"
  val Losses = "19"
  val CompleteGames = "20"
  val Shutouts = "21"
  val Saves = "22"
  val Holds = "23"
  val BlownSaves = "24"
  val HitsAllowed = "25"
  val RunsAllowed = "26"
  val EarnedRunsAllowed = "27"
  val HomeRunsAllowed = "28"
  val WalksAllowed = "29"
  val IntentionalWalksAllowed = "30"
  val StrikeoutsPitcher = "31"
  val NoHitters = "32"
  val PerfectGames = "33"
  val QualityStarts = "34"

  val RocBoiCoPoints = "200"
  val RocBoiCoPointsPerGame = "201"

  val Composite = "1000"

}