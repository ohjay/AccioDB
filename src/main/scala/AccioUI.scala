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
            |sentences [-b book_num] PHRASE: Returns the set of all sentences that contain PHRASE.""".stripMargin
    
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
    
    def executeCommand(command: String, tokens: Array[String], books: Array[RDD[String]]) {
        command match {
            case "quit" => shouldQuit = true
            case "help" => 
                if (tokens.length > 0) {
                    // The user presumably wants information about a specific command
                    tokens(0) match {
                        case "quit" => println("""Usage: quit
                                |Quits the program. When this happens,
                                |the prompt will disappear and all session data will be lost.""".stripMargin)
                        case "count" => println("""Usage: count [-b book_num] PHRASE
                                |If the [-b booknum] option is specified, searches the given book (#1 to #7)
                                |for PHRASE and displays the number of appearances within that one book. 
                                |Otherwise (if no -b flag), counts the number of times PHRASE occurs 
                                |throughout all seven books.
                                |
                                |Ex. usage) count -b 1 Fluffy <-- counts the # of 'Fluffy's in book 1
                                |Ex. usage) count -b 5 Harry shouted <-- counts the # of 'Harry shouted's in book 5""".stripMargin)
                        case "sentences" => println("""Usage: sentences [-b book_num] PHRASE
                                |Returns a set of all sentences that contain PHRASE. If the [-b booknum] option
                                |is specified, such sentences will be limited to the given book (#1 to #7).
                                |
                                |Ex. usage) sentences After all this time <-- we all know which sentence this will return
                                |Ex. usage) sentences -b 2 Dobby <-- outputs all sentences involving Dobby from book 2""".stripMargin)
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
                    var bookNum: Int = 0
                    try {
                        bookNum = tokens(1).toInt
                    } catch {
                        case e: Exception => println("[ERROR] book_num must be an integer.\n")
                        return
                    }
                    
                    if (bookNum < 1 || bookNum > 7) {
                        // That's not a Harry Potter book!
                        println("[ERROR] book_num must be an integer between 1 and 7.\n")
                        return
                    }
                    
                    println(SparkSearcher.count(Array(books(bookNum - 1)), tokens.drop(2).mkString(" ")))
                } else if (tokens.length == 0) {
                    println("[ERROR] Usage: count [-b book_num] PHRASE")
                } else {
                    println(SparkSearcher.count(books, tokens.mkString(" ")))
                }
            case "sentences" => println("This function is not functional yet.")
            case other => println("[ERROR] Unrecognized command: " + other + "\nType `help' for a list of commands.")
        }
        
        if (!shouldQuit) {
            println() // add an extra newline just for the aesthetics
        }
    }
}
