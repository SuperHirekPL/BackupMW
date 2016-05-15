<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head> <title>Rekomendacje </title>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<link rel="stylesheet" href="./css/bootstrap.min.css"/>         
<script src="./js/bootstrap.min.js"></script>   
</head>
<body>
<!-- Wstawić tabelę z wynikami rekomendacji -->
            <!--Employees List-->
           <form action="/servletManager" method="get" id="seachEmployeeForm" role="form">
                <input type="hidden" id="searchAction" name="searchAction" value="searchByName">
                <div class="form-group col-xs-5">
                    <input type="text" name="postName" id="postName" class="form-control" required="true" placeholder="Wprowadź nazwę użytkownika lub treść postu"/>                    
                </div>
                <button type="submit" class="btn btn-info">
                    <span class="glyphicon glyphicon-search"></span> Wyszukaj
                </button>
                <br></br>
                <br></br>
            </form>
            <form action="/servletManager" method="post" id="recomendationForm" role="form" >          
               
                <c:choose>
                    <c:when test="${not empty recomendationList}">
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                	<td>Id</td>
                                    <td>Grupa</td>
                                    <td>Treść</td>
                                    <td>Użytkownik</td>
                                </tr>
                            </thead>
                            <c:forEach var="recomendation" items="${recomendationList}">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${recomendation.getId() == recomendation.getId()}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>${recomendation.getId()}</td>
                                    <td>${recomendation.getGroupName()}</td>
                                    <td>${recomendation.getPost()}</td>
                                    <td>${recomendation.getUser()}</td>                                   
                                </tr>
                            </c:forEach>               
                        </table>  
                    </c:when>                    
                    <c:otherwise>
                        <br>           
                        <div class="alert alert-info">
                           Brak rekomendacji 
                        </div>
                    </c:otherwise>
                </c:choose>                        
            </form>
           
        </div>
        <table>
<tbody>

<table>
<tbody>


</body>
</html>