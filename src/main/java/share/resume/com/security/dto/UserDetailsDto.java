package share.resume.com.security.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import share.resume.com.entities.UserEntity;
import share.resume.com.entities.enums.RoleEnum;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDetailsDto implements UserDetails {
    private UUID id;
    private String nick;
    private String email;
    private String password;
    private RoleEnum role;
    private UserEntity userEntity;

    public UserDetailsDto(UserEntity user) {
        this.id = user.getId();
        this.nick = user.getNick();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.userEntity = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
