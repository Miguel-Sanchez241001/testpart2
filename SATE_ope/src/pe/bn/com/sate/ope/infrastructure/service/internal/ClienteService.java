package pe.bn.com.sate.ope.infrastructure.service.internal;

import pe.bn.com.sate.ope.transversal.dto.sate.Cliente;


public interface ClienteService {

	public Cliente buscarCliente(String tipoDocumento,String numDocumento);
	
	public void actualizarCliente(Cliente cliente);
	
	public void actualizarClienteBD(String tipoDocumento, String numDocumento, String teleno, String email);

	public Cliente buscarClientePorId( Long  idCliente);
	
}
