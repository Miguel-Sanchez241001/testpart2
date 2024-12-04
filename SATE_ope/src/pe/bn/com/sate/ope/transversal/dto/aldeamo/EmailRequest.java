package pe.bn.com.sate.ope.transversal.dto.aldeamo;

import java.util.List;

import lombok.Data;

@Data
public class EmailRequest {
    private List<Object> attachments;
    private String body;
    private Person replyTo;
    private Person from;
    private List<Recipient> to;
    private String subject;
}
