# AccioDB
AccioDB is an easy-to-use query interface for Harry Potter book data, conceived as an exercise in both Scala programming and Apache Spark's distributed processing facilities.

## Features; Implementation
AccioDB runs as a command line UI. Accordingly, in order to interact with it the user must enter commands (and sometimes arguments). Using these, the user can acquire information such as "the number of times Neville is mentioned in the story" or "all quotes by Albus Dumbledore". Useful, don't you think? If you _don't_ think, I pity you... along with all those who live without love.

Full list of current commands (of course, more can always be added!):<br>
```
quit
help [COMMAND]
count [-b book_num] PHRASE
word_dist [-a] [-n top_n_words] BOOK_NUM
stat STAT_OPTION
```

The program uses Apache Spark to process [the book text](#disclaimer), with the aim that users will be able to see results faster than they can say "Quidditch".

## Usage Notes
### Launching AccioDB:
From the AccioDB directory, enter these commands in quick succession:

```shell
sbt package
[SPARK HOME DIRECTORY]/bin/spark-submit --class "AccioUI" --master local[4] target/scala-2.10/acciodb_2.10-1.0.0.jar
```

_Note: you will need to have Scala 2.10 installed. You will also need to substitute_ `[SPARK HOME DIRECTORY]` _above with a path to the Spark directory on your computer (for example,_ `~/spark-1.5.0`_)._

___

Alternatively, enter **these** commands in quick succession. You'll be prompted to enter a path to your Spark directory:

```shell
make compile
make qlaunch
```

### A guide to the commands:
Once you have done this, AccioDB will run a bit of Spark setup and then you will be prompted to enter a command. You can type `help` to get a listing, or otherwise refer to the usage guide below:

- **quit**
  - Quits the program. 
- **help [COMMAND]**
  - Provides a list of commands/arguments.
- **count [-b book_num] PHRASE**
  - Counts the number of times PHRASE appears in all seven books (or, optionally, a single book). This might be used as a rough measure of significance for a word/phrase (as a benchmark, the word "Harry" appears 18165 times in 15701 lines).
  - Example usage #1: `count Quidditch # counts the number of times "Quidditch" is mentioned (output: 421)`
  - Example usage #2: `count the Whomping Willow # counts the number of occurrences of "the Whomping Willow" (output: 20)`
  - Example usage #3: `count -b 5 Harry yelled # counts the number of times Harry yells in book 5 (output: 14)`
- **word_dist [-a] [-n limit] BOOK#**
  - Lists the most or least common words in a book.
  - Example usage #1: `word_dist -n 20 3 # lists the 20 most common words in PoA`
  - Example usage #2: `word_dist -an 9 2 # lists the 9 least common words in CoS`
  - Example usage #3: `word_dist all # lists the 200 most common words in books 1-7`
- **stat STAT_OPTION**
  - Prints statistics related to the book text. Choice of statistic is dependent on the STAT_OPTION input.
  - Example usage #1: `stat UNIQUE_WORDS 6 # outputs the number of unique words in HBP (output: 12569)`

## Pictures (sorry, they don't move)
![alt text](../master/readme_imgs/word_dist_n206.png "Sample output for AccioDB")

## Disclaimer
Due to a bunch of legal stuff that I'm not about to get into, I'm not allowed to upload the actual Harry Potter books here. Thus in order for you to use AccioDB, you would have to either **(a)** find the `.txt` files online, or **(b)** type up the text of all seven books yourself. Sorry!

## Bugs
If you find any [Rita Skeeters](https://www.hp-lexicon.org/images/chapters/gf/c37--the-beginning.jpg) in my program, please let me know! It would be much appreciated.
