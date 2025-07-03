package agh.wggios.analizadanych.datareader

import agh.wggios.analizadanych
import org.apache.spark.sql.DataFrame
import agh.wggios.analizadanych.{LoggingUtils, SparkSessionProvider}
import org.apache.spark.sql.DataFrame

import java.nio.file.{Files, Paths}

class DataReader extends SparkSessionProvider {

  def read_csv(path: String): DataFrame =  {
    logInfo("Reading file")
    spark.read.format("csv").option("header", value = true).option("inferSchema", value = true).load(path)

  }

  def read_parquet(path: String): DataFrame = {
    logInfo("reading parquet")
    spark.read.format("parquet").option("header", value = true).option("inferSchema", value = true).load(path)
  }
}
