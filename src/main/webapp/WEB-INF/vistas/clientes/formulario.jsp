<%-- Formulario para registrar o actualizar un cliente.
     ClienteServlet envía: cliente, accion ("registrar" o "actualizar"), tiposDocumento y, si hay, errores. --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="esNuevo" value="${accion == 'registrar'}" />
<c:set var="tituloPagina" value="${esNuevo ? 'Registrar cliente' : 'Editar cliente'}" />
<%@ include file="/WEB-INF/vistas/plantilla/encabezado.jspf" %>

<h1>${tituloPagina}</h1>

<c:if test="${not empty errores}">
    <div class="aviso aviso--error" role="alert">
        <p>Revise estos datos:</p>
        <ul>
            <c:forEach var="mensaje" items="${errores}">
                <li><c:out value="${mensaje}" /></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<%-- method="post": los datos viajan dentro de la petición, no en la barra de direcciones --%>
<form class="formulario tarjeta" action="${pageContext.request.contextPath}/clientes" method="post">
    <input type="hidden" name="accion" value="${accion}">
    <c:if test="${not esNuevo}">
        <input type="hidden" name="idPersona" value="${cliente.idPersona}">
        <%-- Un select deshabilitado no se envía; este campo oculto lleva su valor --%>
        <input type="hidden" name="tipoDocumento" value="${cliente.tipoDocumento}">
    </c:if>

    <div class="formulario__fila">
        <div class="campo">
            <label for="tipoDocumento">Tipo de documento *</label>
            <%-- El documento no se puede cambiar al editar (es la identidad del cliente) --%>
            <select id="tipoDocumento" name="tipoDocumento" required ${esNuevo ? '' : 'disabled'}>
                <option value="">Seleccione…</option>
                <c:forEach var="tipo" items="${tiposDocumento}">
                    <option value="${tipo}" ${tipo == cliente.tipoDocumento ? 'selected' : ''}>${tipo}</option>
                </c:forEach>
            </select>
        </div>
        <div class="campo">
            <label for="numeroDocumento">Número de documento *</label>
            <input type="text" id="numeroDocumento" name="numeroDocumento" required
                   maxlength="30" pattern="[0-9A-Za-z]{5,30}"
                   value="${fn:escapeXml(cliente.numeroDocumento)}" ${esNuevo ? '' : 'readonly'}>
        </div>
    </div>

    <div class="formulario__fila">
        <div class="campo">
            <label for="nombre">Nombre *</label>
            <input type="text" id="nombre" name="nombre" required maxlength="50"
                   value="${fn:escapeXml(cliente.nombre)}">
        </div>
        <div class="campo">
            <label for="apellido">Apellido</label>
            <input type="text" id="apellido" name="apellido" maxlength="50"
                   value="${fn:escapeXml(cliente.apellido)}">
        </div>
    </div>

    <div class="formulario__fila">
        <div class="campo">
            <label for="telefono">Teléfono</label>
            <input type="tel" id="telefono" name="telefono" maxlength="15" pattern="[0-9 +]{7,15}"
                   value="${fn:escapeXml(cliente.telefono)}">
        </div>
        <div class="campo">
            <label for="email">Correo electrónico</label>
            <input type="email" id="email" name="email" maxlength="100"
                   value="${fn:escapeXml(cliente.email)}">
        </div>
    </div>

    <div class="campo">
        <label for="direccion">Dirección</label>
        <input type="text" id="direccion" name="direccion" maxlength="100"
               value="${fn:escapeXml(cliente.direccion)}">
    </div>

    <c:if test="${not esNuevo}">
        <div class="campo">
            <label for="estado">Estado *</label>
            <select id="estado" name="estado" required>
                <option value="ACTIVO" ${cliente.estado == 'ACTIVO' ? 'selected' : ''}>ACTIVO</option>
                <option value="INACTIVO" ${cliente.estado == 'INACTIVO' ? 'selected' : ''}>INACTIVO</option>
            </select>
        </div>
    </c:if>

    <div class="acciones">
        <button class="boton" type="submit">${esNuevo ? 'Registrar' : 'Guardar cambios'}</button>
        <a class="boton boton--secundario" href="${pageContext.request.contextPath}/clientes">Cancelar</a>
    </div>
</form>

<%@ include file="/WEB-INF/vistas/plantilla/pie.jspf" %>