import java.io.BufferedReader
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.log4j.Logger
import org.apache.log4j.Level

/**
 * The command-line user interface for Accio.
 * @author Owen Jow
 */
object AccioUI {
    var shouldQuit: Boolean = false
    var folderFilepath: String = "harry_potter_bookset"
    val helpStr: String = """quit: Quits the program.
            |help [COMMAND]: Displays the list of commands that you're seeing right now.
            |count [-b book_num] PHRASE: Counts the number of times the given phrase appears throughout all seven books.
            |word_dist [-a] [-n top_n_words] BOOK_NUM: Lists words by frequency of appearance in a given book.
            |stat STAT_OPTION: Prints statistics related to the book text.""".stripMargin
    
    /**
     * Launches the program. Contains the outermost logic for the read-eval-print loop.
     * Interprets commands, executes them, and outputs the results.
     * Then it repeats... and repeats... until the user finally decides to "quit".
     */
    def main(args: Array[String]) {
        println("Welcome to Accio!")
        print("Would you like to specify your own path to the book data directory? [y/n]: ")
        
        val br: BufferedReader = Console.in
        var line: String = br.readLine()
        while (line != "y" && line != "n") {
            print("Please enter 'y' or 'n': ")
            line = br.readLine()
        }
        
        // Either get the user's filepath or indicate usage of the default one
        if (line == "y") {
            print("Enter a filepath: ")
            var folderFilepath = br.readLine()
            println("Okay, we'll use \"" + folderFilepath + "\" as the folder filepath.")
        } else {
            println("Okay, we'll use the default folder filepath (" + folderFilepath + ").")
        }
        
        // Disable (most) log output
        Logger.getLogger("org").setLevel(Level.WARN)
        Logger.getLogger("akka").setLevel(Level.WARN)
        
        // Set up Spark and book file RDDs
        println("\nStarting up AccioDB; please wait......")
        println("[BEGIN] SPARK LOG OUTPUT >>>>>>\n")
        val conf = new SparkConf().setAppName("AccioDB")
        val sc = new SparkContext(conf)
        val books: Array[RDD[String]] = new Array[RDD[String]](7)
        for (i <- 0 to 6) {
            books(i) = sc.textFile(folderFilepath + "/book" + (i + 1) + ".txt").cache()
        }
        
        println("\n>>>>>> [END] SPARK LOG OUTPUT")
        println("AccioDB launched! Remember, help will always be given at Hogwarts to those who ask for it.")
        
        while (!shouldQuit) {
            print("> ")
            line = br.readLine()
            var tokens: Array[String] = line.split(" ")
            var command: String = tokens(0)
            tokens = tokens.drop(1)
            
            executeCommand(command, tokens, books)
        }
    }
    
    /**
     * Attempts to parse TOKEN as a book number.
     * If successful, that book number will be returned as an integer.
     *
     * There are seven Harry Potter books, so the number must be in the range 1-7.
     * If it's not (or TOKEN cannot be represented as an integer), then we'll
     * return -1 as an error signal.
     *
     * This function will also handle the printing of specific error messages.
     */
    def parseBookNum(token: String): Int = {
        var bookNum: Int = 0
        try {
            bookNum = token.toInt
        } catch {
            case e: Exception => println("[ERROR] book_num must be an integer.\n")
            return -1
        }
        
        if (bookNum < 1 || bookNum > 7) {
            // That's not a Harry Potter book!
            println("[ERROR] book_num must be an integer between 1 and 7.\n")
            return -1
        }
        
        return bookNum
    }
    
    /**
     * This function attempts to consider TOKEN as an integer limit.
     *
     * Returns TOKEN as an integer if successful.
     * Otherwise, handles exception logic as necessary and returns -1.
     */
    def parseLimit(token: String): Int = {
        var limit: Int = 0
        try {
            limit = token.toInt
        } catch {
            case e: Exception => println("[ERROR] top_n_words must be an integer.\n")
            return -1
        }
        
        return limit
    }
    
    /**
     * Maps book numbers to book titles.
     * Every Harry Potter book begins with "Harry Potter and the", 
     * so to avoid redundancy we'll only return the last portion of a title.
     *
     * For example, if you passed in a 2, you would get "Chamber of Secrets" as the title.
     * 
     * Special cases:
     * - 100: the code for "all books"
     * - anything else that's not 1-7: invalid
     */
    def getBookTitle(bookNum: Int): String = {
        var title: String = bookNum match {
            case 1 =>
                "Philosopher's Stone"
            case 2 =>
                "Chamber of Secrets"
            case 3 =>
                "Prisoner of Azkaban"
            case 4 =>
                "Goblet of Fire"
            case 5 =>
                "Order of the Phoenix"
            case 6 =>
                "Half-Blood Prince"
            case 7 =>
                "Deathly Hallows"
            case 100 =>
                "all books"
            case other =>
                "[ERROR] Invalid book number!"
        }
        
        return title
    }
    
    /**
     * Given a word distribution in the format returned by SparkSearcher.getWordDist, 
     * this function will print that distribution in a human-readable format.
     *
     * This pretty much amounts to printing each element in the array in order.
     */
    def printWordDist(wordDistribution: Array[(String, Int)], bookNum: Int) {
        println("=== WORD DISTRIBUTION for " + getBookTitle(bookNum).toUpperCase() + "===")
        println("(Format: [word] | [count])\n")
        
        // Iterate through the distribution RDD
        for ((w, wfreq) <- wordDistribution) {
            println(f"$w%-16s" + "| " + wfreq.toString)
        }
    }
    
    /**
     * Executes user commands.
     * Takes in a command and that command's arguments, and processes these tokens
     * in order to display the desired output.
     */
    def executeCommand(command: String, tokens: Array[String], books: Array[RDD[String]]) {
        command match {
            case "quit" | "exit" => shouldQuit = true
            case "help" => 
                if (tokens.length > 0) {
                    // The user presumably wants information about a specific command
                    tokens(0) match {
                        case "quit" => println("""Usage: quit
                                |Quits the program. When this happens,
                                |the prompt will disappear and all session data will be lost.""".stripMargin)
                        case "exit" => println("""Usage: exit
                                |Quits the program. The prompt will disappear and all session data
                                |will be lost. This action is identical to that of the 'quit' command.""".stripMargin)
                        case "count" => println("""Usage: count [-b book_num] PHRASE
                                |If the [-b booknum] option is specified, searches the given book (#1 to #7)
                                |for PHRASE and displays the number of appearances within that one book. 
                                |Otherwise (if no -b flag), counts the number of times PHRASE occurs 
                                |throughout all seven books.
                                |
                                |Ex. usage) count -b 1 Fluffy <-- counts the # of 'Fluffy's in book 1
                                |Ex. usage) count -b 5 Harry shouted <-- counts the # of 'Harry shouted's in book 5""".stripMargin)
                        case "word_dist" => println("""Usage: word_dist [-a] [-n top_n_words] BOOK_NUM
                                |Ranks words by frequency of appearance.
                                |- If the [-n top_n_words] option is specified, limits output to the N 
                                |  most common words.
                                |- If the [-a] flag is used, then the output will be sorted in ascending order. 
                                |  (That is, the least common words will appear first.)
                                |- You can also specify "all" as an option (à la `word_dist all`)
                                |  to see the 200 most frequently-appearing words among all 
                                |  seven books. No other options can be used simultaneously with "all".
                                |
                                |The default word limit is 100 words.
                                |
                                |Ex. usage) word_dist -n 20 3 <-- lists the 20 most common words in PoA
                                |Ex. usage) word_dist 4 <-- lists the 100 most common words in GoF
                                |Ex. usage) word_dist -an 9 2 <-- lists the 9 least common words in CoS
                                |Ex. usage) word_dist -a -n 15 2 <-- lists the 15 least common words in CoS
                                |Ex. usage) word_dist -a 7 <-- lists the 100 least common words in DH
                                |Ex. usage) word_dist all <-- 200 most common words in books 1-7""".stripMargin)
                        case "stat" => println("""Usage: stat STAT_OPTION
                                |Prints text-related statistics.
                                |The statistic displayed will depend on STAT_OPTION.
                                |
                                |Stat options include:
                                |- UNIQUE_WORDS: Returns the number of unique words throughout all seven books.
                                |- UNIQUE_WORDS [booknum]: Returns the number of unique words within book BOOK_NUM.
                                |
                                |Ex. usage) stat UNIQUE_WORDS <-- outputs the number of unique words throughout all 7 books
                                |Ex. usage) stat UNIQUE_WORDS 5 <-- outputs the number of unique words in OotP""".stripMargin)
                        case other => println(s"""Unrecognized command: ${other}.
                                |Displaying general help string:
                                |
                                |$helpStr""".stripMargin)
                    }
                } else {
                    println(helpStr) // general help string
                }
            case "count" => 
                if (tokens.length >= 3 && tokens(0) == "-b") {
                    var bookNum: Int = parseBookNum(tokens(1))
                    if (bookNum == -1) {
                        // The error message should already have been displayed
                        // But the point is that there WAS an error. So we're done here
                        return
                    }
                    
                    println(SparkSearcher.count(Array(books(bookNum - 1)), tokens.drop(2).mkString(" ")))
                } else if (tokens.length == 0) {
                    println("[ERROR] Usage: count [-b book_num] PHRASE")
                } else {
                    println(SparkSearcher.count(books, tokens.mkString(" ")))
                }
            case "word_dist" =>
                try {
                    if (tokens(0) == "all") {
                        printWordDist(SparkSearcher.getWordDistAll(books), 100)
                        println() // formatting, yknow?
                        return
                    } else if (tokens.length > 1) {
                        // That is, if there's a chance that there are flags
                        if (tokens(0) == "-n") {
                            var limit: Int = parseLimit(tokens(1))
                            if (limit == -1) {
                                return
                            }
                        
                            var bookNum: Int = 1
                            var wordDist: Array[(String, Int)] = null
                        
                            // The user might still have specified the "ascending" option
                            if (tokens(2) == "-a") {
                                bookNum = parseBookNum(tokens(3))
                                if (bookNum == -1) {
                                    return
                                }
                            
                                wordDist = SparkSearcher.getWordDist(books(bookNum - 1), limit, true)
                            } else {
                                bookNum = parseBookNum(tokens(2))
                                if (bookNum == -1) {
                                    return
                                }
                            
                                wordDist = SparkSearcher.getWordDist(books(bookNum - 1), limit)
                            }
                        
                            printWordDist(wordDist, bookNum)
                            println()
                            return
                        } else if (tokens(0) == "-a") {
                            // See if the user is also trying to use the -n flag
                            if (tokens(1) == "-n") {
                                var limit: Int = parseLimit(tokens(2))
                                if (limit == -1) {
                                    return
                                }
                            
                                var bookNum: Int = parseBookNum(tokens(3))
                                if (bookNum == -1) {
                                    return
                                }
                            
                                printWordDist(SparkSearcher.getWordDist(books(bookNum - 1), limit, true), bookNum)
                            } else {
                                var bookNum: Int = parseBookNum(tokens(1))
                                if (bookNum == -1) {
                                    return
                                }
                            
                                printWordDist(SparkSearcher.getWordDist(books(bookNum - 1), 100, true), bookNum)
                            }
                        
                            println()
                            return
                        } else if (tokens(0) == "-an" || tokens(0) == "-na") {
                            var limit: Int = parseLimit(tokens(1))
                            if (limit == -1) {
                                return
                            }
                        
                            var bookNum: Int = parseBookNum(tokens(2))
                            if (bookNum == -1) {
                                return
                            }
                        
                            printWordDist(SparkSearcher.getWordDist(books(bookNum - 1), limit, true), bookNum)
                            println()
                            return
                        }
                    }
                
                    // Here, we'll proceed as if there are no flags
                    if (tokens.length == 0) {
                        println("[ERROR] Usage: word_dist [-a] [-n top_n_words] BOOK_NUM")
                    } else {
                        var bookNum: Int = parseBookNum(tokens(0))
                        if (bookNum == -1) {
                            // Bad input!
                            return
                        }
                    
                        printWordDist(SparkSearcher.getWordDist(books(bookNum - 1)), bookNum)
                    }
                } catch {
                    case e: ArrayIndexOutOfBoundsException => 
                        println("[ERROR] Usage: word_dist [-a] [-n top_n_words] BOOK_NUM")
                }
            case "stat" =>
                if (tokens.length == 0) {
                    println("[ERROR] Usage: stat STAT_OPTION")
                    println("STAT_OPTION is required!")
                } else if (tokens(0) == "UNIQUE_WORDS") {
                    // Display the number of unique words throughout certain HP books
                    if (tokens.length > 1) {
                        // That is, if the user is trying to specify a single book number
                        val bookNum: Int = parseBookNum(tokens(1))
                        if (bookNum == -1) {
                            return
                        }
                        
                        println(SparkSearcher.numUniqueWords(Array(books(bookNum - 1))))
                    } else {
                        // That is, if the user is trying to say "give me the # of unique words in ALL books"
                        println(SparkSearcher.numUniqueWords(books))
                    }
                } else {
                    // Unrecognized option!
                    println("[ERROR] Could not process " + tokens(0) + ".")
                    println("Current list of recognized options: UNIQUE_WORDS.")
                }
            case other => println("[ERROR] Unrecognized command: " + other + "\nType `help' for a list of commands.")
        }
        
        if (!shouldQuit) {
            println() // trailing blank line for visual effect
        }
    }
}
