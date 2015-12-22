import org.apache.spark.rdd.RDD

/**
 * The processor/parser for Accio. 
 * Given a Spark context, methods within this object will be able 
 * to search for Harry Potter-related data in the associated text file.
 * @author Owen Jow
 */
object SparkSearcher {
    /**
     * This function takes in an array of books and a phrase to search for,
     * and returns the total number of times the given phrase appears throughout
     * each book in BOOKS.
     */
    def count(books: Array[RDD[String]], phrase: String): Long = {
        var count: Long = 0
        for (i <- 0 to books.length - 1) {
            count += books(i).map(
                line => phrase.r.findAllMatchIn(line).length
            ).reduce(_ + _)
        }
        
        return count
    }
    
    /**
     * Returns a frequency distribution for the most common words in BOOK.
     * This distribution will be given as a map of the form { word: frequency }.
     */
    def getWordDist(book: RDD[String], limit: Int = 100, asc: Boolean = false): Array[(String, Int)] = {
        // Map words to counts
        var tempRDD: RDD[(String, Int)] = book.flatMap(line => {
            val wordPattern = "[a-zA-Z']+".r
            for (word <- wordPattern.findAllMatchIn(line)) yield {
                (word.matched, 1) 
            }
        })
        
        // Reduce by key and convert intermediate RDD into an array
        var wordDist: Array[(String, Int)] = tempRDD.reduceByKey(_ + _).collect();
        
        // Sort the array
        if (asc) { // ...in ascending order of frequency
            wordDist = wordDist.sortWith((wf1, wf2) => wf1._2 < wf2._2) // wf = word/frequency
        } else { // ...in descending order of frequency
            wordDist = wordDist.sortWith((wf1, wf2) => wf1._2 > wf2._2)
        }
        
        return wordDist.slice(0, limit)
    }
}
