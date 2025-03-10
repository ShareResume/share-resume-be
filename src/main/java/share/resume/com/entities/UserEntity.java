package share.resume.com.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.RoleEnum;

import java.util.UUID;

@Entity
@Table(name = "USERS")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nick;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
