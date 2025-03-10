package share.resume.com.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.controllers.dto.request.CreateUserRequestBody;
import share.resume.com.entities.UserEntity;
import share.resume.com.entities.enums.RoleEnum;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.UserRepository;
import share.resume.com.security.dto.UserDetailsDto;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public UserDetailsDto getUserByEmail(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User with email " + email + " not found");
        }
        return new UserDetailsDto(userOpt.get());
    }

    @Transactional
    public void createUser(CreateUserRequestBody createUserRequestBody) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(createUserRequestBody.getEmail());
        userEntity.setPassword(passwordEncoder.encode(createUserRequestBody.getPassword()));
        userEntity.setNick("user" + UUID.randomUUID());
        userEntity.setRole(RoleEnum.USER);
        userRepository.save(userEntity);
    }
}
