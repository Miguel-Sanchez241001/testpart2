package pe.bn.com.sate.ope.persistence.mapper.internal;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import pe.bn.com.sate.ope.transversal.dto.sate.Transaccion;

public interface TransaccionMapper {

	@Select("SELECT " +
	        "    asi.B04_CODIGO_ASIGNACION AS numeroExpediente, " +
	        "    SUBSTR(B14_NUM_TARJETA, 4, 4) || '****' || SUBSTR(B14_NUM_TARJETA, -4) AS numeroTarjeta, " +
	        "    CASE WHEN B14_MONEDA_CUENTA = '0000' THEN 'SOLES' " +
	        "         WHEN B14_MONEDA_CUENTA = '0001' THEN 'DOLARES' END AS monedaCuenta, " +
	        "    B14_FEC_OPERACION AS fechaOperacion, " +
	        "    B14_FEC_POSTEO AS fechaPosteo, " +
	        "    (SELECT TXN.B24_DESC FROM BN_SATE.BNSATE24_COD_TRANSACCIONES TXN WHERE TXN.B124_COD = B14_OPERACION) AS operacion, " +
	        "    B14_COMERCIO AS comercio, " +
	        "    B14_MONTO AS monto, " +
	        "    CASE WHEN B14_MONEDA_TRANSACCION = '0000' THEN 'SOLES' " +
	        "         WHEN B14_MONEDA_TRANSACCION = '0001' THEN 'DOLARES' END AS monedaTransaccion, " +
	        "    B14_NUM_AUTORIZACION AS numeroAutorizacion, " +
	        "    CASE B14_ESTADO WHEN '1' THEN 'Procesada' ELSE 'Pendiente' END AS estado " +
	        "FROM " +
	        "    BN_SATE.BNSATE14_TRANSACCION_HIS his " +
	        "JOIN " +
	        "    BN_SATE.BNSATE04_ASIGNACION asi " +
	        "ON " +
	        "    asi.B04_CUENTA_EXPEDIENTE = his.B14_NUM_CUENTA_EXPEDIENTE " +
	        "WHERE " +
	        "    his.B14_CUENTA_CARGO = SUBSTR(#{cuentaCorriente}, -14) " +
	        "    AND TRUNC(his.B14_FEC_OPERACION) BETWEEN " +
	        "        TRUNC(TO_DATE(#{fechaInicio}, 'dd/mm/yy')) " +
	        "        AND TRUNC(TO_DATE(#{fechaFin}, 'dd/mm/yy'))")
	@ResultMap("mapTransaccion")
	public List<Transaccion> obtenerListaTransacciones( 
	        @Param("cuentaCorriente") String cuentaCorriente,
	        @Param("fechaInicio") String fechaInicio,
	        @Param("fechaFin") String fechaFin);



}
