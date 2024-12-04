package pe.bn.com.sate.ope.transversal.util.componentes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

 
public class CSRFTokenFilter implements Filter {

    private HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
	private final Logger logger = Logger
			.getLogger(CSRFTokenFilter.class);
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización si es necesaria
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // Convertir a HttpServletRequest y HttpServletResponse
        /*HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Intentar obtener el CsrfToken desde la solicitud
        CsrfToken csrfToken = (CsrfToken) httpRequest.getAttribute(CsrfToken.class.getName());
        if (csrfToken == null) {
            // Generar un nuevo CsrfToken
            csrfToken = csrfTokenRepository.generateToken(httpRequest);
            // Guardar el token en la sesión y en la solicitud
            csrfTokenRepository.saveToken(csrfToken, httpRequest, httpResponse);
            httpRequest.setAttribute(CsrfToken.class.getName(), csrfToken);
        }else {
        	logger.info("CSRF Existe en la sesión.");
            logger.info("CSRF TOKEN: "+csrfToken.getToken());
            logger.info("CSRF HEADER: "+csrfToken.getHeaderName());
            logger.info("CSRF ParameterName: "+csrfToken.getParameterName());
        }

        // Hacer que el token esté disponible como atributos de solicitud para las páginas JSF
        request.setAttribute("_csrf.parameterName", csrfToken.getParameterName());
        request.setAttribute("_csrf.token", csrfToken.getToken());

        // Continuar con la cadena de filtros
*/        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpieza si es necesaria
    }
}
