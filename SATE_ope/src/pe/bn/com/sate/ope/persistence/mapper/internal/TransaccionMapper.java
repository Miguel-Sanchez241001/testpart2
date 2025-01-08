package pe.bn.com.sate.ope.persistence.mapper.internal;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import pe.bn.com.sate.ope.transversal.dto.sate.Transaccion;

public interface TransaccionMapper {

	@Select("SELECT " +
	        "SUBSTR(B14_NUM_TARJETA, 4, 4) || '****' || SUBSTR(B14_NUM_TARJETA, -4) AS numeroTarjeta, " +
	        "B14_FEC_OPERACION AS fechaOperacion, " +
	        "B14_FEC_POSTEO AS fechaPosteo, " +
	        "CASE B14_OPERACION " +
	        "    WHEN '001' THEN 'DISP EFECTIVO VENTANILLA - BN (REVOLVING)' " +
	        "    WHEN '002' THEN 'DISP EFECTIVO ATM - BN (REVOLVING) (SVC 1)' " +
	        "    WHEN '003' THEN 'DISP EFECTIVO ATM EXTERIOR (REVOLVING) (SVC 2)' " +
	        "    WHEN '004' THEN 'DISP EFECTIVO VENTANILLA EXTERIOR (REVOLVING)' " +
	        "    WHEN '005' THEN 'DISP EFECTIVO CAJERO CORRESPONSAL BN (REVOLVING)' " +
	        "    WHEN '006' THEN 'DISP EFECTIVO ATM BN' " +
	        "    WHEN '020' THEN 'COMPRA LOCAL REVOLVING' " +
	        "    WHEN '029' THEN 'COMPRAS EN EL EXTERIOR' " +
	        "    WHEN '050' THEN 'PAGO A CUENTA VENTANILLA' " +
	        "    WHEN '051' THEN 'PAGO CARGO EN CUENTA' " +
	        "    WHEN '083' THEN 'DISP EFECTIVO ATM CUOTA - BN (SVC 3)' " +
	        "    WHEN '084' THEN 'DISP EFECTIVO VENTANILLA CUOTA - BN' " +
	        "    WHEN '089' THEN 'COMPRA EN CUOTA' " +
	        "    ELSE B14_OPERACION " +
	        "END AS operacion, " +
	        "B14_COMERCIO AS comercio, " +
	        "B14_MONTO AS monto, " +
	        "B14_AUTORIZACION_PMC AS autorizacionPMC, " +
	        "B14_NUM_AUTORIZACION AS numeroAutorizacion, " +
	        "CASE B14_ESTADO " +
	        "    WHEN '1' THEN 'Procesada' " +
	        "    ELSE 'Pendiente' " +
	        "END AS estado " +
	        "FROM BN_SATE.BNSATE14_TRANSACCION_HIS " +
	        "WHERE " +
	        "B14_CUENTA_CARGO = SUBSTR(#{cuentaCorriente}, -14) AND " +
	        "TRUNC(B14_FEC_OPERACION) BETWEEN " +
	        "TRUNC(TO_DATE(#{fechaInicio}, 'dd/mm/yy')) AND " +
	        "TRUNC(TO_DATE(#{fechaFin}, 'dd/mm/yy'))")
	@ResultMap("mapTransaccion")
	public List<Transaccion> obtenerlistaTransacciones(
	        @Param("cuentaCorriente") String cuentaCorriente,
	        @Param("fechaInicio") String fechaInicio,
	        @Param("fechaFin") String fechaFin);


}
