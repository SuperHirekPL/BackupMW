<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head> <title>Wyszukiwarka facebooka </title>
<script src="./scripts/ajax-utils.js" type="text/javascript"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="./scripts/fblogin.js" type="text/javascript"></script>

</head>
<body>

 <button id="logOut" onclick="logOut()" type="button">Wyloguj</button> 
 <div id="status"></div>
 <div id="timeResult1" class="ajaxResult"></div>
 
 <fieldset>
  <legend>Wyszukiwarka </legend>
  <input  type="text" size="30" onkeyup='ajaxResult("FBMenager", "timeResult1")'/>
</fieldset>



 
<!-- <fieldset> -->
<!--   <legend>Wyszukiwarka </legend> -->
<!--   <input id="mainSearch" list ="searchresults" size="30" onkeyup='ajaxResult("search", "searchresults")'/> -->
  
<!-- </fieldset> -->

<!-- <input list="searchresults"> -->

<!-- <datalist id="searchresults"></datalist> -->


</body>
</html> 