<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>System rekomendacji</title>
</head>
<body>
<!-- Wstawić tabelę z wynikami rekomendacji -->
            <!--Employees List-->
            <form action="/doRecomendation" method="post" id="recomendationForm" role="form" >              
               
                <c:choose>
                    <c:when test="${not empty userRecomendation}">
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>Grupa</td>
                                    <td>Treść</td>
                                    <td>Użytkownik</td>
                                    
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


</body>
</html>