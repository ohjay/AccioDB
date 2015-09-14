import java.io.BufferedReader

object AccioUI {
    def main(args: Array[String]) {
        println("Welcome to Accio!")
        print("Would you like to specify your own path to the book data? [y/n]: ")
        
        val br : BufferedReader = Console.in
        var line : String = br.readLine()
        while (line != "y" && line != "n") {
            print("Please enter 'y' or 'n': ")
            line = br.readLine()
        }
        
        println("You entered " + line + ".")
    }
}
