package ru.napalabs.spark.hscan

import org.scalatest.FunSuite


class ImplicitsTest extends FunSuite with SparkSetup {

  test("test implicit in sql") {
    withSpark { (sc, spark) =>
      val rdd = sc.makeRDD(List(("qwerty"), ("asdfg"), ("Тест русского я\"зыка")))
      import spark.implicits._
      val df = rdd.toDF("val")
      import implicits._
      spark.registerHyperscanFuncs()
      val expected = Array("qwerty", "Тест русского я\"зыка")
      val actual = df.where("hlike(val, array('qwe', 'русс[^ ]+ я\"'))").collect().map(_.getString(0))
      assert(expected sameElements actual)
    }
  }

}
