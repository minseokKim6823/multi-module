package practice.moduleuser.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.moduleuser.user.dto.UserCreateRequest;
import practice.moduleuser.user.dto.UserUpdateRequest;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional
public class UserCrudService {
    private final UserRepository userRepository;

    public User create(UserCreateRequest req) {
        if (req.email() == null || req.email().isBlank()) {
            throw new IllegalArgumentException("email은 필수입니다.");
        }
        if (userRepository.existsByEmailAndDeletedAtIsNull(req.email())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(req.email())
                .password(req.password())
                .name(req.name())
                .role(req.role() != null ? req.role() : UserRole.USER)
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User get(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<User> list(Pageable pageable) {
        return userRepository.findAllByDeletedAtIsNull(pageable);
    }

    public User update(Long id, UserUpdateRequest req) {
        User user = get(id);
        if (req.name() != null) user.setName(req.name());
        if (req.password() != null) user.setPassword(req.password());
        if (req.role() != null) user.setRole(req.role());
        return user;
    }

    public void delete(Long id) {
        User user = get(id);
        user.setDeletedAt(LocalDateTime.now());
    }
}
