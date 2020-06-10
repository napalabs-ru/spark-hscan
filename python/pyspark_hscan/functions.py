from pyspark import SparkContext
from pyspark.sql.column import Column, _to_java_column, _to_seq, _create_column_from_literal
from pyspark.sql import SparkSession


def hlike(col, regexps):
    """Hyperscan regex like. Returns true if col matches one of regexps
    :param col: Column
    :param regexps: list of patterns to match
    :return: boolean column with match result
    """
    sc = SparkContext._active_spark_context
    patterns = sc._jvm.functions.array(_to_seq(sc, [
        _create_column_from_literal(x) for x in regexps
    ]))
    return Column(sc._jvm.ru.napalabs.spark.hscan.functions.hlike(_to_java_column(col), patterns))
