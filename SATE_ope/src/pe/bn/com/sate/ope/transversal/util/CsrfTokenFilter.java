package pe.bn.com.sate.ope.transversal.util;

import java.io.IOException;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;

public class CsrfTokenFilter implements Filter {

    private static final String CSRF_TOKEN_SESSION_ATTR = "csrf_token";
    private static final Logger logger = Logger.getLogger(CsrfTokenFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("Inicializando CsrfTokenFilter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Convertir las solicitudes y respuestas a HTTP
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Obtener el método HTTP y la URI de la solicitud
        String method = httpRequest.getMethod();
        String requestURI = httpRequest.getRequestURI();
        logger.debug("Método HTTP de la solicitud: " + method);
        logger.debug("Request URI: " + requestURI);
        // Verificar si la URL está en la lista de exclusión
        if (method == null) {
             chain.doFilter(request, response);
            return;
        }
        // Solo validar solicitudes que modifican el estado (POST, PUT, DELETE, etc.)
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)) {

            // Obtener el token CSRF de la sesión
            HttpSession session = httpRequest.getSession(false);
            String sessionToken = (session != null) ? (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR) : null;
            logger.debug("Token CSRF de la sesión: " + sessionToken);

            // Obtener el token CSRF de la solicitud
            String requestToken = getRequestCsrfToken(httpRequest);
            logger.debug("Token CSRF de la solicitud: " + requestToken);

            // Validar el token CSRF
            if (sessionToken == null || requestToken == null  || !sessionToken.equals(requestToken)) {
                // Token CSRF inválido
                logger.debug("Token CSRF inválido o no coincide. Redirigiendo a /acceso_denegado.jsf");
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/acceso_denegado.jsf");
                return;
            } else {
                logger.debug("Token CSRF válido. Continuando con la solicitud.");
            }
        } else {
            logger.debug("Método HTTP no requiere validación CSRF: " + method);
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.debug("Destruyendo CsrfTokenFilter");
    }

    // Método para obtener el token CSRF de la solicitud
    private String getRequestCsrfToken(HttpServletRequest request) {
        // Intentar obtener el token sin el prefijo del formulario
        String token = request.getParameter("csrf_token");
        if (token != null) {
            return token;
        }
        // Si no se encuentra, buscar entre todos los parámetros
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String paramName : parameterMap.keySet()) {
            if (paramName.endsWith(":csrfToken")) {
                String[] values = parameterMap.get(paramName);
                if (values != null && values.length > 0) {
                    return values[0];
                }
            }
        }
        // Si no se encuentra, intentar obtenerlo del encabezado
        token = request.getHeader("X-Csrf-Token");
        if (token != null) {
            return token;
        }
        // Si no se encuentra, devolver null
        return null;
    }
}
