<%-- Página de error. isErrorPage="true" activa el objeto implícito "exception". --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Error" />
<%@ include file="/WEB-INF/vistas/plantilla/encabezado.jspf" %>

<section class="tarjeta">
    <c:choose>
        <c:when test="${pageContext.errorData.statusCode == 404}">
            <h1>Página no encontrada</h1>
            <p>La dirección que escribió no existe en el módulo de clientes.</p>
        </c:when>
        <c:otherwise>
            <h1>Algo salió mal</h1>
            <p>No se pudo completar la operación. Revise que MySQL esté encendido y vuelva a intentarlo.</p>
        </c:otherwise>
    </c:choose>
    <a class="boton" href="${pageContext.request.contextPath}/clientes">Volver a clientes</a>
</section>

<%@ include file="/WEB-INF/vistas/plantilla/pie.jspf" %>