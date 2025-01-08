package pe.bn.com.sate.ope.persistence.mapper.internal;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import pe.bn.com.sate.ope.transversal.dto.sate.Cargo;

public interface CargoMapper {

	@Select("SELECT " +
	        "B15_FEC_CORTE AS fechaCorte, " +
	        "B15_FEC_PROCESO AS fechaProceso, " +
	        "B15_CUENTA_CARGO AS numeroCuenta, " +
	        "B15_MONTO_CARGADO AS montoCargado, " +
	        "B15_MONTO_PENDIENTE AS montoPendiente, " +
	        "B15_SALDO_INICIAL AS saldoInicial, " +
	        "B15_SALDO_FINAL AS saldoFinal " +
	        "FROM BN_SATE.BNSATE15_CARGO_HIS " +
	        "WHERE B15_CUENTA_CARGO = SUBSTR(#{cuentaCorriente}, -19) " +
	        "AND TRUNC(B15_FEC_PROCESO) BETWEEN TRUNC(TO_DATE(#{fechaInicio},'dd/mm/yy')) " +
	        "AND TRUNC(TO_DATE(#{fechaFin},'dd/mm/yy'))")
	@ResultMap("mapCargo")
	public List<Cargo> obtenerlistaCargos(
	        @Param("cuentaCorriente") String cuentaCorriente,
	        @Param("fechaInicio") String fechaInicio,
	        @Param("fechaFin") String fechaFin);

}
