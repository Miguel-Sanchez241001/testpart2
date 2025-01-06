package pe.bn.com.sate.ope.transversal.dto.sate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.bn.com.sate.ope.infrastructure.service.external.domain.reniec.Identidad2;
import pe.bn.com.sate.ope.transversal.util.enums.EstadoCivil;
import pe.bn.com.sate.ope.transversal.util.enums.Sexo;
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Cliente {

	private Long id;
	private String nombres;
	private String apPaterno;
	private String apMaterno; //null
	private String tipoDocumento;
	private String nroDocumento;
	private Date fechaNacimiento;
	private String estadoCivil;
	private String sexo;
	private String telefonoCasa;
	private String direccion;//null
	private String ubigeo;//null
	private String referencia;//null
	private String numCelular;//null
	private String apCompleto;



	public Cliente(Identidad2 vIdentidad2, String tipoDocumento) {
		this.nombres = vIdentidad2.getNombres().trim();
		this.apPaterno = vIdentidad2.getApellidoPaterno().trim();
		this.apMaterno = vIdentidad2.getApellidoMaterno().trim();
		this.tipoDocumento = tipoDocumento;
		this.nroDocumento = vIdentidad2.getNumDoc().trim();
		this.direccion = vIdentidad2.getDireccion().trim();
		this.ubigeo = vIdentidad2.getDomicDptoCod().concat(
				vIdentidad2.getDomicProvCod().concat(
						vIdentidad2.getDomicDistCod()));
		this.estadoCivil = EstadoCivil.obtenerCodigoTrama(vIdentidad2
				.getEstadoCivilCod());
		this.sexo = Sexo.obtenerCodigoTrama(vIdentidad2.getSexoCod());
		try {
			this.fechaNacimiento = new SimpleDateFormat("yyyyMMdd")
					.parse(vIdentidad2.getFechaNacimiento());
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
	}
	
	


}
