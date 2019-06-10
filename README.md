Score Board App
=================

Description: 
------------
The application is a simple HTTP server that keeps a scoreboard.
The user can do the following:
 * GET: /<userid>/login
    Where the user id is a positive integer. The request will return a unique session id.
      
 * POST: /<levelid>/score?sessionkey=<sessionkey>
    Where the level id is also a positive integer and the session key is the session id provided by the request above.
    
 * GET: /<levelid>/highscorelist
    Where the level id is also a positive integer. The request will return the top 15 high scores and the users that obtained them, in csv format.
    An user can have multiple entries on the scoreboard.
    
In case requests are wrong 400 errors are being returned.


Implementation details:
-----------------------
The application is split in 4 packages:
    * app
        Contains the Main class that triggers the HTTP server. The requests will be received on localhost:8081 and will be handled by the RequestHandler from the service package.
        In order to support multiple requests simultaneous, a CachedThreadPool executor is being used. This means that a new thread will be created every time a new request arrives 
        and there are no threads available to address it. If threads are not being used for more than 60 seconds, they will be killed.
    * service
        Contains the RequestHandler class. It receives the http requests parses them and then solves them by querying the ScoreKeeper or the SessionManager.
    * persistence
        Contains the following:
            * SessionManager: It keeps a list of active sessions in a PriorityQueue so that it is easy to remove the expired ones. And also a map of users and their sessions, so 
            that the session can be easily retrieved for each user. Both elements are static so that every instance of SessionManager can access them. Before each request the 
            SessionManager will clear the expired sessions.
            * ScoreKeeper: It keeps a map of levelId and a PriorityQueue with the top 15 scores for that level. The map is initialized in a lazy manner, meaning that an entry for 
            a level is created if there is at least a score for it. The PriorityQueue was chosen so that the smallest score can be easily extracted. The trade-off is that every time
            the score board is requested, elements have to be sorted. 
    * dto 
        Contains the following:
            * Session: domain class representing the session. It contains an id, a timestamp from when it was created and the id of the user that has this session. In here the 
            validation logic is present. The session class knows if the user id is not valid or that the session itself has expired.
            * ScoreEntry: domain class containing a score ant the user id that obtained it.
