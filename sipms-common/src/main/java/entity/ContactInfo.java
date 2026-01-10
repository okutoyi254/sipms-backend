package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class ContactInfo implements Serializable {

    private static final long serialVersionID =1L;

    @Column(name = "email",length = 100)
    private String email;

    @Column(name = "phone_number",length = 20)
    private String phoneNumber;


}
