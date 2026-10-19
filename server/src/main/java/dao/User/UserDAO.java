package dao.User;

import model.User;
import java.util.List;

public interface UserDAO {
    List<User> getAllUsers();
    User getUserById(int id);
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(int id);
}