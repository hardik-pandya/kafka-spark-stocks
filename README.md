kaka-spark-stocks
=================

Kafka Producer App producing random stock ticker events and Aggrgating using Spark Structured Streaming

- ticker-consumer-app (spark structured streaming kaka consumer app)
- ticker-producer-app (scala producer)

- build.sbt (main sbt file which adds lib deepdancy to app folders)
- docker-compose.yml with kafka, zookeeper, consumer, producer apps
Dockerfile for both apps is generated using sbt docker plugin


To run the producer/consumer witk kafka and zookeeper

sbt docker
docker-compose up


Summary
=============
1. Producer implemenetd by sending JSON array to stocks topic every 10 seconds
2. Stocks price is randomized +/- 10% - 3 stocks per message.
3. Consumer is implemeted using Spark Structured Streaming by extracting
"value" field and using from_json() spark sql functions
4. JSON array "tickers" is parsed in spark with StructType
5. Aggregation performed on priced field after explode() array group by name
6. timestamp got added.
7. 30 sec batch interval conssole output at the end of this file.

Improvements
============
1. Paramterize hardcoded TOPIC with docker environment variables
2. Unit and Integration tests

Producer output
===============

```ticker-producer_1  | 21:27:54.211 [default-akka.actor.default-dispatcher-2] INFO TickerProducer$ - [Producer] Sending Data To Topic:: stocks with {"tickers":[{"name":"AMZN","price":2081},{"name":"MSFT","price":114},{"name":"AAPL","price":266}]}
ticker-producer_1  | 21:27:54.218 [default-akka.actor.default-dispatcher-2] INFO TickerProducer$ - [Producer] Sent Data To:: stocks
ticker-producer_1  | 21:28:04.176 [default-akka.actor.default-dispatcher-2] INFO TickerProducer$ - [Producer] Sending Data To Topic:: stocks with {"tickers":[{"name":"AMZN","price":2046},{"name":"MSFT","price":110},{"name":"AAPL","price":266}]}
ticker-producer_1  | 21:28:04.185 [default-akka.actor.default-dispatcher-2] INFO TickerProducer$ - [Producer] Sent Data To:: stocks
ticker-producer_1  | 21:28:14.176 [default-akka.actor.default-dispatcher-2] INFO TickerProducer$ - [Producer] Sending Data To Topic:: stocks with {"tickers":[{"name":"AMZN","price":2003},{"name":"MSFT","price":112},{"name":"AAPL","price":257}]}
ticker-producer_1  | 21:28:14.184 [default-akka.actor.default-dispatcher-2] INFO TickerProducer$ - [Producer] Sent Data To:: stocks```


Consumer Output
===============
-- error with consumer inside docker is not working inside docker with following error
```ticker-consumer_1  | Exception in thread "main" org.apache.spark.sql.AnalysisException: Failed to find data source: kafka. Please deploy the application as per the deployment section of "Structured Streaming + Kafka Integration Guide".; aka requires spark-submit
```


### Example Output from running outside Docker (IntelliJ console output)

```
-------------------------------------------
Batch: 0
-------------------------------------------

+----+------------------+--------------------+
|name|         avg_price|   current_timestamp|
+----+------------------+--------------------+
|AAPL|228.85145888594164|2021-06-11 16:18:...|
|AMZN|1939.2740740740742|2021-06-11 16:18:...|
|MSFT| 144.5976430976431|2021-06-11 16:18:...|
+----+------------------+-------------------

-------------------------------------------
Batch: 1
-------------------------------------------
+----+------------------+--------------------+
|name|         avg_price|   current_timestamp|
+----+------------------+--------------------+
|AAPL| 229.1578947368421|2021-06-11 16:19:...|
|AMZN|1939.4137931034484|2021-06-11 16:19:...|
|MSFT|144.54005602240898|2021-06-11 16:19:...|
+----+------------------+--------------------+

-------------------------------------------
Batch: 2
-------------------------------------------
+----+------------------+--------------------+
|name|         avg_price|   current_timestamp|
+----+------------------+--------------------+
|AAPL| 229.3717277486911|2021-06-11 16:19:...|
|AMZN|1939.4860655737705|2021-06-11 16:19:...|
|MSFT|144.50251818690543|2021-06-11 16:19:...|
+----+------------------+--------------------+


-------------------------------------------
Batch: 3
-------------------------------------------
+----+------------------+--------------------+
|name|         avg_price|   current_timestamp|
+----+------------------+--------------------+
|AAPL|229.78756476683938|2021-06-11 16:20:...|
|AMZN|1939.6772875816994|2021-06-11 16:20:...|
|MSFT|144.42657733109994|2021-06-11 16:20:...|
+----+------------------+--------------------+


Batch: 4
-------------------------------------------
+----+------------------+--------------------+
|name|         avg_price|   current_timestamp|
+----+------------------+--------------------+
|AAPL| 230.0771208226221|2021-06-11 16:20:...|
|AMZN|  1939.80358598207|2021-06-11 16:20:...|
|MSFT|144.37235228539578|2021-06-11 16:20:...|
+----+------------------+--------------------+

```
