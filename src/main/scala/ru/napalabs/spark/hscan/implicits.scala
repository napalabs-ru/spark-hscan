package ru.napalabs.spark.hscan

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.FunctionIdentifier
import org.apache.spark.sql.catalyst.expressions.Expression
import ru.napalabs.spark.hscan.funcs.HyperscanLike

object implicits extends Serializable {

  implicit class SessionImplicits(spark: SparkSession) {
    def registerHyperscanFuncs() = {
      spark.sessionState.functionRegistry
        .registerFunction(FunctionIdentifier("hlike"),
          (expressions: Seq[Expression]) => {
            HyperscanLike(expressions(0), expressions(1))
          }
        )
    }
  }

}
