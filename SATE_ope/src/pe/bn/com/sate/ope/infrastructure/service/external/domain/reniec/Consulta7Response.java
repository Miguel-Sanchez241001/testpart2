//
// Generated By:JAX-WS RI IBM 2.2.1-11/25/2013 11:48 AM(foreman)- (JAXB RI IBM 2.2.3-11/25/2013 12:35 PM(foreman)-)
//


package pe.bn.com.sate.ope.infrastructure.service.external.domain.reniec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="consulta7Return" type="{http://bean.mq2.bn}Identidad2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "consulta7Return"
})
@XmlRootElement(name = "consulta7Response")
public class Consulta7Response {

    @XmlElement(required = true, nillable = true)
    protected Identidad2 consulta7Return;

    /**
     * Gets the value of the consulta7Return property.
     * 
     * @return
     *     possible object is
     *     {@link Identidad2 }
     *     
     */
    public Identidad2 getConsulta7Return() {
        return consulta7Return;
    }

    /**
     * Sets the value of the consulta7Return property.
     * 
     * @param value
     *     allowed object is
     *     {@link Identidad2 }
     *     
     */
    public void setConsulta7Return(Identidad2 value) {
        this.consulta7Return = value;
    }

}
