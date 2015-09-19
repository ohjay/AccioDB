# AccioDB
AccioDB is an easy-to-use Scala interface for Harry Potter book data, conceived as an exercise in both Scala and MapReduce.

## Features; Implementation
AccioDB runs as a command line UI. Accordingly, in order to interact with it the user must enter commands (and sometimes arguments). Using these, the user can acquire information such as "the number of times Neville is mentioned in the story" or "all quotes by Albus Dumbledore". Useful, don't you think? (If you don't, I pity you... along with those who live without love.)

Full list of current commands (of course, more can always be added!):

- help
- quit

The program uses Apache Spark to process [the book text](#disclaimer), with the aim that users will be able to see results faster than they can say "Quidditch".

## Disclaimer
Due to a bunch of legal stuff that I'm not about to get into, I'm not allowed to upload the actual Harry Potter books here. Thus in order for you to use AccioDB yourself, you would have to either **(a)** find the .txt files yourself online, or **(b)** type up the text of all seven books yourself. Sorry!

## Demo Video
[coming soon! For now, enjoy the video below]

<a href="http://www.youtube.com/watch?feature=player_embedded&v=-ZsIiuuAACw
" target="_blank"><img src="http://img.youtube.com/vi/-ZsIiuuAACw/0.jpg" 
alt="AccioDB demo video" width="240" height="180" border="10" /></a>
