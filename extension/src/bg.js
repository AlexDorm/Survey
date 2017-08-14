//to try to launch a notification at the given date
function toHappen(func, date) {
    var now = new Date().getTime();
    var diff = date.getTime() - now;
    //alert(diff/(1000*60*60) + " hours before next notification");
    setTimeout(func, diff);
}
//https://stackoverflow.com/questions/2271156/chrome-desktop-notification-example
// request permission on page load
document.addEventListener('DOMContentLoaded', function () {
  if (Notification.permission !== "granted")
    Notification.requestPermission();
});

//launch the notification when called
function notifyMe() {
  //verify if your browser supports notifications
  if (!Notification) {
    alert('Desktop notifications not available in your browser. Try Chromium.'); 
    return;
  }

  //if you didn't allow the notifications, ask for it
  if (Notification.permission !== "granted")
    Notification.requestPermission();
  //if it's granted, notifies you
  else {
    var notification = new Notification('How is your day going ?', {
      icon: 'question2.png',
      body: "Don't forget to answer the survey today !",
    });

    //close the notification if you click on it
    notification.onclick = function () {
        notification.close();
    };
  }
}

var date = new Date(); //return the date at the moment it is called

//set the notification hour / date. Today at h or tomorrow at "h" if "h" is over
function setDate(h = 12) {
	if (date.getHours() > h) {
		date = new Date(date.getTime() + 24*60*60*1000); //set the date to the next day
		date.setHours(h); //between h & h+1
	} else {
		date.setHours(h); //set the date to today at h:(current minutes)
	}
	return date;
}

toHappen(notifyMe,setDate());