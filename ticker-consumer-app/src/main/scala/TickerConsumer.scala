import org.apache.spark.sql.{SparkSession, functions}
import org.apache.spark.sql.functions.{avg, col, explode, from_json,current_timestamp}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{ArrayType, IntegerType, StringType, StructField, StructType}

object TickerConsumer extends App{

  val TOPIC = "stocks"
  val BROKERS  = "kafka:9092"
  val spark = SparkSession
    .builder
    .appName("AvergeStockPrice")
    .master("local[*]")
    .config("spark.testing.memory","2147480000")
    .getOrCreate()

  var inputDF = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers",BROKERS)
    .option("subscribe", TOPIC)
    .option("startingOffsets", "earliest")
    .load()


  val schema = StructType(
    Array(
      StructField("tickers",
        ArrayType(StructType(
          Array(
               StructField("name", StringType,true),
               StructField("price", IntegerType,true)
      ))))
    )
  )

  val rawDF = inputDF.select(col("value").cast(StringType).alias("json"))
    .select(from_json(col("json"), schema) as "data")
    .select("data.*")
  val explodeTickersDF = rawDF.select(explode(col("tickers"))).
    withColumnRenamed("col","tickers")

  val tickerDF = explodeTickersDF.select(col("tickers.name").alias("name"),
    col("tickers.price").alias("price"))

  val aggDF = tickerDF.select("*").groupBy("name").agg(avg(col("price")).alias("avg_price"))
  val outputDF = aggDF.withColumn("current_timestamp",current_timestamp)
  outputDF.printSchema()
  val query = outputDF.writeStream.trigger(Trigger.ProcessingTime(30)).outputMode("complete").format("console").start()
  query.awaitTermination()
}
