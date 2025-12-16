package repository;

import component.User;
import java.util.List;

public interface UserRepository {
    User findByCredentials(String username, String password);
    User create(String username, String email, String password);
    List<User> findAllExcept(int userId);
    void delete(int userId);
    boolean isAdmin(int userId);
}
