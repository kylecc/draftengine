package draftengine.data.loader.projections.mapper

import draftengine.data.loader.projections.ProjectionsDataFromCSVFileLoader
import draftengine.model.player.PlayerProjection
import draftengine.model.statistics.{ProjectionsSource, SeasonStatistics, Statistic, Statistics}
import org.apache.commons.lang3.StringUtils

class ProjectionsDataRowMapperSteamer2020 extends ProjectionsDataFileBasedRowMapper[String, PlayerProjection] {

  private val year = 2020
  private val playerIdColumnName = "playerid"
  private val projectionsSource = ProjectionsSource.STEAMER

  override def convertRowToProjection(row: String): Option[PlayerProjection] = {
    if (StringUtils.isEmpty(getString(row, 0))) {
      None
    } else {
      val playerId = getString(row, playerIdColumnName)
      val statistics = getPlayerStatistics(row)
      Some(PlayerProjection(playerId, projectionsSource, statistics))
    }
  }

  private def getPlayerStatistics(row: String): Statistics = {
    val doubles = getString(row, "2B").toInt
    val triples = getString(row, "3B").toInt
    val hits = getString(row, "H").toInt
    val singles = hits - doubles - triples
    val homeRuns = getString(row, "HR").toInt
    val runs = getString(row, "R").toInt
    val rbi = getString(row, "RBI").toInt
    val bb = getString(row, "BB").toInt
    val strikeouts = getString(row, "SO").toInt
    val stolenBases = getString(row, "SB").toInt
    val rbcPoints = singles + (2 * doubles) + (3 * triples) + (5 * homeRuns) + runs + rbi + bb - strikeouts + (2 * stolenBases)
    val games = getString(row, "G").toInt
    val rbcPointsPerGame = rbcPoints.toDouble / games
    val stats = Seq(
      Statistic(Statistic.GAMES_PLAYED, games),
      Statistic(Statistic.ROC_BOI_CO_POINTS, rbcPoints),
      Statistic(Statistic.ROC_BOI_CO_PER_GAME, rbcPointsPerGame))
    SeasonStatistics(year, stats)
  }

  def getString(row: String, index: Int): String = row.split(",")(index)

  def getString(row: String, colName: String): String = row.split(",")(getHeaderMapping(colName))

}

object ProjectionsDataRowMapperSteamer2020 {

  def main(args: Array[String]): Unit = {
    val steamer2020FileName = "C:\\Users\\kcopp\\Dropbox\\Documents\\Fantasy\\Fantasy Baseball\\Fantasy Baseball 2020\\Steamer 2020 projections as of Jan 19 2020 - do not edit.csv"
    val rowMapper = new ProjectionsDataRowMapperSteamer2020()
    val projections = new ProjectionsDataFromCSVFileLoader(2020, steamer2020FileName, rowMapper).load()
    projections.foreach(System.err.println)
  }

}