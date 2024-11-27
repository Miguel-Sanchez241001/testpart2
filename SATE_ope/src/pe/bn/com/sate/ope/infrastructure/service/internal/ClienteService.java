package pe.bn.com.sate.ope.infrastructure.service.internal;

import org.apache.ibatis.annotations.Param;

import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;


public interface ClienteService {

	public Cliente buscarCliente(String tipoDocumento,String numDocumento);
	
	public void actualizarCliente(Cliente cliente);
	
	public void actualizarClienteBD(String tipoDocumento, String numDocumento, String teleno, String email, String celular);

	public Cliente buscarClientePorId( Long  idCliente);
	
	public long consultarExisteClienteRUC(String tipoDocumento,String numDocumento,String ruc);

	public String buscarClienteNumCel(String tipoBusqueda, String numDocumento);
	
	

}
