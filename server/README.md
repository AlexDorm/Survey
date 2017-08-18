# Server part

## Dependencies

The tomcat/Servlet server needs some librairies to work correctly linked to the database, see below to know which you need to install if you want to run it properly:

- servlet-api-2.4.jar
- javax.json-1.0.jar
- mysql-connector-java-6.0.6.jar

(Some more librairies will be needed if you want to run JSP pages, jstl-1.2.jar for example.)

#### servlet-api-2.4.jar

This librairy is used to allow the Servlet API, that way you can use every component of the Servlet as HttpServlet and its different methods.

#### javax.json-1.0.jar

This is a personnal choice, indeed the server responses we send are in JSON format. It is a quite common format, it is easy to process with JavaScript (for chrome extensions) or Android...

#### mysql-connector-java-6.0.6.jar

As we chose to develop with a MySQL database, we needed a package able to connect our server to the database for the different requests.

## Server working

The server got an API for the different platforms to access the data from the database. It has three different access point:
* API/Users
* API/Questions
* API/Answers

For each of the above urls, you can send four types of HTTP requests (GET, POST, PUT, DELETE) and get quite the same result and functioning.

The GET request will send the whole list of what was asked to the server as a JSON object.  
The POST will add your value to the database if it respects the attended format.  
The PUT request will modify something already existing but needs an authentication.  
The DELETE request finally will remove what you've asked from the database.
