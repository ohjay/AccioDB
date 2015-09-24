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
            count += books(i).map(
                line => phrase.r.findAllMatchIn(line).length
            ).reduce(_ + _)
        }
        
        return count
    }
    
    def get_sentences_with(books: Array[RDD[String]], phrase: String): Array[String] = {
        var sentences: Array[String] = Array[String]()
        /* Insert sentence-finding logic here */
        return sentences
    }
}
