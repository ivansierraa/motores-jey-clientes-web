<%-- Lista de clientes. La llena ClienteServlet con el atributo "clientes". --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="tituloPagina" value="Clientes" />
<%@ include file="/WEB-INF/vistas/plantilla/encabezado.jspf" %>

<h1>Clientes del taller</h1>

<%@ include file="/WEB-INF/vistas/plantilla/mensajes.jspf" %>

<%-- Formulario de búsqueda: usa GET porque solo consulta --%>
<form class="buscador" action="${pageContext.request.contextPath}/clientes" method="get">
    <input type="hidden" name="accion" value="buscar">
    <label for="id">Buscar por id</label>
    <input type="number" id="id" name="id" min="1" required value="${fn:escapeXml(param.id)}">
    <button class="boton" type="submit">Buscar</button>
    <c:if test="${busqueda}">
        <a class="boton boton--secundario" href="${pageContext.request.contextPath}/clientes">Ver todos</a>
    </c:if>
</form>

<c:choose>
    <c:when test="${empty clientes}">
        <p class="vacio">No hay clientes para mostrar.</p>
    </c:when>
    <c:otherwise>
        <div class="tabla-contenedor">
            <table class="tabla">
                <thead>
                <tr>
                    <th>Id</th><th>Documento</th><th>Nombre</th><th>Teléfono</th>
                    <th>Correo</th><th>Estado</th><th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="cliente" items="${clientes}">
                    <tr>
                        <td>${cliente.idPersona}</td>
                        <td><c:out value="${cliente.tipoDocumento} ${cliente.numeroDocumento}" /></td>
                        <td><c:out value="${cliente.nombre} ${cliente.apellido}" /></td>
                        <td><c:out value="${cliente.telefono}" default="—" /></td>
                        <td><c:out value="${cliente.email}" default="—" /></td>
                        <td><span class="insignia insignia--${cliente.estado}">${cliente.estado}</span></td>
                        <td class="tabla__acciones">
                            <a class="boton boton--pequeno"
                               href="${pageContext.request.contextPath}/clientes?accion=editar&id=${cliente.idPersona}">Editar</a>
                            <%-- Eliminar cambia datos: por eso es un formulario POST y no un enlace --%>
                            <form action="${pageContext.request.contextPath}/clientes" method="post"
                                  onsubmit="return confirm('¿Eliminar al cliente #${cliente.idPersona}?');">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="idPersona" value="${cliente.idPersona}">
                                <button class="boton boton--peligro boton--pequeno" type="submit">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <p class="total">Total: ${clientes.size()} cliente(s)</p>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/vistas/plantilla/pie.jspf" %>