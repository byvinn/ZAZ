package service;

import component.User;
import repository.UserRepository;

import java.util.List;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        return userRepository.findByCredentials(username, password);
    }

    public User register(String username, String email, String password) {
        return userRepository.create(username, email, password);
    }

    public List<User> getAllUsersExcept(int userId) {
        return userRepository.findAllExcept(userId);
    }

    public void deleteUser(int userId) {
        userRepository.delete(userId);
    }

    public boolean isAdmin(int userId) {
        return userRepository.isAdmin(userId);
    }
}
