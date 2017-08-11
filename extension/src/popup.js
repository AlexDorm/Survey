'use strict';

window.onload = function () {
    //display the questions only if logged, else ask for you to login and directs to options
    function loadExtension(){
        if (!localStorage.userName) {
            document.getElementById("question").innerHTML = "please go to the option page and log in (Right click on the extension --> Options)"
        } else {
            document.getElementById("choices").style.visibility = "visible";
            httpGetAsync("http://localhost:8080/web/API/Question",instantiate);
        }
    }

    // instantiate the global var with values and display first question
    function instantiate(tab) {
        questionTab = new Questions(tab);
        displayQuestion(questionTab);
    }

    //a class to manipulate the JSON tab of questions
    class Questions {
        constructor(tab) {
            this.questions = tab; //json array
            this.cursor = 0; //to know which question we're at
        }

        get cursor() {
            return this._cursor;
        }

        set cursor(i) {
            this._cursor = i;
        }

        incCursor() {
            this._cursor += 1;
        }

        get questionID() {
            return JSON.parse(this.questions)[this.cursor].QuestionID;
        }

        get question() {
            return JSON.parse(this.questions)[this.cursor].Question;
        }
    }

    //instantiate an empty global var for questions 
    var questionTab = new Questions(null);

    //post the answer given to the server then change the display (submitted)
    document.getElementById("next").onclick = function () {
        var params = "answer=" + document.querySelector("input[name='answer']:checked").value + "&userID=" + localStorage.id
                        + "&questionID=" + questionTab.questionID;
        httpPostAsynch("http://localhost:8080/web/API/Answer", params, submitted);
    }

    // disable the next button if !finished, else disable every button
    function checked(finished = false) {
        var selectArray = document.getElementsByClassName("select");
        if (!finished) {
            for (var i = 0; i < selectArray.length; i++) {
                if (selectArray[i].checked) {
                    document.getElementById("next").disabled = false;
                }
            }
        } else {
            for (var i = 0; i < selectArray.length; i++) {
                selectArray[i].disabled = true;
            }
            document.getElementById("next").disabled = true;
            document.getElementById("answers").clickable = false;
        }
    }
    
    document.getElementById("answers").onclick = function () {
        checked();
    }

    //executed when the post request has been approved
    function submitted() {
        //if there are more questions, we change the display
        if (Object.keys(questionTab).length > (questionTab.cursor+1)) {
            questionTab.incCursor();
            displayQuestion(questionTab);
        } else {
            checked(true);
        }
        //disable the buttons clicked on the previous question
        document.querySelector("input[name='answer']:checked").checked = false;
        document.getElementById("next").disabled = true;
    }

    //takes the JSON of questions and display the current question in extension
    function displayQuestion(questionTab) { 
        var question = document.getElementById("question");
        var text = questionTab.question;
        question.innerHTML = text;
    }

    //sends the GETrequest and return the result to the callback function
    function httpGetAsync(url, callback) { 
        var xmlHttp = new XMLHttpRequest();
        xmlHttp.onreadystatechange = function() { 
            if (xmlHttp.readyState == 4 && xmlHttp.status == 200) 
                callback(xmlHttp.responseText);
        }
        xmlHttp.open("GET", url, true); // true for asynchronous 
        xmlHttp.send();
    }

    //does a post request on the given url with given parmas and callback
    function httpPostAsynch (url, params, callback) {
    var xHttp = new XMLHttpRequest();
    xHttp.onreadystatechange = function() {//Call a function when the state changes.
        if(this.readyState == 4 && this.status == 200) {
            callback();
        }
    }
    xHttp.open("POST", url, true);
    xHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xHttp.send(params);
}

    loadExtension();
}


/*
* TODO :
    - look at how to create a desktop notification even if browser closed
    - do something for the login servlet and encoding password, yet we have no connection for this part to DB
*/