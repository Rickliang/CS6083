/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.Query;
import twitter4j.QueryResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.List;

/**
 * Example application that uses OAuth method to acquire access to your account.<br>
 * This application illustrates how to use OAuth method with Twitter4J.<br>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class updateStatus {
    /**
     * Usage: java twitter4j.examples.tweets.UpdateStatus [text]
     *
     * @param args message
     */
	private Twitter twitter;
	private Connection conn;
	private String dbURL = "jdbc:mysql://localhost:3306/tweetDB";
	private String dbUser = "root";
	private String dbPass = "1718fresh";
	
	
	public void getTweetByQuery(boolean loadRecords, String keyword) throws InterruptedException {

		
        if(conn != null) {
		try {			
			Query query = new Query(keyword);
			query.setCount(100);
			QueryResult result;
			result = twitter.search(query);
			System.out.println("Getting Tweets...");
			List<Status> tweets = result.getTweets();
			
			for(Status tweet : tweets) {
				try{
					
				    String sql = "INSERT INTO tweets (user_name, retweet_count, tweet_followers_count, source, tweet_mentioned_count, tweet_ID, tweet_text) values (?, ?, ?, ?, ?, ?,?)";
				    PreparedStatement statement = conn.prepareStatement(sql);
				    statement.setString(1, tweet.getUser().getScreenName());
				    statement.setInt(2, tweet.getRetweetCount());
				    statement.setInt(3, tweet.getUser().getFollowersCount());
				    statement.setString(4, tweet.getSource());
				    UserMentionEntity[] mentioned = tweet.getUserMentionEntities();
				    statement.setInt(5, mentioned.length);
				    String s = (String.valueOf(tweet.getId()));
				    statement.setString(6, s);
				    statement.setString(7, tweet.getText());
				    
				    int row = statement.executeUpdate();
					if (row > 0) {
						System.out.println("tweets saved into database");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
		} catch(TwitterException te){
			 System.out.println("te.getErrorCode() " + te.getErrorCode());
             System.out.println("te.getExceptionCode() " + te.getExceptionCode());
             System.out.println("te.getStatusCode() " + te.getStatusCode());
             if (te.getStatusCode() == 401) {
                 System.out.println("Twitter Error : \nAuthentication credentials (https://dev.twitter.com/pages/auth) were missing or incorrect.\nEnsure that you have set valid consumer key/secret, access token/secret, and the system clock is in sync.");
             } else {
                 System.out.println("Twitter Error : " + te.getMessage());
             }
		}
        }else{
        	System.out.println("Mysql is not Connected! Please check Mysql intance running..");
        }
	}
	
	public void loadMenu() throws InterruptedException, IOException, TwitterException {
		System.out.print("Please choose your Keyword:\t");
		
		Scanner input = new Scanner(System.in);
		String keyword = input.nextLine();
		
		conn = null;	// connection to the database
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
			conn.close();
			conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		twitter = new TwitterFactory().getInstance();
		try {
            // get request token.
            // this will throw IllegalStateException if access token is already available
            RequestToken requestToken = twitter.getOAuthRequestToken();
            System.out.println("Got request token.");
            System.out.println("Request token: " + requestToken.getToken());
            System.out.println("Request token secret: " + requestToken.getTokenSecret());
            AccessToken accessToken = null;

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (null == accessToken) {
                System.out.println("Open the following URL and grant access to your account:");
                System.out.println(requestToken.getAuthorizationURL());
                System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
                String pin = br.readLine();
                try {
                    if (pin.length() > 0) {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = twitter.getOAuthAccessToken(requestToken);
                    }
                } catch (TwitterException te) {
                    if (401 == te.getStatusCode()) {
                        System.out.println("Unable to get the access token.");
                    } else {
                        te.printStackTrace();
                    }
                }
            }
            System.out.println("Got access token.");
            System.out.println("Access token: " + accessToken.getToken());
            System.out.println("Access token secret: " + accessToken.getTokenSecret());
        } catch (IllegalStateException ie) {
            // access token is already available, or consumer key/secret is not set.
            if (!twitter.getAuthorization().isEnabled()) {
                System.out.println("OAuth consumer key/secret is not set.");
                System.exit(-1);
            }
        }
		int count = 0;
		while (count != 100) {
			
			getTweetByQuery(true, keyword);
			
			Thread.sleep(1000);
			count ++;
			System.out.println(count + "saved");
		}
		if (conn != null) {
			// closes the database connection
			try {
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
    public static void main(String[] args) throws InterruptedException, IOException, TwitterException {
    	
    	updateStatus taskObj = new updateStatus();
    	taskObj.loadMenu();
//        try {
//            
//            try {
//                // get request token.
//                // this will throw IllegalStateException if access token is already available
//                RequestToken requestToken = twitter.getOAuthRequestToken();
//                System.out.println("Got request token.");
//                System.out.println("Request token: " + requestToken.getToken());
//                System.out.println("Request token secret: " + requestToken.getTokenSecret());
//                AccessToken accessToken = null;
//
//                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                while (null == accessToken) {
//                    System.out.println("Open the following URL and grant access to your account:");
//                    System.out.println(requestToken.getAuthorizationURL());
//                    System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
//                    String pin = br.readLine();
//                    try {
//                        if (pin.length() > 0) {
//                            accessToken = twitter.getOAuthAccessToken(requestToken, pin);
//                        } else {
//                            accessToken = twitter.getOAuthAccessToken(requestToken);
//                        }
//                    } catch (TwitterException te) {
//                        if (401 == te.getStatusCode()) {
//                            System.out.println("Unable to get the access token.");
//                        } else {
//                            te.printStackTrace();
//                        }
//                    }
//                }
//                System.out.println("Got access token.");
//                System.out.println("Access token: " + accessToken.getToken());
//                System.out.println("Access token secret: " + accessToken.getTokenSecret());
//            } catch (IllegalStateException ie) {
//                // access token is already available, or consumer key/secret is not set.
//                if (!twitter.getAuthorization().isEnabled()) {
//                    System.out.println("OAuth consumer key/secret is not set.");
//                    System.exit(-1);
//                }
//            }
//            Status status = twitter.updateStatus("test again");
//            System.out.println("Successfully updated the status to [" + status.getText() + "].");
//            System.exit(0);
//        } catch (TwitterException te) {
//            te.printStackTrace();
//            System.out.println("Failed to get timeline: " + te.getMessage());
//            System.exit(-1);
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//            System.out.println("Failed to read the system input.");
//            System.exit(-1);
//        }
    }
}
