# AccioDB [v1.1]
AccioDB is an easy-to-use query interface for Harry Potter book data, conceived as an exercise in both Scala programming and Apache Spark's distributed processing facilities.

## 1. Features; Implementation
AccioDB runs as a command line UI. Accordingly, in order to interact with it the user must enter commands (and sometimes arguments). Using these, the user can acquire information such as "the number of times Neville is mentioned in the story" or "all quotes by Albus Dumbledore". Useful, don't you think? If you _don't_ think, I pity you... along with all those who live without love.

Full list of current commands (of course, more can always be added!):<br>
```
quit
help [COMMAND]
count [-b book_num] PHRASE
word_dist [-a] [-n top_n_words] BOOK_NUM
stat STAT_OPTION
```

The program uses Apache Spark to process [the book text](#3-disclaimer), with the aim that users will be able to see results faster than they can say "Quidditch".

## 2. Usage Notes
### 2.1 Launching AccioDB
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

### 2.2 A Guide to the Commands
Once you have done this, AccioDB will run a bit of Spark setup and then you will be prompted to enter a command. You can type `help` to get a listing, or otherwise refer to the usage guide below:

<table width="100%">
    <tr><th colspan="2" align="left">count [-b book_num] PHRASE</th></tr>
    <tr>
        <td rowspan="3" width="42%">Counts the number of times PHRASE appears in all seven books (or, optionally, a single book). This might be used as a rough measure of significance for a word/phrase (as a benchmark, the word "Harry" appears 18165 times in 15701 lines).</td>
        <td width="58%"><code>count Quidditch # counts the number of times "Quidditch" is mentioned (output: 421)</code></td>
    </tr>
    <tr><td width="58%"><code>count the Whomping Willow # counts the number of occurrences of "the Whomping Willow" (output: 20)</code></td></tr>
    <tr><td width="58%"><code>count -b 5 Harry yelled # counts the number of times Harry yells in book 5 (output: 14)</code></td></tr>
    <tr><th colspan="2" align="left">stat STAT_OPTION</th></tr>
    <tr>
        <td width="42%">Prints statistics related to the book text. Choice of statistic is dependent on STAT_OPTION.</td>
        <td width="58%"><code>stat UNIQUE_WORDS 6 # identifies the number of unique words in HBP (output: 12569)</code></td>
    </tr>
    <tr><th colspan="2" align="left">word_dist [-a] [-n limit] BOOK#</th></tr>
    <tr>
        <td rowspan="3" width="42%">Lists the most or least common words in a book. Users can specify a limit for the number of words displayed; this limit defaults to 200. To survey the entire set of books, "all" should be passed in as BOOK#.</td>
        <td width="58%"><code>word_dist -n 20 3 # lists the 20 most common words in PoA</code></td>
    </tr>
    <tr><td width="58%"><code>word_dist -an 9 2 # lists the 9 least common words in CoS</code></td></tr>
    <tr><td width="58%"><code>word_dist all # lists the 200 most common words in books 1-7</code></td></tr>
    <tr><th colspan="2" align="left">quit</th></tr>
    <tr>
        <td width="42%">Quits the program.</td>
        <td width="58%"><code>quit # guess what this does?</code></td>
    </tr>
    <tr><th colspan="2" align="left">help [COMMAND]</th></tr>
    <tr>
        <td width="42%">Provides a list of commands/arguments.</td>
        <td width="58%"><code>help stat # displays documentation for the stat command</code></td>
    </tr>
</table>

## 3. Disclaimer
Due to a bunch of legal stuff that I'm not about to get into, I'm not allowed to upload the actual Harry Potter books here. Thus in order for you to use AccioDB, you would have to either **(a)** find the `.txt` files online, or **(b)** type up the text of all seven books yourself. Sorry!

## 4. Bugs
If you find any [Rita Skeeters](https://www.hp-lexicon.org/images/chapters/gf/c37--the-beginning.jpg) in my program, please let me know! It would be much appreciated.
