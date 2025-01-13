package pe.bn.com.sate.ope.infrastructure.service.internal.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pe.bn.com.sate.ope.infrastructure.service.external.domain.comp.ParametroInterfazKeyProxy;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.comp.SistemaParametro;
import pe.bn.com.sate.ope.infrastructure.service.internal.CompService;
import pe.bn.com.sate.ope.transversal.util.componentes.Parametros;
import pe.bn.com.sate.ope.transversal.util.constantes.ConstantesGenerales;
import pe.bn.com.sate.ope.transversal.util.excepciones.LoginException;

@Service
public class CompServiceImpl implements CompService {
	private final Logger logger = Logger.getLogger(CompServiceImpl.class);
	private @Autowired Parametros parametros;

	@Value("${comp.llave.ruta}")
	private String rutaClaveSegura;
	
	
	
	
	/**
	 * Asigna los parámetros necesarios para la autenticación.
	 * 
	 * @throws Exception
	 *             Si hay un error en la asignación de los parámetros.
	 */
	@Override
	public void asignarParametros() throws Exception {
		logger.info("Asignando parámetros");
		byte[] llave = leerClavesSegurades();

		if (llave != null) {
			ParametroInterfazKeyProxy proxyComp = new ParametroInterfazKeyProxy();

			SistemaParametro sParam = proxyComp.datoParametroService(ConstantesGenerales.SISTEMA,
					ConstantesGenerales.CUENTA, ConstantesGenerales.SEMILLA, llave, ConstantesGenerales.IDUSUARIO);

			logger.info("Código de proceso: " + sParam.getProceso().getCodigo());
			parametros.setErrorComp(sParam.getProceso().getCodigo());
			parametros.setDesErrorComp(sParam.getProceso().getDescripcion());

			if (sParam.getProceso().getCodigo().equals("00000")) {

				for (int n = 0; n < sParam.getGrupoParametro().getGrupoParametro().size(); n++) {
					int cantFilas = sParam.getGrupoParametro().getGrupoParametro().get(n).getParametro().getParametro()
							.size() - 1;
					for (int j = 0; j < cantFilas + 1; j++) {
						String param = sParam.getGrupoParametro().getGrupoParametro().get(n).getParametro()
								.getParametro().get(j).getAliasParam();
						String valor = sParam.getGrupoParametro().getGrupoParametro().get(n).getParametro()
								.getParametro().get(j).getValorParam();
						if (!param.equals("")) {
							setParametros(sParam.getGrupoParametro().getGrupoParametro().get(n).getAliasGrupo(), param,
									valor);
						}
					}
				}
				logger.info("Parámetros asignados exitosamente");
			} else {
				logger.error("ERROR EN OBTENCION DE PARAMETROS COD: "+sParam.getProceso().getCodigo());
				logger.error("ERROR EN OBTENCION DE PARAMETROS DESCRIPCION: "+sParam.getProceso().getDescripcion());
				throw new Exception("ERROR EN OBTENCION DE PARAMETROS COD: "+sParam.getProceso().getCodigo().concat(sParam.getProceso().getDescripcion()));
			}
		} else {
			logger.error("Error al leer el archivo clavesegurades.key");
			throw new Exception("Error con la lectura del archivo clavesegurades.key");
		}
	}

	private void setParametros(String aliasGrupo, String param, String valor) {
		if (aliasGrupo.equals(ConstantesGenerales.GRUPO_CONEXION_RENIEC)) {
			this.setDatosReniec(param, valor);
		}
		if (aliasGrupo.equals(ConstantesGenerales.GRUPO_CONEXION_MC)) {
			this.setDatosMC(param, valor);
		}
		if (aliasGrupo.equals(ConstantesGenerales.GRUPO_TIEMPO)) {
			this.setDatosTiempo(param, valor);
		}
		if (aliasGrupo.equals(ConstantesGenerales.GRUPO_CONEXION_SFTP_MC)) {
			this.setServiceAntiguos(param, valor);
		}
		if (aliasGrupo.equals(ConstantesGenerales.GRUPO_SERVICE_ALDEAMO)) {
			this.setCorreoAldeamo(param, valor);
		}
	}

	private void setDatosReniec(String param, String valor) {
		if (param.equals(ConstantesGenerales.PARAM_CONSULTRENIEC)) {
			parametros.setConsultaReniec(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_SISTEMARENIEC)) {
			parametros.setSistemaReniec(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_USER1RENIEC)) {
			parametros.setUser1Reniec(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_USERRENIEC)) {
			parametros.setUserReniec(valor);
		}
	}

	// MGL
	private void setDatosMC(String param, String valor) {
		if (param.equals(ConstantesGenerales.PARAM_CODIGOEMISOR)) {
			parametros.setCodigoEmisorMc(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_CODIGOUSUARIO)) {
			parametros.setCodigoUsuarioMc(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_NUMTERMINAL)) {
			parametros.setNumTerminalMc(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_PREFIJONUMREFERENCIA)) {
			parametros.setPrefijoNumReferenciaMc(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_WSUSUARIOMC)) {
			parametros.setWsUsuarioMc(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_WSCLAVEMC)) {
			parametros.setWsClaveMc(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_WSURLSOAPMC)) {
			parametros.setWsSoapMc(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_COMERCIO)) {
			parametros.setWsComercioMc(valor);
		}
	}

	private void setDatosTiempo(String param, String valor) {
		if (param.equals(ConstantesGenerales.PARAM_SESIONEXPIRADATIEMPO)) {
			parametros.setSesionExpiradaTiempo(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_CONEXIONTIEMPO)) {
			parametros.setConexionTiempo(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_RESPUESTATIEMPO)) {
			parametros.setRespuestaTiempo(valor);
		}
	}

	private void setServiceAntiguos(String param, String valor) {
		if (param.equals(ConstantesGenerales.PARAM_URLSERVICESATEREST)) {
			parametros.setUrlServiceRestAntiguos(valor);
		}
	}

	private void setCorreoAldeamo(String param, String valor) {
		if (param.equals(ConstantesGenerales.PARAM_URLALDEAMO)) {
			parametros.setUrlAldeamo(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_TOKENALDEAMO)) {
			parametros.setTokenAldeamo(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_CORREOALDEAMO)) {
			parametros.setCorreoEmisor(valor);
		} else if (param.equals(ConstantesGenerales.PARAM_CROMENVIOCORREO)) {
			parametros.setCromenviocorreo(valor);
		}
	}

	public byte[] leerClavesSegurades() {
		try {
			FileInputStream fis = new FileInputStream(new File(rutaClaveSegura));
			return IOUtils.toByteArray(fis);
		} catch (IOException e) {
			logger.error("Error en la lectura del archivo: " + e.getMessage());
			logger.error("Error en la lectura de clavesegurades.key: " + e.getMessage());
			throw new LoginException(e.getMessage());
		}
	}

}
