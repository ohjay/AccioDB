import org.apache.spark.rdd.RDD

/**
 * The processor/parser for Accio. 
 * Given a Spark context, methods within this object will be able 
 * to search for Harry Potter-related data in the associated text file.
 * @author Owen Jow
 */
object SparkSearcher {
    def count(books: Array[RDD[String]], phrase: String): Long = {
        var count: Long = 0
        for (i <- 0 to books.length - 1) {
            count += books(i).filter(line => line.contains(phrase)).count()
        }
        
        return count
    }
}
