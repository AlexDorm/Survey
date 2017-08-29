## **Here are the basis on how to set up the WM and server**

First of all, we are not installing anything on the VM directly. We use *Docker* to do it for us. It kind of emulates our programs (MySQL, Tomcat..) and make them work together without anything to configure directly.

I tried to configure and install docker but I have a proxy problem that should be resolved sometime soon.  

### What is important ?
*docker is explained after, in this part you have the global explanation*
#### Where to put the files
When the Tomcat files will be created, you need to put your server in *$CATALINA_HOME/webapps/* (CATALINA_HOME is an environment var from Tomcat).  
Here you have the root of every server you then have to create a folder with the name you want. That way you can access your server from *localhost:8080/[folderName]*.

#### How to order them
After you created your folder, you have to order your files inside in a specific way if you want Tomcat to recognize that it is a web server/application.

![Folder organisation scheme](https://i.stack.imgur.com/oxDGM.gif "Folder organisation")


* ***web.xml*** is the deployment descriptor file. It gives to the browser the description of the project with urls etc.
* ***lib*** folder is where you put all the libraries files needed for the server in *.jar*.
* ***classes*** folder is where you put the compiled Java classes from the server.

### Set up docker environment
To install docker, you can follow their official tutorial:
https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/

Once this is done, you have to "install" the technologies you need (here MySQL & Tomcat). For this, create two files:
* Dockerfile
* docker-compose.yml

The first one tells if you need to copy some of the files on your machine onto docker.
The second is to set up the whole environment and install what you need to make the server work.

#### Dockerfile
    FROM tomcat:latest
    WORKDIR $CATALINA_HOME/webapps/
    COPY . $WORKDIR
    EXPOSE 8080
This is the Dockerfile used on my laptop, it sets up some folder from my computer to the docker image of Tomcat.  
With more details, I declare the WORKDIR variable as the root of the server, then I copy my app folder in it to get the correct path. Finally, we tell docker to let the port 8080 open for the outside (us) to be able to connect to the server.

#### docker-compose.yml
    version: "2"
    services:
        tomcat:
            build: .
            ports:
                - "8080:8080"
            restart: on-failure
        db:
            image: mysql
            environment:
                MYSQL_ROOT_PASSWORD: admin
                MYSQL_DATABASE: app_db
                MYSQL_USER: app_user
                MYSQL_PASSWORD: app_pwd
            ports:
                - "3306:3306"
        phpmyadmin:
            image: phpmyadmin/phpmyadmin
            container_name: phpmyadmin
            environment:
                - PMA_ARBITRARY=1
            restart: always
            ports:
                - "80:80"
This docker compose sets up the environment necessary to use three programs (Tomcat, mysql, phpmyadmin). On the server, we won't need phpmyadmin because we don't want to manage the DB from inside the server.
It creates the rules on each service for the restart, and redirects ports if needed (not here).

#### How to launch docker

As I precised in the docker compose to build on ".", you have to get the two files in the same directory if you want it to work. Then type ***docker-compose up --build*** to launch docker and its services.  
It will normally launch and you can see from your the server the services you asked for at the normal addresses (for example you should have tomcat launched on *localhost:8080/[appName]*).
