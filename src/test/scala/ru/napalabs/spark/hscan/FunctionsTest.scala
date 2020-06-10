package ru.napalabs.spark.hscan

import org.scalatest.FunSuite
import functions._

class FunctionsTest extends FunSuite with SparkSetup {
  test("hlike function") {
    withSpark { (sc, spark) =>
      val rdd = sc.makeRDD(List(("Lorem ipsum"), ("dolor sit amet"), ("consectetur adipiscing elit")))
      import spark.implicits._
      val df = rdd.toDF("val")
      val actual = df.select(hlike($"val", Array("Lorem? ", "dolo[^ ]+"))).collect().map(_.getBoolean(0))
      val expected = Array(true, true, false)
      assert(actual sameElements expected)
    }
  }
}
