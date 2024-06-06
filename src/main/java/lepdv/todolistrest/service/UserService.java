package lepdv.todolistrest.service;

import lepdv.todolistrest.dto.auth.CredentialsDto;
import lepdv.todolistrest.dto.auth.RegisterDto;
import lepdv.todolistrest.dto.user.EditPasswordDto;
import lepdv.todolistrest.dto.user.UserDto;
import lepdv.todolistrest.entity.Role;
import lepdv.todolistrest.entity.User;
import lepdv.todolistrest.exception.NotFoundException;
import lepdv.todolistrest.exception.UnitedException;
import lepdv.todolistrest.mapper.Mapper;
import lepdv.todolistrest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static lepdv.todolistrest.util.AuthUser.getAuthUsername;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }



    @Transactional
    public void register(RegisterDto registerDto) {

        User user = mapper.mapToUser(registerDto);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);
        user.setIsNonLocked(true);

        User savedUser = userRepository.save(user);
        log.info("New user was registered id={}, username={}", savedUser.getId(), savedUser.getUsername());
    }



    public List<User> getAllByPageable(Pageable pageable) {
        return userRepository.findAllByOrderByUsername(pageable);
    }



    public UserDto getUserDto() {
        return mapper.mapToUserDto(getAuthUser());
    }



    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no user with id=" + id + " in database"));
    }



    public Optional<User> getByUsername(String username) { return userRepository.findByUsername(username); }



    @Transactional
    public UserDto update(UserDto userDto) {
        User authUser = getAuthUser();
        Optional.ofNullable(userDto.getUsername()).ifPresent(authUser::setUsername);
        Optional.ofNullable(userDto.getFullName()).ifPresent(authUser::setFullName);
        Optional.ofNullable(userDto.getDateOfBirth()).ifPresent(authUser::setDateOfBirth);
        log.info("User id={} was updated", authUser.getId());
        return mapper.mapToUserDto(authUser);
    }



    @Transactional
    public void editPassword(EditPasswordDto editPasswordDto) {
        User authUser = getAuthUser();
        if (editPasswordDto.getUsername().equals(authUser.getUsername()) &&
                passwordEncoder.matches(editPasswordDto.getOldPassword(), authUser.getPassword())) {

            String encodedPassword = passwordEncoder.encode(editPasswordDto.getNewPassword());
            authUser.setPassword(encodedPassword);
            log.info("User id={} password was edited", authUser.getId());

        } else {
            throw new UnitedException("Incorrect credentials");
        }
    }



    @Transactional
    public void deleteUser(CredentialsDto credentialsDto) {
        User authUser = getAuthUser();
        if (credentialsDto.getUsername().equals(authUser.getUsername()) &&
                passwordEncoder.matches(credentialsDto.getPassword(), authUser.getPassword())) {

            userRepository.delete(authUser);
            log.info("User id={}, username={} was deleted", authUser.getId(), authUser.getUsername());
        } else {
            throw new UnitedException("Incorrect credentials");
        }
    }


    @Transactional
    public void deleteUserById(Long id) {
        User user = getUser(id);
        userRepository.deleteById(id);
        log.info("User id={}, username={} was deleted", id, user.getUsername());
    }



    public User getAuthUser() {
        return getByUsername(getAuthUsername()).get();
    }



}
