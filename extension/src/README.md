# Extension part

## Generality about extensions
You can provide an extension unpacked for testing :
- go to the extension tab : chrome://extensions/
- check the upper right box "developper mode"
- click on add an extension and select your folder on your computer

To pack it, make it a .crx file by clicking on the "Pack extension" button. It will also provide .pem file which contains the private key.
**/!\ keep it (you need it if you do any upgrade) /!\**  
see: https://developer.chrome.com/extensions/packaging for more information

## Our extension description
Icon is the icon.png the user will see in his browser while questionMark.png is the icon displayed on the desktop notification we send.

#### Manifest.json
It is the desciption of the  extension, it is in JSON format and explains how the extension works and what rights it has on the different web pages.  
The ***browser action*** is the icon the user can see in its browser that launch the extension.  
The ***background*** is a script launched at the same time as the browser itself, or the extension reload.
The ***option page*** is the page you use as option when you right click on the icon. In our case, it is for the user to log in.

#### Popup
It is the code that sets the look of the popup when you click on the application icon in the browser.
Here it is a form to let the user answer the questions or skip them if he doesn't want to.

#### Background.js
The sript that is launched at every browser beginning. We use it to send a desktop notification to the user at the hour decided in the code (between 12 & 13 as a default value).

#### Options
The option page of an extension, here we use it to log the user. If no one is logged in, you can't answer the survey, it isn't displayed.
