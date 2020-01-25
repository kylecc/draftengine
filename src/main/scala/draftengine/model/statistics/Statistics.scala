package draftengine.model.statistics

sealed trait Statistics

case class SeasonStatistics(year: Int, statistics: Seq[Statistic]) extends Statistics

