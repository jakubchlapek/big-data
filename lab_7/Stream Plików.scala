// Databricks notebook source
// MAGIC %md ## Dane
// MAGIC Dane są dostępne na AWS i dostęp zapewnia Databricks `/databricks-datasets/structured-streaming/events/` 

// COMMAND ----------

// MAGIC %fs ls /databricks-datasets/structured-streaming/events/

// COMMAND ----------

// MAGIC %fs head /databricks-datasets/structured-streaming/events/file-0.json

// COMMAND ----------

// MAGIC %md 
// MAGIC * Stwórz osobny folder 'streamDir' do którego będziesz kopiować część plików. możesz użyć dbutils....
// MAGIC * Pozostałe pliki będziesz kopiować jak stream będzie aktywny

// COMMAND ----------

val streamDir = "/tmp/streamDir/"
dbutils.fs.mkdirs(streamDir)

dbutils.fs.cp("/databricks-datasets/structured-streaming/events/file-0.json", streamDir)
dbutils.fs.cp("/databricks-datasets/structured-streaming/events/file-1.json", streamDir)

// COMMAND ----------

// MAGIC %md ## Analiza danych/Statyczny DF
// MAGIC * Stwórz schemat danych i wyświetl zawartość danych z oginalnego folderu

// COMMAND ----------

import org.apache.spark.sql.types._

val inputPath = "/databricks-datasets/structured-streaming/events/"

val jsonSchema =  new StructType() 
  .add("time", LongType) 
  .add("action", StringType)

val staticInputDF = spark.read 
  .schema(jsonSchema) 
  .json(inputPath)

display(staticInputDF.limit(5))

// COMMAND ----------

// MAGIC %md 
// MAGIC Policz ilość akcji "open" i "close" w okienku (window) jedno godzinnym (kompletny folder). 

// COMMAND ----------

import org.apache.spark.sql.functions._

val iloscAkcji = staticInputDF
  .withColumn("eventTime", from_unixtime(col("time")).cast("timestamp"))
  .groupBy(window(col("eventTime"), "1 hour"), col("action"))
  .agg(count("*").alias("count"))
  .orderBy("window")

iloscAkcji.createOrReplaceTempView("iloscAkcji")
display(iloscAkcji.limit(5))

// COMMAND ----------

// MAGIC %md 
// MAGIC Użyj sql i pokaż na wykresie ile było akcji 'open' a ile 'close'.

// COMMAND ----------

// MAGIC %sql select action, sum(count) as total_count from iloscAkcji group by action

// COMMAND ----------

// MAGIC %md
// MAGIC Użyj sql i pokaż ile było akcji w każdym dniu i godzinie przykład ('Jul-26 09:00')

// COMMAND ----------

// MAGIC %sql select action, date_format(window.end, "MMM-dd HH:mm") as time, count from iloscAkcji order by time, action

// COMMAND ----------

// MAGIC %md ## Stream Processing 
// MAGIC Teraz użyj streamu.
// MAGIC * Ponieważ będziesz streamować pliki trzeba zasymulować, że jest to normaly stream. Podpowiedź dodaj opcję 'maxFilesPerTrigger'
// MAGIC * Użyj 'streamDir' niekompletne pliki

// COMMAND ----------

import org.apache.spark.sql.functions._

//odpal stream
val streamingInputDF = spark.readStream
  .schema(jsonSchema)
  .option("maxFilesPerTrigger", 1)
  .json(streamDir)

val streamingWithTimestamp = streamingInputDF.withColumn("timestamp", from_unixtime(col("time")))

// sumujemy open i close tak ja jak powyżej w okienku jednogodzinnym
val streamingCountsDF = streamingWithTimestamp
  .groupBy(window(col("timestamp"), "1 hour"), col("action"))
  .count()

// COMMAND ----------

// MAGIC %md
// MAGIC Sprawdź czy stream działa

// COMMAND ----------


streamingCountsDF.isStreaming

// COMMAND ----------

// MAGIC %md 
// MAGIC * Zredukuj partyce shuffle do 4 
// MAGIC * Teraz ustaw Sink i uruchom stream
// MAGIC * użyj formatu 'memory'
// MAGIC * 'outputMode' 'complete'

// COMMAND ----------

val query = streamingCountsDF
  .repartition(4) 
  .writeStream
  .format("memory")              
  .outputMode("complete")        
  .queryName("counts") 
  .start()

// COMMAND ----------

// MAGIC %md 
// MAGIC `query` działa teraz w tle i wczytuje pliki cały czas uaktualnia count. Postęp widać w Dashboard

// COMMAND ----------

Thread.sleep(3000) // lekkie opóźnienie żeby poczekać na wczytanie plików

// COMMAND ----------

// MAGIC %md
// MAGIC * Użyj sql żeby pokazać ilość akcji w danym dniu i godzinie 

// COMMAND ----------

// MAGIC %sql select action, date_format(window.end, "MMM-dd HH:mm") as time, count from counts order by time, action

// COMMAND ----------

// MAGIC %md 
// MAGIC * Sumy mogą się nie zgadzać ponieważ wcześniej użyłeś niekompletnych danych.
// MAGIC * Teraz przekopiuj resztę plików z orginalnego folderu do 'streamDir', sprawdź czy widać zmiany 
// MAGIC

// COMMAND ----------

// MAGIC %sql select action, date_format(window.end, "MMM-dd HH:mm") as time, count from counts WHERE HOUR(window.end) IN (3, 4, 5) order by time, action 

// COMMAND ----------

dbutils.fs.cp("/databricks-datasets/structured-streaming/events/", streamDir, recurse=true)


// COMMAND ----------

// MAGIC %md
// MAGIC * Zatrzymaj stream

// COMMAND ----------

query.stop()