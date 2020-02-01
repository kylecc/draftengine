package draftengine.calculation

import draftengine.model.league.LeagueRules

case class CalculationContext[T](
  playerInputStatistics: Seq[CalculationInputComponent[T]],
  leagueRules: LeagueRules,
  outputStatisticsCalculators: Seq[Calculation[T]])