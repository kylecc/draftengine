package draftengine.service

import draftengine.model.player.PlayerProjection
import draftengine.model.statistics.{ProjectionsSource, SeasonStatistics, Statistic, Statistics}

trait ProjectionsService {

  def getPlayerProjections(playerId: String): Seq[PlayerProjection]
  def getPlayerProjection(playerId: String, year: Int): PlayerProjection
  def getPlayerProjection(playerId: String, year: Int, sourceId: String): PlayerProjection

}

class AggregatingProjectionsCachedFileBasedService extends ProjectionsService {

  private lazy val projectionsByPlayerId = getPlayerProjectionsFrom2020DataFile()

  override def getPlayerProjections(playerId: String): Seq[PlayerProjection] = {
    // TODO: pull from multiple sources
    val projections = getCachedPlayerProjections(playerId).getOrElse(dummyProjection(playerId))
    Seq(projections)
  }

  override def getPlayerProjection(playerId: String, year: Int): PlayerProjection = {
    getCachedPlayerProjections(playerId).collect {
      case p @ PlayerProjection(_, _, SeasonStatistics(statsYear, _)) if statsYear == year => p
    }.getOrElse(dummyProjection(playerId))
  }

  override def getPlayerProjection(playerId: String, year: Int, sourceId: String): PlayerProjection = {
    getCachedPlayerProjections(playerId).collect {
      case p @ PlayerProjection(_, src, SeasonStatistics(statsYear, _)) if statsYear == year && src == sourceId => p
    }.getOrElse(dummyProjection(playerId))
  }

  private def getCachedPlayerProjections(playerId: String): Option[PlayerProjection] = {
    projectionsByPlayerId.get(playerId)
  }

  // TODO: use this!
  private def getAggregatedProjectionsForPlayer(playerId: String): PlayerProjection = {
    // TODO: calculate with weights properly
    dummyProjection(playerId, 2020)
  }

  private def getPlayerProjectionsFrom2020DataFile(): Map[String, PlayerProjection] = {
    null/*
    FileBasedDataLoader
      .load2020ProjectionsSpreadsheet()
      .map { case (_, projection, _) => projection.playerId -> projection }
      .toMap*/
  }

  private def dummyProjection(playerId: String, year: Int = 0): PlayerProjection = {
    PlayerProjection(playerId, ProjectionsSource.NONE, dummyStatistics(year))
  }

  private def dummyStatistics(year: Int): Statistics = {
    // TODO: probably revisit this and make more general
    val stats = Seq(Statistic(Statistic.ROC_BOI_CO_POINTS, 0))
    SeasonStatistics(year, stats)
  }

}