#### Question1: How did changing values on the SparkSession property parameters affect the throughput and latency of the data?
Reducing "spark.sql.shuffle.partitions" from 200 (default) to 20 raises the throughput and reduces the latency since the amount of data is not huge and the operation can eliminate overhead.
Raising "spark.default.parallelism" to 8 improves the throughput and reduces the latency since there are more cores to execute the work.

#### Question2: What were the 2-3 most efficient SparkSession property key/value pairs? Through testing multiple variations on values, how can you tell these were the most optimal?
Parameters spark.sql.shuffle.partitions, spark.default.parallelism, spark.executor.cores. Those parameters directly affects job execution and operations like join and aggregation, I tried to find the optimal parameters by observing the speed of updating progress report in Console.