package ru.napalabs.spark.hscan

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

trait SparkSetup {
  def withSpark(testMethod: (SparkContext, SparkSession) => Any): Unit = {
    val sc = new SparkContext("local","test")
    val spark = SparkSession.builder().getOrCreate()
    try {
      testMethod(sc, spark)
    } finally spark.stop()
  }
}
