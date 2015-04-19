# CS6083

Database Project

Instruction:

1.Make sure you already have a php server localhost.

2.Create the databse, the database and table creating sql already contained in the db folder of the root directory. The CREATE TABLE commands for these tables are stored in the file mysql_database_schema.sql in db folder. Run it in your local database and everything would be fine.
  
3.Then open the terminal and run following two command(in order):
  Commands With error log output:
  
  nohup php ./get_tweets.php &
  
  nohup php ./parse_tweets.php &
  
  Or commands Without error log output:
  
  nohup php get_tweets.php > /dev/null &
  
  nohup php parse_tweets.php > /dev/null &
  
4.Input following link in your browser to run your code
  http://yoursite.com/Twitter_adapter_php/plugins/twitter_display/index.html
  
That's it! Happy coding!
  
