package pe.bn.com.sate.ope.persistence.mapper.internal;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import pe.bn.com.sate.ope.transversal.dto.sate.Parametro;

 
public interface ParametroMapper {

	@Select("SELECT BN_SATE.BNSQ_16_MOV_NUM_REFERENCIA.nextval from dual ")	
	public long obtenerNumeroReferenciaMovimientos(); 
	
	@Select("SELECT BN_SATE.BNSQ_16_SAL_NUM_REFERENCIA.nextval from dual ")	
	public long obtenerNumeroReferenciaSaldos(); 
		
	@Select("SELECT BN_SATE.BNSQ_REFE.nextval FROM DUAL")	
	public long obtenerNumeroReferenciaWS(); 

	@Select("SELECT * FROM bn_sate.BNSATE16_parametro WHERE B16_ID_TABLA = #{idTabla} and B16_ID_REGISTRO =#{idRegistro} ")
	@ResultMap("mapParametro")
	public Parametro buscarParametro(@Param("idTabla") String idTabla,
			@Param("idRegistro") String idRegistro);
}
