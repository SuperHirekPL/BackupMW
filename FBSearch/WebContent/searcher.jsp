<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head> <title>Wyszukiwarka facebooka </title>
<script src="./scripts/ajax-utils.js" type="text/javascript"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script src="./scripts/fblogin.js" type="text/javascript"></script>
<link rel="stylesheet" href="./css/bootstrap.min.css"/>         
<script src="./js/bootstrap.min.js"></script>   
</head>
<body>

 
 <button id="logOut" onclick="logOut()" type="button">Wyloguj</button> 
 <div id="status"></div>
 <div id="timeResult1" class="ajaxResult"></div>
 
 <fieldset>
  <legend>Wyszukiwarka </legend>
  <input  type="text" size="30" onkeyup='ajaxResult("FBMenager", "timeResult1")'/>
</fieldset>



<form>
    <div class="well carousel-search hidden-sm">
        <div class="btn-group"> <a class="btn btn-default dropdown-toggle btn-select" data-toggle="dropdown" href="#">Wybierz działanie <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <li><a href="#">Algorytmy rekomendacji</a></li>
<!--                 <li><a href="#">Canada</a></li> -->
                <li class="divider"></li>
            </ul>
        </div>
        <div class="btn-group"> <a class="btn btn-default dropdown-toggle btn-select2" data-toggle="dropdown" href="#">Wybierz algorytm <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <li><a href="./">Metryka miejska</a></li>
                <li><a href="#">Metryka Euklidesowa</a></li>
                <li><a href="#">Współczynnik podobieństwa Log Likelihood'a</a></li>
                <li><a href="#">Współczynnik podobieństwa Tanimoto</a></li>
                <li><a href="#">Współczynnik podobieństwa Persona</a></li>
                <li><a href="#">Wpsoółczynik podobieństwa Cosine-based</a></li>
                <li class="divider"></li>
                </ul>
        </div>
        <div class="btn-group">
            <button type="button" id="btnSearch" class="btn btn-primary">Wykonaj analize</button>
        </div>
    </div>
</form>


<div class="container">
            <h2>Employees</h2>
            <!--Search Form -->
            <form action="/employee" method="get" id="seachEmployeeForm" role="form">
                <input type="hidden" id="searchAction" name="searchAction" value="searchByName">
                <div class="form-group col-xs-5">
                    <input type="text" name="employeeName" id="employeeName" class="form-control" required="true" placeholder="Type the Name or Last Name of the employee"/>                    
                </div>
                <button type="submit" class="btn btn-info">
                    <span class="glyphicon glyphicon-search"></span> Search
                </button>
                <br></br>
                <br></br>
            </form>
            
            <!--Employees List-->
            <form action="/employee" method="post" id="employeeForm" role="form" >              
               
                <c:choose>
                    <c:when test="${not empty employeeList}">
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>#</td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td>Role</td>
                                    <td>Department</td>
                                    <td>E-mail</td>                                 
                                </tr>
                            </thead>
                            <c:forEach var="employee" items="${employeeList}">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${idEmployee == employee.id}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>${employee.id}</td>
                                    <td>${employee.name}</td>
                                    <td>${employee.lastName}</td>
                                    <td>${employee.birthDate}</td>
                                    <td>${employee.role}</td>
                                    <td>${employee.department}</td>
                                    <td>${employee.email}</td>   
                                   
                                </tr>
                            </c:forEach>               
                        </table>  
                    </c:when>                    
                    <c:otherwise>
                        <br>           
                        <div class="alert alert-info">
                            No people found matching your search criteria
                        </div>
                    </c:otherwise>
                </c:choose>                        
            </form>
           
        </div>
        
        <!-- Search box Start -->
<form>
    <div class="well carousel-search hidden-sm">
        <div class="btn-group"> <a class="btn btn-default dropdown-toggle btn-select" data-toggle="dropdown" href="#">Select a Country <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <li><a href="#">United States</a></li>
                <li><a href="#">Canada</a></li>
                <li class="divider"></li>
                <li><a href="#"><span class="glyphicon glyphicon-star"></span> Other</a></li>
            </ul>
        </div>
        <div class="btn-group"> <a class="btn btn-default dropdown-toggle btn-select2" data-toggle="dropdown" href="#">Select a Region <span class="caret"></span></a>
            <ul class="dropdown-menu">
                <li><a href="#">California</a></li>
                <li><a href="#">New York</a></li>
                <li><a href="#">Massachusetts</a></li>
                <li class="divider"></li>
                <li><a href="#">Quebec</a>
                </li><li><a href="#">Ontario</a>
                </li><li><a href="#">British Columbia</a>
                </li>
            </ul>
        </div>
        <div class="btn-group">
            <button type="button" id="btnSearch" class="btn btn-primary">Search</button>
        </div>
    </div>
</form>
<!-- Search box End -->



 
<!-- <fieldset> -->
<!--   <legend>Wyszukiwarka </legend> -->
<!--   <input id="mainSearch" list ="searchresults" size="30" onkeyup='ajaxResult("search", "searchresults")'/> -->
  
<!-- </fieldset> -->

<!-- <input list="searchresults"> -->

<!-- <datalist id="searchresults"></datalist> -->


</body>
</html> 