package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "user", key = "#user.getId()")
    // <1>
    public User createUser(User user) {

        User result = null;

        if (!userRepository.existsById(user.getId())) {
            result = userRepository.save(user);
        }

        return result;
    }

    @Cacheable(value = "user")
    // <2>
    public User getUser(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @CachePut(value = "user", key = "#id")
    // <3>
    public User updateUser(String id, User user) {

        User result = null;

        if (userRepository.existsById(user.getId())) {
            result = userRepository.save(user);
        }

        return result;
    }

    @CacheEvict(value = "user", key = "#id")
    // <4>
    public boolean deleteUser(String id) {

        boolean deleted = false;

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            deleted = true;
        }

        return deleted;
    }
}
