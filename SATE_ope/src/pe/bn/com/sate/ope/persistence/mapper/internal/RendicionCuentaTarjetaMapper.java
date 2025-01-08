package pe.bn.com.sate.ope.persistence.mapper.internal;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import pe.bn.com.sate.ope.transversal.dto.sate.CuentaTarjeta;

public interface RendicionCuentaTarjetaMapper {
	
	@Select("SELECT " +
	        "fecha_autorizacion AS fechaAut, " +
	        "fecha_salida AS fechaSal, " +
	        "fecha_retorno AS fechaRet, " +
	        "duracion AS duracion, " +
	        "monto_linea_asignado AS montoAsig, " +
	        "monto_linea_utilizado AS montoUtil, " +
	        "numero_autorizacion AS numAut, " +
	        "monto_linea_devuelta AS montoDev " +
	        "FROM ficticia_cuenta_tarjeta " +
	        "WHERE numero_tarjeta = SUBSTR(#{numeroTarjeta}, -16) " +
	        "AND ruc = #{ruc}")
	@ResultMap("mapCuentaTarjeta")
	public List<CuentaTarjeta> obtenerListaCuentaTarjeta(
	        @Param("numeroTarjeta") String numeroTarjeta,
	        @Param("ruc") String ruc);

}
