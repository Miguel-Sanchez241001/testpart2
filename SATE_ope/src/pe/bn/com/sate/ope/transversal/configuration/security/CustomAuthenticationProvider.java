package pe.bn.com.sate.ope.transversal.configuration.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

 import pe.bn.com.sate.ope.infrastructure.exception.InternalServiceException;
import pe.bn.com.sate.ope.infrastructure.exception.ServiceException;
import pe.bn.com.sate.ope.infrastructure.facade.InterfaceGatewayFacade;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.comp.ParametroInterfazKeyProxy;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.comp.SistemaParametro;
import pe.bn.com.sate.ope.infrastructure.service.internal.CompService;
import pe.bn.com.sate.ope.infrastructure.service.internal.EmpresaService;
import pe.bn.com.sate.ope.infrastructure.service.internal.UsuarioService;
import pe.bn.com.sate.ope.persistence.mapper.internal.EmpresaMapper;
import pe.bn.com.sate.ope.persistence.mapper.internal.ParametroMapper;
import pe.bn.com.sate.ope.transversal.dto.sate.Empresa;
import pe.bn.com.sate.ope.transversal.dto.sate.Usuario;
import pe.bn.com.sate.ope.transversal.util.componentes.Parametros;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEmpresa;
import pe.bn.com.sate.ope.transversal.util.enums.TipoEstado;
import pe.bn.com.sate.ope.transversal.util.excepciones.LoginException;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private @Autowired
	InterfaceGatewayFacade fwInterfaceGateway;

	private @Autowired
	EmpresaService empresaService;
	private @Autowired
	ParametroMapper parametroMapper;
	private @Autowired
	UsuarioService usuarioService;
	private @Autowired
	CompService compService;
	
	private final Logger logger = Logger
			.getLogger(CustomAuthenticationProvider.class);
	 /**
     * Autentica el usuario contra el gateway de servicios web.
     * 
     * @param authentication Objeto de autenticación con las credenciales del usuario.
     * @return Un token de autenticación si las credenciales son válidas.
     * @throws AuthenticationException Si la autenticación falla.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return autenticacionWSInterfaceGateway(authentication);
    }

    /**
     * Verifica si el proveedor soporta el tipo de autenticación dado.
     * 
     * @param authentication Tipo de autenticación.
     * @return true si el proveedor soporta el tipo de autenticación, false en caso contrario.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * Realiza la autenticación contra el gateway de servicios web.
     * 
     * @param authentication Objeto de autenticación con las credenciales del usuario.
     * @return Un token de autenticación si las credenciales son válidas.
     */
    private Authentication autenticacionWSInterfaceGateway(Authentication authentication) {
        try {
            String username = authentication.getName();
            String password = String.valueOf(authentication.getCredentials());
            logger.info("Autenticando usuario: " + username);

            String[] usernameAndDomain = username.split("-");
            if (usernameAndDomain.length != 3) {
                String mensaje = "Formato de usuario inválido.";
                logger.error(mensaje);
                throw new LoginException(mensaje);
            }

            Empresa empresa = empresaService.buscarEmpresaAfiliada(usernameAndDomain[0]);
            Usuario usuario = usuarioService.buscarUsuario(usernameAndDomain[1], usernameAndDomain[2]);

            if (empresa != null) {
                if (usuario != null) {
                    if (usuario.getEstado() != null && !usuario.getEstado().equals(TipoEstado.INACTIVO.getId())) {
                        if (usuarioService.existeUsuarioEmpresa(usuario, usernameAndDomain[0])) {
                        	compService.asignarParametros();
                        	ConstantesGenerales.setPorcentajeEfectivo(
                        		    parametroMapper.buscarParametro("15", "0").getValor().split("-")[1]
                        		);                            return fwInterfaceGateway.buscarUsuarioInterfaceGateway(
                                    usernameAndDomain[0], empresa.getCic(), usernameAndDomain[1], usernameAndDomain[2], password);
                        } else {
                            String mensaje = "Usuario no tiene acceso a esta " + TipoEmpresa.tipoEmpresaLetras(empresa.getTipo());
                            logger.error(usuario.getNumeroDocumento()+ " " + mensaje);
                            throw new LoginException(mensaje);
                        }
                    } else {
                        String mensaje = "Usted ya no es representante de la Unidad Ejecutora";
                        logger.error(usuario.getNumeroDocumento()+ " " +mensaje);
                        throw new LoginException(mensaje);
                    }
                } else {
                    String mensaje = "Número de documento no pertenece a ningún usuario";
                    logger.error(mensaje);
                    throw new LoginException(mensaje);
                }
            } else {
                String mensaje = "RUC de empresa o unidad ejecutora no se encuentra afiliado";
                logger.error(mensaje);
                throw new LoginException(mensaje);
            }
        } catch (InternalServiceException ise) {
            logger.error("Error interno de servicio: " + ise.getMessage(), ise);
             throw new LoginException(ise.getMessage());
        } catch (ServiceException se) {
            logger.error("Error de servicio: " + se.getMessage(), se);
            throw new LoginException(ConstantesGenerales.ERROR_PERSISTENCE_EXTERNAL_WEB_SERVICE_IGF);
        } catch (Exception e) {
            logger.error("Error inesperado: " + e.getMessage(), e);
            throw new LoginException(e.getMessage());
        }
    }
   

}