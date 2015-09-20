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
            |help: Displays the list of commands that you're seeing right now.
            |count [phrase]: Counts the number of times the given phrase appears throughout all seven books.""".stripMargin
    
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
            case "help" => println(helpStr)
            case "count" => 
                if (tokens.length == 0) {
                    println("[ERROR] Usage: count [phrase]")
                } else {
                    println(SparkSearcher.count(books, tokens.mkString(" ")))
                }
            case other => println("[ERROR] Unrecognized command: " + other + "\nType \"help\" for a list of commands.")
        }
        
        if (!shouldQuit) {
            println() // add an extra newline just for the aesthetics
        }
    }
}
