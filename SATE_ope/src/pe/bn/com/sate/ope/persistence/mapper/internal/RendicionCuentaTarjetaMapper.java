package pe.bn.com.sate.ope.persistence.mapper.internal;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import pe.bn.com.sate.ope.transversal.dto.sate.CuentaTarjeta;

public interface RendicionCuentaTarjetaMapper {
	
	@Select("SELECT " +
	        "TAR.B05_FEC_AUTORIZACION AS fechaAut, " +
	        "B04A.B04_FECHA_INICIO_LINEA AS fechaSal, " +
	        "B04A.B04_FECHA_FIN_LINEA AS fechaRet, " +
	        "B04A.B04_FECHA_FIN_LINEA - B04A.B04_FECHA_INICIO_LINEA AS duracion, " +
	        "TO_NUMBER(B04A.B04_LINEA) AS montoAsig, " +
	        "CARGO.B15_MONTO_CARGADO AS montoUtil, " +
	        "B04A.B04_CODIGO_ASIGNACION AS numAut, " +
	        "TO_NUMBER(B04A.B04_LINEA) - CARGO.B15_MONTO_CARGADO AS montoDev " +
	        "FROM BN_SATE.BNSATE05_TARJETA TAR " +
	        "JOIN BN_SATE.BNSATE04_ASIGNACION B04A ON TAR.B05_ID_TAR = B04A.B05_ID_TAR " +
	        "JOIN BN_SATE.BNSATE15_CARGO_HIS CARGO ON CARGO.B15_NUM_CUENTA_EXPEDIENTE = SUBSTR(B04A.B04_CUENTA_EXPEDIENTE, -16) " +
	        "JOIN BN_SATE.BNSATE00_EMPRESA B00E ON TAR.B00_ID_EMP = B00E.B00_ID_EMP " +
	        "WHERE TAR.B05_NUM_TARJETA = #{numeroTarjeta}  " +
	        "AND B00E.B00_NUM_RUC = #{ruc}")
	@ResultMap("mapCuentaTarjeta")
	public List<CuentaTarjeta> obtenerListaCuentaTarjeta(
	        @Param("numeroTarjeta") String numeroTarjeta,
	        @Param("ruc") String ruc);


}
