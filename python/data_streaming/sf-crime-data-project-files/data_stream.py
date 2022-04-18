import logging
from pyspark.sql import SparkSession
from pyspark.sql.types import *
import pyspark.sql.functions as psf


schema = StructType([
    StructField("crime_id", StringType(), True),
    StructField("original_crime_type_name", StringType(), True),
    StructField("report_date", StringType(), True),
    StructField("call_date", StringType(), True),
    StructField("offense_date", StringType(), True),
    StructField("call_time", StringType(), True),
    StructField("call_date_time", StringType(), True),
    StructField("disposition", StringType(), True),
    StructField("address", StringType(), True),
    StructField("city", StringType(), True),
    StructField("state", StringType(), True),
    StructField("agency_id", StringType(), True),
    StructField("address_type", StringType(), True),
    StructField("common_location", StringType(), True)])

def run_spark_job(spark):

    # TODO Create Spark Configuration
    # Create Spark configurations with max offset of 200 per trigger
    # set up correct bootstrap server and port
    df = spark \
        .readStream \
        .format("kafka") \
        .option("subscribe", "calls") \
        .option("startingOffsets", "earliest") \
        .option("maxOffsetsPerTrigger", 200) \
        .option("kafka.bootstrap.servers", "localhost:9092") \
        .load()

    # Show schema for the incoming resources for checks
    df.printSchema()

    # TODO extract the correct column from the kafka input resources
    # Take only value and convert it to String
    kafka_df = df.selectExpr("CAST(value AS STRING)")

    service_table = kafka_df\
        .select(psf.from_json(psf.col('value'), schema).alias("DF"))\
        .select("DF.*")

    # TODO select original_crime_type_name and disposition
    distinct_table = service_table.select("original_crime_type_name", "disposition", "call_date_time")
    distinct_table = distinct_table.withColumn("call_date_time", psf.to_timestamp("call_date_time"))
    distinct_table = distinct_table.withWatermark("call_date_time", "60 minutes")
    distinct_table.printSchema()
    
    # count the number of original crime type
    agg_df = distinct_table.withWatermark("call_date_time", "60 minutes")\
        .groupBy(psf.window(distinct_table.call_date_time, "60 minutes"), "disposition")\
        .agg(psf.approx_count_distinct("original_crime_type_name").alias("number_of_original_crime_type"))
    # agg_df = distinct_table.groupBy("disposition").count()
    crime_type_count_df = distinct_table.withWatermark("call_date_time", "60 minutes")\
        .groupBy(psf.window(distinct_table.call_date_time, "60 minutes"), "original_crime_type_name").count()
    
    # TODO Q1. Submit a screen shot of a batch ingestion of the aggregation
    # TODO write output stream
    query1 = agg_df \
            .writeStream \
            .outputMode("append") \
            .trigger(processingTime="30 seconds") \
            .format("console") \
            .option("truncate", "false") \
            .start()
    
    query2 = crime_type_count_df \
            .writeStream \
            .outputMode("append") \
            .trigger(processingTime="30 seconds") \
            .format("console") \
            .option("truncate", "false") \
            .start()
    
    # TODO attach a ProgressReporter
    # query1.awaitTermination()

    # TODO get the right radio code json path
    radio_code_json_filepath = "./radio_code.json"
    radio_code_df = spark.read.option("multiline","true").json(radio_code_json_filepath)
    
    # clean up your data so that the column names match on radio_code_df and agg_df
    # we will want to join on the disposition code

    # TODO rename disposition_code column to disposition
    radio_code_df = radio_code_df.withColumnRenamed("disposition_code", "disposition")

    # TODO join on disposition column
    join_query = agg_df.alias("agg").join(radio_code_df.alias("radio_code"), agg_df.disposition == radio_code_df.disposition).select("window", "agg.disposition", "description", "number_of_original_crime_type") \
                .writeStream \
                .outputMode("append") \
                .trigger(processingTime="30 seconds") \
                .format("console") \
                .option("truncate", "false") \
                .start()

    
    query1.awaitTermination()
    query2.awaitTermination()
    join_query.awaitTermination()


if __name__ == "__main__":
    logger = logging.getLogger(__name__)

    # TODO Create Spark in Standalone mode
    spark = SparkSession \
        .builder \
        .master("local[*]") \
        .appName("KafkaSparkStructuredStreaming") \
        .config("spark.ui.port", 3000) \
        .getOrCreate()
    
    spark.conf.set("spark.sql.shuffle.partitions", 20)
    spark.conf.set("spark.default.parallelism", 8)
    
    spark.sparkContext.setLogLevel('WARN')

    logger.info("Spark started")

    run_spark_job(spark)

    spark.stop()
