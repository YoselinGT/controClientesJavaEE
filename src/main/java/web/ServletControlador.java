package web;

import datos.ClienteDaoJDBC;
import dominio.Cliente;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ServletControlador")
public class ServletControlador extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion != null) {
            switch (accion) {
                case "editar":
                    this.editarCliente(request, response);
                    break;
                case "eliminar":
                    this.elimiarCliente(request, response);
                    break;
                default:
                    this.accionDefaul(request, response);
                    break;

            }
        } else {
            this.accionDefaul(request, response);
        }
    }

    private void accionDefaul(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Cliente> clientes = new ClienteDaoJDBC().listar();
        System.out.println("clientes " + clientes);
        HttpSession sesion = request.getSession();
        sesion.setAttribute("clientes", clientes);
        sesion.setAttribute("saldoTotal", this.calcularSaldoTotal(clientes));
        sesion.setAttribute("totalClientes", clientes.size());
        //request.getRequestDispatcher("clientes.jsp").forward(request, response);
        response.sendRedirect("clientes.jsp");

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String accion = request.getParameter("accion");
        if (accion != null) {
            switch (accion) {
                case "insertar":
                    this.insertarCliente(request, response);
                    break;
                case "modificar":
                    this.modificarCliente(request, response);
                    break;
                default:
                    this.accionDefaul(request, response);
                    break;

            }
        } else {
            this.accionDefaul(request, response);
        }
        //request.getRequestDispatcher("clientes.jsp").forward(request, response);

    }

    private void insertarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperamos los valores del fomularios agregarCliente

        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        double saldo = 0;
        String saldoString = request.getParameter("saldo");
        if (saldoString != null || "".equals(saldoString)) {
            saldo = Double.parseDouble(saldoString);
        }

        //Creamos el objeto de cliente
        Cliente cliente = new Cliente(nombre, apellido, email, telefono, saldo);

        //Insertamos en nuevo objeto en la base de datos
        int registroModificados = new ClienteDaoJDBC().insertar(cliente);
        System.out.println("registroModificados = " + registroModificados);

        //Redirigimos hacia accion por default 
        this.accionDefaul(request, response);
    }

    private double calcularSaldoTotal(List<Cliente> clientes) {
        double saldoTotal = 0;
        for (Cliente cliente : clientes) {
            saldoTotal += cliente.getSaldo();
        }
        return saldoTotal;
    }

    private void editarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperamos el id cliente
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));

        Cliente cliente = new ClienteDaoJDBC().encontrar(new Cliente(idCliente));
        request.setAttribute("cliente", cliente);
        String jspEditar = "/WEB-INF/paginas/cliente/editarCliente.jsp";
        request.getRequestDispatcher(jspEditar).forward(request, response);
    }

    private void modificarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperamos los valores del fomularios agregarCliente

        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        double saldo = 0;
        String saldoString = request.getParameter("saldo");
        if (saldoString != null || "".equals(saldoString)) {
            saldo = Double.parseDouble(saldoString);
        }

        //Creamos el objeto de cliente
        Cliente cliente = new Cliente(idCliente, nombre, apellido, email, telefono, saldo);

        //Modificar el objeto en la base de datos
        int registroModificados = new ClienteDaoJDBC().actualizar(cliente);
        System.out.println("registroModificados = " + registroModificados);

        //Redirigimos hacia accion por default 
        this.accionDefaul(request, response);
    }

    private void elimiarCliente(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Recuperamos el id cliente
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));

        Cliente cliente = new Cliente(idCliente);

        int registroModificados = new ClienteDaoJDBC().eliminar(cliente);
        System.out.println("registroModificados = " + registroModificados);

        //Redirigimos hacia accion por default 
        this.accionDefaul(request, response);
    }

}
