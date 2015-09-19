import java.io.BufferedReader

object AccioUI {
    var shouldQuit: Boolean = false
    var bookFilepath: String = "harry_potter_bookset"
    val helpStr: String = """quit: Quits the program.
            |help: Provides the list of commands that you're seeing right now.""".stripMargin
    
    def main(args: Array[String]) {
        println("Welcome to Accio!")
        print("Would you like to specify your own path to the book data? [y/n]: ")
        
        val br: BufferedReader = Console.in
        var line: String = br.readLine()
        while (line != "y" && line != "n") {
            print("Please enter 'y' or 'n': ")
            line = br.readLine()
        }
        
        // Either get the user's filepath or indicate usage of the default one
        if (line == "y") {
            print("Enter a filepath: ")
            var bookFilepath = br.readLine()
            println("Okay, we'll use \"" + bookFilepath + "\" as the filepath.")
        } else {
            println("Okay, we'll use the default filepath (" + bookFilepath + ").")
        }
        
        println("\nStarting up AccioDB; please wait......")
        println("AccioDB launched! Remember, help will always be given at Hogwarts to those who ask for it.")
        
        while (!shouldQuit) {
            print("> ")
            line = br.readLine()
            var tokens: Array[String] = line.split(" ")
            var command: String = tokens(0)
            tokens = tokens.drop(1)
            
            executeCommand(command, tokens)
        }
    }
    
    def executeCommand(command: String, tokens: Array[String]) {
        command match {
            case "quit" => shouldQuit = true
            case "help" => println(helpStr)
            case other => println("Unrecognized command: " + other + "\nType \"help\" for a list of commands.")
        }
        
        if (!shouldQuit) {
            println() // add an extra newline just for the aesthetics
        }
    }
}
