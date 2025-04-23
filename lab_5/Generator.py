# Databricks notebook source
from pyspark.sql.functions import expr
from pyspark.sql import Window
from pyspark.sql.functions import row_number


df1 = spark.range(0, 1_000_000).withColumnRenamed("id", "user_id") \
    .withColumn("score", (rand() * 100).cast("int")) \
    .withColumn("group_id", (expr("user_id % 5000")).cast("int"))

df2 = spark.range(0, 1_000_000).withColumnRenamed("id", "user_id") \
    .withColumn("level", (rand() * 5).cast("int")) \
    .withColumn("group_id", (expr("user_id % 5000")).cast("int"))

# COMMAND ----------

# INNER JOIN
df_inner = df1.join(df2, on=["user_id", "group_id"], how="inner")
df_inner.display()
# LEFT JOIN
df_left = df1.join(df2, on=["user_id", "group_id"], how="left")
df_left.display()

# COMMAND ----------

window_spec = Window.partitionBy("user_id").orderBy("score")

# KEEP HIGHEST SCORE
df_deduped = df_inner.withColumn("row_num", row_number().over(window_spec)) \
                     .filter("row_num = 1") \
                     .drop("row_num")
df_deduped_simple = df_inner.dropDuplicates(["user_id"]).display()
