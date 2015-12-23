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
     * Maps words to counts. Returns an RDD of (word, 1) pairs from BOOK.
     * The RDD will NOT have been reduced, so there will be multiple occurrences of words.
     */
    def getWordFrequencies(book: RDD[String]): RDD[(String, Int)] = {
        var tempRDD: RDD[(String, Int)] = book.flatMap(line => {
            val wordPattern = "[a-zA-Z']+".r
            for (word <- wordPattern.findAllMatchIn(line)) yield {
                (word.matched, 1) 
            }
        })
        
        return tempRDD
    }
    
    /**
     * Returns a frequency distribution for the most (or least) common words in BOOK.
     * This distribution will be given as an array of the form [ (word, frequency) ].
     *
     * Restricts output to a maximum of LIMIT words.
     * If ASC (short for "ascending") is specified, the distribution will prioritize
     * the least common words. Otherwise, the distribution will prioritize the most
     * common words.
     */
    def getWordDist(book: RDD[String], limit: Int = 100, asc: Boolean = false): Array[(String, Int)] = {
        // Create a (word, 1) pair for every word in the book
        var tempRDD: RDD[(String, Int)] = getWordFrequencies(book)
        
        // Reduce by key and convert intermediate RDD into an array
        var wordDist: Array[(String, Int)] = tempRDD.reduceByKey(_ + _).collect()
        
        // Sort the array
        if (asc) { // ...in ascending order of frequency
            wordDist = wordDist.sortWith((wf1, wf2) => wf1._2 < wf2._2) // wf = word/frequency
        } else { // ...in descending order of frequency
            wordDist = wordDist.sortWith((wf1, wf2) => wf1._2 > wf2._2)
        }
        
        return wordDist.slice(0, math.min(limit, wordDist.length))
    }
    
    /**
     * Returns a frequency distribution for the most common words in books 1-7.
     * Similarly to getWordDist, the output is an array of the form [ (word, frequency) ].
     * 
     * At the present time, no options can be selected.
     * Output will be restricted to the top 200 words.
     */
    def getWordDistAll(books: Array[RDD[String]]): Array[(String, Int)] = {
        var tempRDD: RDD[(String, Int)] = getWordFrequencies(books(0))
        for (i <- 1 to books.length - 1) {
            tempRDD = tempRDD.union(getWordFrequencies(books(i)))
        }
        
        var wordDist: Array[(String, Int)] = tempRDD.reduceByKey(_ + _).collect()
        wordDist = wordDist.sortWith((wf1, wf2) => wf1._2 > wf2._2)
        return wordDist.slice(0, 200)
    }
    
    /**
     * Returns an RDD of words from the book BOOK.
     * Words are defined as character groups containing letters and single quotation marks.
     */
    def getWordsFromBook(book: RDD[String]): RDD[String] = {
        var words: RDD[String] = book.flatMap(line => {
            val wordPattern = "[a-zA-Z']+".r
            for (word <- wordPattern.findAllMatchIn(line)) yield {
                word.matched
            }
        })
        
        return words
    }
    
    /**
     * Returns the number of unique words that appear within the set of books BOOKS.
     * For the moment, capitalized words are treated differently than non-capitalized words.
     */
    def numUniqueWords(books: Array[RDD[String]]): Long = {
        if (books.length > 0) {
            // Initial words (i.e. words from the first book)
            var words: RDD[String] = getWordsFromBook(books(0))
            words = words.distinct()
            
            // The rest of the words
            for (i <- 1 to books.length - 1) {
                var singleBkWords: RDD[String] = getWordsFromBook(books(i))
                words = words.union(singleBkWords)
                words = words.distinct()
            }
        
            return words.count()
        } else {
            return 0
        }
    }
}
