<%-- Página de inicio del módulo --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Inicio" />
<%@ include file="/WEB-INF/vistas/plantilla/encabezado.jspf" %>

<section class="tarjeta">
    <h1>Módulo Gestión de Clientes</h1>
    <p>Registre, consulte, actualice y elimine los clientes del taller (RF-03).</p>
    <div class="acciones">
        <a class="boton" href="${pageContext.request.contextPath}/clientes">Ver clientes</a>
        <a class="boton boton--secundario" href="${pageContext.request.contextPath}/clientes?accion=nuevo">Registrar cliente</a>
    </div>
</section>

<%@ include file="/WEB-INF/vistas/plantilla/pie.jspf" %>