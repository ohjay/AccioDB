import org.apache.spark.rdd.RDD

/**
 * The processor/parser for Accio. 
 * Given a Spark context, methods within this object will be able 
 * to search for Harry Potter-related data in the associated text file.
 * @author Owen Jow
 */
object SparkSearcher {
    def find(books: Array[RDD[String]]) {
        return "Nitwit! Blubber! Oddment! Tweak!"
    }
}
