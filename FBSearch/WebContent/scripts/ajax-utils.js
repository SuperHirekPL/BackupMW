// Get the browser-specific request object, either for
// Firefox, Safari, Opera, Mozilla, Netscape, IE 8, or IE 7 (top entry);
// or for Internet Explorer 5 and 6 (bottom entry). 

function getRequestObject() {
  if (window.XMLHttpRequest) {
    return(new XMLHttpRequest());
  } else if (window.ActiveXObject) { 
    return(new ActiveXObject("Microsoft.XMLHTTP"));
  } else {
    // Don't throw Error: this part is for very old browsers,
    // and Error was implemented starting in JavaScript 1.5.
    return(null); 
  }
}


// Make an HTTP request to the given address. 
// Display result in an alert box.

function ajaxAlert(address) {
  var request = getRequestObject();
  request.onreadystatechange = 
    function() { showResponseAlert(request); };
  request.open("GET", address, true);
  request.send(null);
}

// Put response text in alert box.

function showResponseAlert(request) {
  if ((request.readyState == 4) &&
      (request.status == 200)) {
    alert(request.responseText);
  }
}

// Make an HTTP request to the given address. 
// Display result in the HTML element that has given ID.

function ajaxResult(address, resultRegion) {
  var request = getRequestObject();
  request.onreadystatechange = 
    function() { showResponseText(request, 
                                  resultRegion); };
  request.open("GET", address, true);
  request.send(null);
}



// Put response text in the HTML element that has given ID.

function showResponseText(request, resultRegion) {
  if ((request.readyState == 4) &&
      (request.status == 200)) {
    htmlInsert(resultRegion, request.responseText);
	 // parseToDataList(resultRegion, request);
  }
}

function  parseToDataList(resultRegion, request){
	var nameArray = JSON.parse(request.responseText);
	for (var i = 0; i < nameArray.length; i++) {
		document.getElementById(resultRegion).append("<option value='" + nameArray[i] + "'>");
	}
	
}


//Insert the html data into the element that has the specified id.

function htmlInsert(id, htmlData) {
  document.getElementById(id).innerHTML = htmlData;
}

try { console.log("Loading script"); 
} catch(e) { console = { log: function() {} }; }


function showResult(str) {
	  if (str.length==0) {
	    document.getElementById("livesearch").innerHTML="";
	    document.getElementById("livesearch").style.border="0px";
	    return;
	  }
	  if (window.XMLHttpRequest) {
	    // code for IE7+, Firefox, Chrome, Opera, Safari
	    xmlhttp=new XMLHttpRequest();
	  } else {  // code for IE6, IE5
	    xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	  xmlhttp.onreadystatechange=function() {
	    if (xmlhttp.readyState==4 && xmlhttp.status==200) {
	      document.getElementById("livesearch").innerHTML=xmlhttp.responseText;
	      document.getElementById("livesearch").style.border="1px solid #A5ACB2";
	    }
	  }
	  xmlhttp.open("GET","livesearch.php?q="+str,true);
	  xmlhttp.send();
	}

// Trick so that the Firebug console.log function will
// be ignored (instead of crashing) in Internet Explorer.
// Also see Firebug Lite and Faux Console if you want 
// logging to actually do something in IE.
// Firebug Lite: http://getfirebug.com/lite.html
// Faux Console: http://icant.co.uk/sandbox/fauxconsole/
 
