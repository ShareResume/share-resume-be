package share.resume.com.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.RoleEnum;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nick;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<UserCommentVoteStateEntity> userCommentVoteStates;
}
