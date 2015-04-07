<?php
/**
* 140dev_config.php
* Constants for the entire 140dev Twitter framework
* You MUST modify these to match your server setup when installing the framework
* 
* Latest copy of this code: http://140dev.com/free-twitter-api-source-code-library/
* @author Adam Green <140dev@gmail.com>
* @license GNU Public License
* @version BETA 0.30
*/

// OAuth settings for connecting to the Twitter streaming API
// Fill in the values for a valid Twitter app
define('TWITTER_CONSUMER_KEY','bPRnzhcLJxr1gSgcpDWXeXSm3');
define('TWITTER_CONSUMER_SECRET','gz4WCIGj7lGVKbdA8imqTwntXO86yuhGks75m0NLO5kRDlplcD');
define('OAUTH_TOKEN','1068719712-GblrfVA2vneQH6gKvcMiw99ha9bTmfTpfCkBjEQ');
define('OAUTH_SECRET','bpGM0csnYnXwNTdZHww3wVgffNO5Omz6V0P8o5pAXW7KU');

// Settings for monitor_tweets.php
define('TWEET_ERROR_INTERVAL',10);
// Fill in the email address for error messages
define('TWEET_ERROR_ADDRESS','hg1068@nyu.edu');
?>