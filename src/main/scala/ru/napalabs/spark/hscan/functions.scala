package ru.napalabs.spark.hscan

import org.apache.spark.sql.Column
import org.apache.spark.sql.catalyst.expressions.{Expression, Literal}
import ru.napalabs.spark.hscan.funcs.HyperscanLike

object functions {
  private def withExpr(expr: Expression): Column = new Column(expr)

  /**
   * Hyperscan regex like. Hyperscan regexps differs from java regexps,
   * see <a href="https://intel.github.io/hyperscan/dev-reference/compilation.html#semantics">hyperscan documentation</a>
   * @return boolean column with match result
   */
  def hlike(e: Column, patterns: Array[String]): Column = withExpr{
    HyperscanLike(e.expr, Literal(patterns))
  }

  /**
   * Hyperscan regex like. Hyperscan regexps differs from java regexps,
   * see <a href="https://intel.github.io/hyperscan/dev-reference/compilation.html#semantics">hyperscan documentation</a>
   * @return boolean column with match result
   */
  def hlike(e: Column, patterns: Column): Column = withExpr{
    HyperscanLike(e.expr, patterns.expr)
  }
}
