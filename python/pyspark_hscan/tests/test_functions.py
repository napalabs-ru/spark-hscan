import os
import sys
import pathlib
import unittest
import logging
import pandas as pd
from pandas.testing import assert_frame_equal

from pyspark.sql import SparkSession
from pyspark.sql.functions import col
from pyspark_hscan.functions import *


class PySparkTest(unittest.TestCase):
    @classmethod
    def suppress_py4j_logging(cls):
        logger = logging.getLogger('py4j')
        logger.setLevel(logging.WARN)

    @classmethod
    def create_testing_pyspark_session(cls):
        os.environ["PYSPARK_PYTHON"] = sys.executable
        os.environ["PYSPARK_DRIVER_PYTHON"] = sys.executable
        jar_path = str(pathlib.Path(os.path.realpath(__file__)).parent) + '/spark-hscan-lib.jar'
        return (SparkSession.builder
                .master('local')
                .appName('local-pyspark-testing-context')
                .config('spark.jars', jar_path)
                .enableHiveSupport()
                .getOrCreate())

    @classmethod
    def setUpClass(cls):
        cls.suppress_py4j_logging()
        cls.spark = cls.create_testing_pyspark_session()

    @classmethod
    def tearDownClass(cls):
        cls.spark.stop()

    @classmethod
    def assert_frame_equal_with_sort(cls, actual, expected, keycolumns):
        actual_sorted = actual.sort_values(by=keycolumns).reset_index(drop=True)
        expected_sorted = expected.sort_values(by=keycolumns).reset_index(drop=True)
        assert_frame_equal(actual_sorted, expected_sorted)


class TestHlike(PySparkTest):
    def test_basic(self):
        data_pd = pd.DataFrame({'val': ['Lorem ipsum', 'dolor sit amet', 'consectetur adipiscing elit']})
        data_spark = self.spark.createDataFrame(data_pd)
        actual = data_spark.select(
            col('val'),
            hlike(col('val'), ['Lorem? ', 'dolo[^ ]+']).alias('match_result')
        ).toPandas()
        expected = pd.DataFrame({
            'val': ['Lorem ipsum', 'dolor sit amet', 'consectetur adipiscing elit'],
            'match_result': [True, True, False]
        })
        expected = expected[['val', 'match_result']]
        self.assert_frame_equal_with_sort(actual, expected, ['val'])


if __name__ == '__main__':
    unittest.main()
