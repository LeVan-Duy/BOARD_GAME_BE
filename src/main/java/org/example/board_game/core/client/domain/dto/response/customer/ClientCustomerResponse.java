package org.example.board_game.core.client.domain.dto.response.customer;

import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.infrastructure.enums.CustomerStatus;
import org.example.board_game.infrastructure.enums.Gender;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientCustomerResponse {

    String id;

    String fullName;

    String email;

    Long dateOfBirth;

    String password;

    CustomerStatus status;

    Gender gender;

    String image;

    List<ClientAddressResponse> addressList;

    public ClientCustomerResponse(Tuple tuple) {
        this.id = tuple.get("id",String.class);
        this.fullName = tuple.get("fullName",String.class);
        this.email = tuple.get("email",String.class);
        this.dateOfBirth = tuple.get("dateOfBirth",Long.class);
        this.password = tuple.get("password",String.class);
        this.status = tuple.get("status",CustomerStatus.class);
        this.gender = tuple.get("gender",Gender.class);
        this.image = tuple.get("image",String.class);
    }

}
