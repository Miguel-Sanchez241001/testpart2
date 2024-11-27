package pe.bn.com.sate.ope.persistence.mapper.internal;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;

public interface ClienteMapper {
	
	
	@Insert("INSERT INTO BN_SATE.BNSATE06_CLIENTE (" +
			"B06_NOMBRES," +
			"B06_APPATERNO," +
			"B06_APMATERNO," +
			"B06_TIPO_DOCUMENTO," +
			"B06_NUM_DOCUMENTO," +
			"B06_FEC_NACIMIENTO," +
			"B06_EST_CIVIL," +
			"B06_SEXO," +
			"B06_TELEF_CASA," +
			"B06_DIRECCION," +
			"B06_UBIGEO," +
			"B06_REFERENCIA)" +
			" VALUES (" +
			"#{nombres}," +
			"#{apPaterno}," +
			"#{apMaterno}," +
			"#{tipoDocumento}," +
			"#{nroDocumento}," +
			"#{fechaNacimiento}," +
			"#{estadoCivil}," +
			"#{sexo}," +
			"#{telefonoCasa}," +
			"#{direccion}," +
			"#{ubigeo}," +
			"#{referencia})")
	//@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "B06_ID_CLI")
	public void registrarCliente(Cliente cliente);
	
	@Update("UPDATE BN_SATE.BNSATE06_CLIENTE SET B06_TELEF_CASA=#{telefonoCasa} WHERE B06_ID_CLI = #{id}")
	public void actualizarCliente(Cliente cliente);
	
	@Select("SELECT * FROM BN_SATE.BNSATE06_CLIENTE WHERE " +
			"B06_TIPO_DOCUMENTO = #{tipoDocumento} AND B06_NUM_DOCUMENTO = #{numDocumento}")
	@ResultMap("mapCliente")
	public Cliente buscarCliente(@Param("tipoDocumento")String tipoDocumento,@Param("numDocumento")String numDocumento);
	
	
	@Select("SELECT BN06.* FROM BN_SATE.BNSATE06_CLIENTE BN06 INNER JOIN BN_SATE.BNSATE05_TARJETA BN05 ON BN06.B06_ID_CLI = BN05.B06_ID_CLI WHERE BN05.B05_NUM_TARJETA =#{numTarjeta}")
	@ResultMap("mapCliente")
	public Cliente buscarClientePorNumTajeta(@Param("numTarjeta") String numTarjeta);
	
	
	@Update("UPDATE BN_SATE.BNSATE06_CLIENTE " +
            "SET B06_TELEF_CASA = #{telefonoCasa} " +
            "WHERE B06_TIPO_DOCUMENTO = #{tipoDocumento} " +
            "AND B06_NUM_DOCUMENTO = #{numeroDocumento}")
	public void actualizarTelefonoCliente(
        @Param("tipoDocumento") String tipoDocumento,
        @Param("numeroDocumento") String numeroDocumento,
        @Param("telefonoCasa") String telefonoCasa
    );
 
	@Update("UPDATE BN_SATE.BNSATE05_TARJETA " +
	        "SET B05_EMAIL = #{email}, " +
	        "    B05_NUM_CELULAR = #{numcelular} " +
	        "WHERE B06_ID_CLI = (SELECT B06_ID_CLI FROM BN_SATE.BNSATE06_CLIENTE " +
	        "WHERE B06_TIPO_DOCUMENTO = #{tipoDocumento} " +
	        "AND B06_NUM_DOCUMENTO = #{numeroDocumento})")
	public void actualizarEmailYCelularTarjeta(
	        @Param("tipoDocumento") String tipoDocumento,
	        @Param("numeroDocumento") String numeroDocumento,
	        @Param("email") String email,
	        @Param("numcelular") String numcelular
	);

    
    @Select("SELECT * FROM BN_SATE.BNSATE06_CLIENTE WHERE " +
			"B06_ID_CLI = #{idCliente}  ")
	@ResultMap("mapCliente")
	public Cliente buscarClientePorId(@Param("idCliente")Long  idCliente);
	
	
    @Select("select count(*) " +
    		"from BN_SATE.BNSATE05_TARJETA tar " +
    		"join BN_SATE.BNSATE00_EMPRESA empre on " +
    		"tar.B00_ID_EMP = empre.B00_ID_EMP " +
    		"join BN_SATE.BNSATE06_CLIENTE cliente " +
    		"on cliente.B06_ID_CLI = tar.B06_ID_CLI " +
    		"where empre.B00_NUM_RUC = #{ruc} " +
    		"and cliente.B06_TIPO_DOCUMENTO = #{tipoDocumento} " +
    		"and cliente.B06_NUM_DOCUMENTO = #{numDocumento} " +
    		"and tar.B05_NUM_TARJETA is not null")	
    public long consultarExisteClienteRUC(
    		@Param("tipoDocumento")String tipoDocumento,
    		@Param("numDocumento")String numDocumento,
    		@Param("ruc")String ruc
    		);

    @Select("SELECT TAR.B05_NUM_CELULAR " +
            "FROM bn_sate.BNSATE05_TARJETA TAR " +
            "JOIN bn_sate.BNSATE06_CLIENTE CLIE " +
            "ON TAR.B06_ID_CLI = CLIE.B06_ID_CLI " +
            "WHERE CLIE.B06_TIPO_DOCUMENTO = #{tipoDocumento} " +
            "AND CLIE.B06_NUM_DOCUMENTO = #{numDocumento} " +
            "AND TAR.B05_NUM_TARJETA IS NOT NULL " +
            "ORDER BY TAR.B05_FEC_CREACION DESC " +
            "FETCH FIRST ROW ONLY")
    public String buscarClienteNumCel(@Param("tipoDocumento") String tipoDocumento,
                                      @Param("numDocumento") String numDocumento);

	
    
	
}
