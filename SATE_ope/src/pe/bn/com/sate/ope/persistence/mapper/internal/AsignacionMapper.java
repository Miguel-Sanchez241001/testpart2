package pe.bn.com.sate.ope.persistence.mapper.internal;
 
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
public interface AsignacionMapper {
 
	@Select("SELECT t1.B04_ID_CAS , " +
            "t1.B05_ID_TAR , " +
            "t1.B04_CODIGO_ASIGNACION , " +
            "t1.B04_FECHA_INICIO_LINEA , " +
            "t1.B04_FECHA_FIN_LINEA , " +
            "t1.B04_FECHA_REGISTRO , " +
            "t1.B04_LINEA , " +
            "t1.B04_CUENTA_EXPEDIENTE , " +
            "CASE " +
            "    WHEN t1.B04_FECHA_FIN_LINEA = ( " +
            "        SELECT MAX(t2.B04_FECHA_FIN_LINEA) " +
            "        FROM BN_SATE.BNSATE04_ASIGNACION t2 " +
            "        WHERE t2.B05_ID_TAR = t1.B05_ID_TAR " +
            "    ) THEN 'VIGENTE' " +
            "    ELSE 'PASADO' " +
            "END AS estado, " +
            "tar.B05_NUM_TARJETA , " +
            "tar.B05_FEC_VENCIMIENTO  " +
            "FROM BN_SATE.BNSATE04_ASIGNACION t1 " +
            "JOIN BN_SATE.BNSATE05_TARJETA tar ON tar.B05_ID_TAR = t1.B05_ID_TAR " +
            "JOIN BN_SATE.BNSATE06_CLIENTE clien ON clien.B06_ID_CLI = tar.B06_ID_CLI " +
            "WHERE clien.B06_TIPO_DOCUMENTO = #{tipoDocumento} " +
            "AND clien.B06_NUM_DOCUMENTO = #{numDocumento} AND tar.B05_DISENO = #{diseno} AND tar.B05_TIPO_TARJETA = #{tipoTar} ")
    @ResultMap("mapAsignacion")
    public List<Asignacion> obtenerAsignacionesPorDocumento(
            @Param("tipoDocumento") String tipoDocumento,
            @Param("numDocumento") String numDocumento,@Param("diseno") String diseno,@Param("tipoTar") String tipoTar);
    
    
	@Select("SELECT t1.B04_ID_CAS , " +
            "t1.B05_ID_TAR , " +
            "t1.B04_CODIGO_ASIGNACION , " +
            "t1.B04_FECHA_INICIO_LINEA , " +
            "t1.B04_FECHA_FIN_LINEA , " +
            "t1.B04_FECHA_REGISTRO , " +
            "t1.B04_LINEA , " +
            "t1.B04_CUENTA_EXPEDIENTE , " +
            "CASE " +
            "    WHEN t1.B04_FECHA_FIN_LINEA = ( " +
            "        SELECT MAX(t2.B04_FECHA_FIN_LINEA) " +
            "        FROM BN_SATE.BNSATE04_ASIGNACION t2 " +
            "        WHERE t2.B05_ID_TAR = t1.B05_ID_TAR " +
            "    ) THEN 'VIGENTE' " +
            "    ELSE 'PASADO' " +
            "END AS estado, " +
            "tar.B05_NUM_TARJETA , " +
            "tar.B05_FEC_VENCIMIENTO  " +
            "FROM BN_SATE.BNSATE04_ASIGNACION t1 " +
            "JOIN BN_SATE.BNSATE05_TARJETA tar ON tar.B05_ID_TAR = t1.B05_ID_TAR " +
            "JOIN BN_SATE.BNSATE06_CLIENTE clien ON clien.B06_ID_CLI = tar.B06_ID_CLI " +
            "WHERE tar.B05_NUM_TARJETA = #{numTarjeta} AND tar.B05_DISENO = #{diseno} AND tar.B05_TIPO_TARJETA = #{tipoTar} ")
    @ResultMap("mapAsignacion")
    public List<Asignacion> obtenerAsignacionesPorTarjeta(
            @Param("numTarjeta") String numTarjeta,@Param("diseno") String diseno,@Param("tipoTar") String tipoTar);
    
    
    @Select("SELECT t1.B04_ID_CAS , " +
            "t1.B05_ID_TAR , " +
            "t1.B04_CODIGO_ASIGNACION , " +
            "t1.B04_FECHA_INICIO_LINEA , " +
            "t1.B04_FECHA_FIN_LINEA , " +
            "t1.B04_FECHA_REGISTRO , " +
            "t1.B04_LINEA , " +
            "t1.B04_CUENTA_EXPEDIENTE , " +
            "CASE " +
            "    WHEN t1.B04_FECHA_FIN_LINEA = ( " +
            "        SELECT MAX(t2.B04_FECHA_FIN_LINEA) " +
            "        FROM BN_SATE.BNSATE04_ASIGNACION t2 " +
            "        WHERE t2.B05_ID_TAR = t1.B05_ID_TAR " +
            "    ) THEN 'VIGENTE' " +
            "    ELSE 'PASADO' " +
            "END AS estado, " +
            "tar.B05_NUM_TARJETA , " +
            "tar.B05_FEC_VENCIMIENTO  " +
            "FROM BN_SATE.BNSATE04_ASIGNACION t1 " +
            "JOIN BN_SATE.BNSATE05_TARJETA tar ON tar.B05_ID_TAR = t1.B05_ID_TAR " +
            "JOIN BN_SATE.BNSATE06_CLIENTE clien ON clien.B06_ID_CLI = tar.B06_ID_CLI " +
            "WHERE clien.B06_TIPO_DOCUMENTO = #{tipoDocumento} " +
            "AND clien.B06_NUM_DOCUMENTO = #{numDocumento}")
    @ResultMap("mapAsignacion")
    public List<Asignacion> obtenerAsignacionesPorDocumentoSimple(
            @Param("tipoDocumento") String tipoDocumento,
            @Param("numDocumento") String numDocumento);
    
    
    @Select("SELECT t1.B04_ID_CAS , " +
            "t1.B05_ID_TAR , " +
            "t1.B04_CODIGO_ASIGNACION , " +
            "t1.B04_FECHA_INICIO_LINEA , " +
            "t1.B04_FECHA_FIN_LINEA , " +
            "t1.B04_FECHA_REGISTRO , " +
            "t1.B04_LINEA , " +
            "t1.B04_CUENTA_EXPEDIENTE , " +
            "CASE " +
            "    WHEN t1.B04_FECHA_FIN_LINEA = ( " +
            "        SELECT MAX(t2.B04_FECHA_FIN_LINEA) " +
            "        FROM BN_SATE.BNSATE04_ASIGNACION t2 " +
            "        WHERE t2.B05_ID_TAR = t1.B05_ID_TAR " +
            "    ) THEN 'VIGENTE' " +
            "    ELSE 'PASADO' " +
            "END AS estado, " +
            "tar.B05_NUM_TARJETA , " +
            "tar.B05_FEC_VENCIMIENTO  " +
            "FROM BN_SATE.BNSATE04_ASIGNACION t1 " +
            "JOIN BN_SATE.BNSATE05_TARJETA tar ON tar.B05_ID_TAR = t1.B05_ID_TAR " +
            "JOIN BN_SATE.BNSATE06_CLIENTE clien ON clien.B06_ID_CLI = tar.B06_ID_CLI " +
            "WHERE tar.B05_NUM_TARJETA = #{numTarjeta}")
    @ResultMap("mapAsignacion")
    public List<Asignacion> obtenerAsignacionesPorTarjetaSimple(
            @Param("numTarjeta") String numTarjeta );
    
}