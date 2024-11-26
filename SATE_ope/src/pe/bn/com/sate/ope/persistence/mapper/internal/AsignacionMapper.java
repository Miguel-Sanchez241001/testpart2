package pe.bn.com.sate.ope.persistence.mapper.internal;
 
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
 
import pe.bn.com.sate.ope.transversal.dto.sate.Asignacion;
 
import java.util.List;
public interface AsignacionMapper {
 
	@Select("SELECT t1.B04_ID_CAS , " +
            "t1.B05_ID_TAR , " +			
			"tar.B00_ID_EMP , " +
			"tar.B06_ID_CLI , " +            
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
            "JOIN  BN_SATE.BNSATE00_EMPRESA empre ON empre.B00_ID_EMP = tar.B00_ID_EMP " +
            "JOIN BN_SATE.BNSATE06_CLIENTE clien ON clien.B06_ID_CLI = tar.B06_ID_CLI " +
            "WHERE clien.B06_TIPO_DOCUMENTO = #{tipoDocumento} " +
            "and empre.B00_NUM_RUC = #{rucUsuario} " + 
            "AND clien.B06_NUM_DOCUMENTO = #{numDocumento} " +
            "AND tar.B05_DISENO = #{diseno} " +
            "AND tar.B05_TIPO_TARJETA = #{tipoTar} " +             
			" ORDER BY " +         "    CASE " +         
			"        WHEN t1.B04_FECHA_FIN_LINEA = ( " +         
			"            SELECT MAX(t2.B04_FECHA_FIN_LINEA) " +         
			"            FROM BN_SATE.BNSATE04_ASIGNACION t2 " +         
			"            WHERE t2.B05_ID_TAR = t1.B05_ID_TAR " +         
			"        ) THEN 1 " +         "        ELSE 2 " +         
			"    END, " +         "    t1.B04_FECHA_FIN_LINEA DESC")			
    @ResultMap("mapAsignacion")
    public List<Asignacion> obtenerAsignacionesPorDocumento(
            @Param("tipoDocumento") String tipoDocumento,
            @Param("numDocumento") String numDocumento,
            @Param("diseno") String diseno,
            @Param("tipoTar") String tipoTar,
            @Param("rucUsuario") String rucUsuario);
    
    
	@Select("SELECT t1.B04_ID_CAS , " +
            "t1.B05_ID_TAR , " +
            "tar.B00_ID_EMP , " +
            "tar.B06_ID_CLI , " +
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
            "JOIN  BN_SATE.BNSATE00_EMPRESA empre ON empre.B00_ID_EMP = tar.B00_ID_EMP " +
            "JOIN BN_SATE.BNSATE06_CLIENTE clien ON clien.B06_ID_CLI = tar.B06_ID_CLI " +
            "WHERE tar.B05_NUM_TARJETA = #{numTarjeta} " +
            "and empre.B00_NUM_RUC = #{rucUsuario} " +
            "AND tar.B05_DISENO = #{diseno} " +
            "AND tar.B05_TIPO_TARJETA = #{tipoTar} " +
            " ORDER BY " +         "    CASE " +         
            "        WHEN t1.B04_FECHA_FIN_LINEA = ( " +         
            "            SELECT MAX(t2.B04_FECHA_FIN_LINEA) " +         
            "            FROM BN_SATE.BNSATE04_ASIGNACION t2 " +         
            "            WHERE t2.B05_ID_TAR = t1.B05_ID_TAR " +         
            "        ) THEN 1 " +         "        ELSE 2 " +         
            "    END, " +         "    t1.B04_FECHA_FIN_LINEA DESC")
    @ResultMap("mapAsignacion")
    public List<Asignacion> obtenerAsignacionesPorTarjeta(
            @Param("numTarjeta") String numTarjeta,
            @Param("diseno") String diseno,
            @Param("tipoTar") String tipoTar,
            @Param("rucUsuario") String rucUsuario);
    
	
	
	
	
	
	
	
    
    @Select("SELECT t1.B04_ID_CAS , " +
            "t1.B05_ID_TAR , " +
            "tar.B00_ID_EMP , " +
            "tar.B06_ID_CLI , " +
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
            "JOIN  BN_SATE.BNSATE00_EMPRESA empre ON empre.B00_ID_EMP = tar.B00_ID_EMP " +
            "JOIN BN_SATE.BNSATE06_CLIENTE clien ON clien.B06_ID_CLI = tar.B06_ID_CLI " +
            "WHERE clien.B06_TIPO_DOCUMENTO = #{tipoDocumento} " +            
            "AND clien.B06_NUM_DOCUMENTO = #{numDocumento} and empre.B00_NUM_RUC = #{rucUsuario} " +            
            " ORDER BY " +         "    CASE " +         
            "        WHEN t1.B04_FECHA_FIN_LINEA = ( " +         
            "            SELECT MAX(t2.B04_FECHA_FIN_LINEA) " +         
            "            FROM BN_SATE.BNSATE04_ASIGNACION t2 " +         
            "            WHERE t2.B05_ID_TAR = t1.B05_ID_TAR " +         
            "        ) THEN 1 " +         "        ELSE 2 " +         
            "    END, " +         "    t1.B04_FECHA_FIN_LINEA DESC")
            
    @ResultMap("mapAsignacion")
    public List<Asignacion> obtenerAsignacionesPorDocumentoSimple(
            @Param("tipoDocumento") String tipoDocumento,
            @Param("numDocumento") String numDocumento,
            @Param("rucUsuario") String rucUsuario);
    
    
    @Select("SELECT t1.B04_ID_CAS , " +
            "t1.B05_ID_TAR , " +
            "tar.B00_ID_EMP , " +
            "tar.B06_ID_CLI , " +
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
            "JOIN  BN_SATE.BNSATE00_EMPRESA empre ON empre.B00_ID_EMP = tar.B00_ID_EMP " +
            "JOIN BN_SATE.BNSATE06_CLIENTE clien ON clien.B06_ID_CLI = tar.B06_ID_CLI " +            
            "WHERE tar.B05_NUM_TARJETA = #{numTarjeta} and empre.B00_NUM_RUC = #{rucUsuario} " +      
            " ORDER BY " +         "    CASE " +         
            "        WHEN t1.B04_FECHA_FIN_LINEA = ( " +         
            "            SELECT MAX(t2.B04_FECHA_FIN_LINEA) " +         
            "            FROM BN_SATE.BNSATE04_ASIGNACION t2 " +         
            "            WHERE t2.B05_ID_TAR = t1.B05_ID_TAR " +         
            "        ) THEN 1 " +         "        ELSE 2 " +         
            "    END, " +         "    t1.B04_FECHA_FIN_LINEA DESC")
    @ResultMap("mapAsignacion")
    public List<Asignacion> obtenerAsignacionesPorTarjetaSimple(
            @Param("numTarjeta") String numTarjeta ,
            @Param("rucUsuario") String rucUsuario);
    
}