# NETS-150-Final-Project

This program will calculate the G.O.A.T. player of basketball by scraping through the teams provided by a bleacher report article that provided the top teams from each franchise. We were then able to move to a basketball statistics website (basketballreference) which would provide us with the statistics that we would use in our calculations, all of which was scraped using Jsoup and regular expressions. 

The way our calculator works is it first creates an adjacency list which represents a directed graph of all of the top 5 "per-game" performers from every team. There exists an outgoing edge from a player to a player that they beat. 

For example: If Michael Jordan beat Patrick Ewing at some point in the year 1996:
MJ -> Patrick Ewing. 

The winningest 5 players all receive 50 points, the next 10 players receive 47, and we continue to allocate points to players depending on where they rank based off the graph. 

We then distribute more points per team to highlight individual performance during that year, again by an analysis of the Per-Game statistics. We look at 5 statistics: assists, points, field-goal %, total rebounds, free-throw percentage and rank the 5 players for each statistic. 1st place in every statistic receives 10 points, 2nd: 8 points, 3rd: 6 points, and so on. This way, even if a team is outperformed by another, if an individual led the team in all 5 statistical categories, he will still receive a large amount of points. 

The parse class contains all of the web-scraping.
The main class allows for the user to return whichever number (up to 140) of players from highest rank to lowest rank through the function topXPlayers (but overall shouldn't be touched). 
The PlayerEntry class contains our new type which allowed for us organize our players in descending order based off of points. 
The statistics class initiated the statistics scrape and allocated points per player and handled the calculations. 

NOTES: 
1. We removed the Hornets teams from the list because there was no way to distinguish between them. The same "city code" wasn't applicable for both, so we removed them. We were safe to do so, however, because either team, it could safely be assumed, did not have a goat. 
2. Because the bleacher report website page was created in 2014, before a lot of influential teams (2016 Warriors, 2018 Raptors) played, we updated the years of some teams to more accurately calculate the answer.   