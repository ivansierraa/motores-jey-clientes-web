package com.yiesoftware.motoresjey.controlador;

import com.yiesoftware.motoresjey.dao.ClienteDAO;
import com.yiesoftware.motoresjey.dao.ClienteDAOImpl;
import com.yiesoftware.motoresjey.modelo.Cliente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador web del módulo Gestión de Clientes (RF-03).
 * GET  /clientes → consultas: listar, buscar, mostrar formularios.
 * POST /clientes → cambios: registrar, actualizar, eliminar.
 *
 * @author Iván Sierra
 */
@WebServlet(name = "ClienteServlet", urlPatterns = {"/clientes"})
public class ClienteServlet extends HttpServlet {

    private static final String VISTA_LISTA = "/WEB-INF/vistas/clientes/lista.jsp";

    private final ClienteDAO clienteDAO = new ClienteDAOImpl();

    /**
     * Atiende las consultas: no cambia nada en la base de datos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = valorOVacio(request.getParameter("accion"));
        try {
            switch (accion) {
                case "buscar" -> buscarPorId(request, response);
                default -> listar(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Error al consultar la base de datos", e);
        }
    }

    // ---------- Acciones GET ----------

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        request.setAttribute("clientes", clienteDAO.listarTodos());
        request.getRequestDispatcher(VISTA_LISTA).forward(request, response);
    }

    private void buscarPorId(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Integer id = convertirEntero(request.getParameter("id"));
        List<Cliente> resultado = new ArrayList<>();
        if (id == null) {
            request.setAttribute("error", "Escriba un id válido (solo números).");
        } else {
            clienteDAO.buscarPorId(id).ifPresent(resultado::add);
            if (resultado.isEmpty()) {
                request.setAttribute("error", "No existe un cliente con el id " + id + ".");
            }
        }
        request.setAttribute("clientes", resultado);
        request.setAttribute("busqueda", true);
        request.getRequestDispatcher(VISTA_LISTA).forward(request, response);
    }

    // ---------- Ayudantes ----------

    private static String valorOVacio(String valor) {
        return valor == null ? "" : valor;
    }

    private static Integer convertirEntero(String valor) {
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }
}