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
<script type="text/javascript">
                       
    $(document).ready(function(){
        $('#itemRecTable tbody').on( 'click', 'td', function () {
        	var $row = $(this).closest("tr");
        	$tds = $row.find("td:nth-child(4)");
        	var idObjFb = "";
        	$.each($tds, function() {                
        	    idObjFb = $(this).text();         
        	});
        	window.location.href = "https://www.facebook.com/"+idObjFb;

        });
    });
</script>

<script type="text/javascript">
$(document).ready(function(){
$("#searcher").on("keyup", function() {
    var value = $(this).val();

    $("table tr").each(function(index) {
        if (index !== 0) {

            $row = $(this);

            var id = $row.find("td").slice(0,-1).text().toLowerCase();
            

            if (id.indexOf(value.toLowerCase()) > -1) {
                $row.show();
            }
            else {
                $row.hide();
            }
        }
    });
});
});


</script>
</head>

<body>
<!-- Wstawić tabelę z wynikami rekomendacji -->
           <div class="container">
                 <h2>Rekomendacje</h2>
<!--            <form action="/servletManager" method="get" id="seachEmployeeForm" role="form"> -->
<!--                 <input type="hidden" id="searchAction" name="searchAction" value="searchByName"> -->
                
                <div class="form-group col-xs-5">
                    <input type="text" id="searcher"  class="form-control" placeholder="Wprowadź nazwę użytkownika lub treść postu"/>                    
                </div>
                <button type="submit" class="btn btn-info">
                    <span class="glyphicon glyphicon-search"></span> Wyszukaj
                </button>
                <br></br>
                <br></br>
<!--             </form> -->
            
            <form action="/servletManager" method="post" id="recomendationForm" role="form" >          
               
                <c:choose>
                    <c:when test="${not empty recomendationList}">
                        <table  class="table table-hover" id="itemRecTable">
                            <thead>
                                <tr>
<!--                                 	<td>Id</td> -->
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
<%--                                     <td >${recomendation.getId()}</td> --%>
                                    <td class="infoTd">${recomendation.getGroupName()}</td>
                                    <td class ="infoTd">${recomendation.getPost()}</td>
                                    <td class = "infoId">${recomendation.getUser()}</td>   
                                    <td style="display:none">${recomendation.getGroupId()} </td>                                
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
           
</body>
</html>