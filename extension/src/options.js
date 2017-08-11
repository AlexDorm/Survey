"use strict";
window.onload = function () {
    document.getElementById("loginButton").onclick = function () {
        var userName = document.getElementById("userName").value;
        var password = document.getElementById("password").value;
        var params = "passName=" + userName + "&password=" + password;
        if (userName !== null && password !== null) {
            httpPostAsynch("http://localhost:8080/web/API/Login", params, login);
        } else {
            alert("Please enter a user name or password !")
        }
    }

    document.getElementById("logoutButton").onclick = function () {
        localStorage.removeItem("userName");
        localStorage.removeItem("id")
        chrome.tabs.getCurrent(function (tab) {
            chrome.tabs.update(tab.id,{url:tab.url});
        });
    }

    function logged() {
        if(localStorage.userName) {
            document.getElementsByTagName("p")[0].innerHTML =  "Currently logged in as " + localStorage.userName;
            document.getElementById("userName").placeholder = localStorage.userName;
        } else {
            document.getElementsByTagName("p")[0].innerHTML =  "Please log in...";
        }
    } 
    logged();
}

//does a post request on the given url with given parmas and callback with the server answer
function httpPostAsynch (url, params, callback) {
    var xHttp = new XMLHttpRequest();
    xHttp.onreadystatechange = function() {//Call a function when the state changes.
        if(this.readyState == 4 && this.status == 200) {
            callback(this.responseText);
        }
    }
    xHttp.open("POST", url, true);
    xHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xHttp.send(params);
}

function login (jsonUser) {
    try {
        localStorage.id = JSON.parse(jsonUser)[0].userID;
        localStorage.userName = JSON.parse(jsonUser)[0].name;
    } catch (err) {
        alert(jsonUser);
    } finally {
        chrome.tabs.getCurrent(function (tab) {
            chrome.tabs.update(tab.id,{url:tab.url});
        });
    }
}