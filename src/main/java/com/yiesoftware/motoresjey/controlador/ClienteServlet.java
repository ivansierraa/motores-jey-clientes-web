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
import java.sql.SQLIntegrityConstraintViolationException;
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
    private static final String VISTA_FORMULARIO = "/WEB-INF/vistas/clientes/formulario.jsp";
    private static final List<String> TIPOS_DOCUMENTO = List.of("CC", "CE", "TI", "PAS", "NIT");

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
                case "nuevo" -> mostrarFormularioNuevo(request, response);
                case "buscar" -> buscarPorId(request, response);
                default -> listar(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Error al consultar la base de datos", e);
        }
    }

    /**
     * Atiende los trámites: registrar (actualizar y eliminar llegan en el paso 5).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = valorOVacio(request.getParameter("accion"));
        try {
            switch (accion) {
                case "registrar" -> registrar(request, response);
                default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
            }
        } catch (SQLException e) {
            throw new ServletException("Error al guardar en la base de datos", e);
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

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("cliente", new Cliente());
        mostrarFormulario(request, response, "registrar");
    }

    // ---------- Acciones POST ----------

    private void registrar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Cliente cliente = leerFormulario(request);
        List<String> errores = validar(cliente, true);
        if (!errores.isEmpty()) {
            devolverConErrores(request, response, cliente, errores, "registrar");
            return;
        }
        try {
            int id = clienteDAO.insertar(cliente);
            guardarMensaje(request, "exito", "Cliente registrado con el id " + id + ".");
            response.sendRedirect(request.getContextPath() + "/clientes");
        } catch (SQLIntegrityConstraintViolationException e) {
            errores.add("Ya existe una persona con ese documento o con ese correo.");
            devolverConErrores(request, response, cliente, errores, "registrar");
        }
    }

    // ---------- Ayudantes ----------

    private Cliente leerFormulario(HttpServletRequest request) {
        Cliente cliente = new Cliente(
                limpiar(request.getParameter("tipoDocumento")),
                limpiar(request.getParameter("numeroDocumento")),
                limpiar(request.getParameter("nombre")),
                limpiar(request.getParameter("apellido")),
                limpiar(request.getParameter("telefono")),
                limpiar(request.getParameter("email")),
                limpiar(request.getParameter("direccion")));
        cliente.setEstado(limpiar(request.getParameter("estado")));
        return cliente;
    }

        private List<String> validar(Cliente cliente, boolean esNuevo) {
        List<String> errores = new ArrayList<>();
        if (esNuevo) {
            if (cliente.getTipoDocumento() == null || !TIPOS_DOCUMENTO.contains(cliente.getTipoDocumento())) {
                errores.add("Seleccione un tipo de documento.");
            }
            if (cliente.getNumeroDocumento() == null
                    || !cliente.getNumeroDocumento().matches("[0-9A-Za-z]{5,30}")) {
                errores.add("El número de documento debe tener entre 5 y 30 letras o números.");
            }
        } else if (!"ACTIVO".equals(cliente.getEstado()) && !"INACTIVO".equals(cliente.getEstado())) {
            errores.add("Seleccione un estado válido.");
        }
        if (cliente.getNombre() == null) {
            errores.add("El nombre es obligatorio.");
        } else if (cliente.getNombre().length() > 50) {
            errores.add("El nombre admite máximo 50 caracteres.");
        }
        if (cliente.getTelefono() != null && !cliente.getTelefono().matches("[0-9 +]{7,15}")) {
            errores.add("El teléfono debe tener entre 7 y 15 números.");
        }
        if (cliente.getEmail() != null && !cliente.getEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            errores.add("El correo no tiene un formato válido.");
        }
        return errores;
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
            String accion) throws ServletException, IOException {
        request.setAttribute("accion", accion);
        request.setAttribute("tiposDocumento", TIPOS_DOCUMENTO);
        request.getRequestDispatcher(VISTA_FORMULARIO).forward(request, response);
    }

    private void devolverConErrores(HttpServletRequest request, HttpServletResponse response,
            Cliente cliente, List<String> errores, String accion) throws ServletException, IOException {
        request.setAttribute("cliente", cliente);
        request.setAttribute("errores", errores);
        mostrarFormulario(request, response, accion);
    }

    /** Guarda un aviso en la sesión para mostrarlo después de la redirección. */
    private void guardarMensaje(HttpServletRequest request, String tipo, String texto) {
        request.getSession().setAttribute("mensajeTipo", tipo);
        request.getSession().setAttribute("mensajeTexto", texto);
    }

    /** Quita espacios y convierte un campo vacío en null (la BD lo guarda como NULL). */
    private static String limpiar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

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