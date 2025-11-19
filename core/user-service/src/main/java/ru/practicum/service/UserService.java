package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.request.NewUserRequestDto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // MODIFY OPS

    @Transactional(readOnly = false)
    public UserDto create(NewUserRequestDto newUserRequestDto) {
        if (userRepository.existsByEmail(newUserRequestDto.getEmail())) {
            throw new ConflictException("User with email " + newUserRequestDto.getEmail() + " already exists",
                    "Integrity constraint has been violated");
        }
        User newUser = UserMapper.toEntity(newUserRequestDto);
        userRepository.save(newUser);
        return UserMapper.toDto(newUser);
    }

    @Transactional(readOnly = false)
    public void delete(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        userRepository.delete(userToDelete);
    }

    // GET COLLECTION

    public List<UserDto> findByIdListWithOffsetAndLimit(List<Long> idList, Integer from, Integer size) {
        if (idList == null || idList.isEmpty()) {
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            return userRepository.findAll(PageRequest.of(from / size, size, sort))
                    .stream()
                    .map(UserMapper::toDto)
                    .toList();
        } else {
            return userRepository.findAllById(idList)
                    .stream()
                    .map(UserMapper::toDto)
                    .toList();
        }
    }

    public UserDto existUserById(Long userId) {
        return UserMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с id %d не существует"))));
    }

    public Map<Long, UserDto> getAllBySetIds(Set<Long> ids) {
        List<User> users = userRepository.findByIdIn(ids);
        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        UserMapper::toDto
                ));
    }

    public List<UserDto> getAllByListIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserShortDto findByIdShort(Long userId) {
        User findById = userRepository.findById(userId).get();

        return UserMapper.toUserShortDto(findById);
    }

    public Map<Long, UserShortDto> findAllByIdsShort(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);


        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        UserMapper::toUserShortDto
                ));
    }

    public List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (ids.isEmpty()) {
            return userRepository.findAll(pageRequest).getContent().stream()
                    .map(UserMapper::toDto)
                    .toList();
        } else {
            return userRepository.findAllByIdIn(ids, pageRequest).getContent().stream()
                    .map(UserMapper::toDto)
                    .toList();
        }
    }
}
