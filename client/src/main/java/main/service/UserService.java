package main.service;

import main.dao.User.UserDAO;
import main.dao.User.UserDAOImpl;
import main.model.User;
import java.util.List;

public class UserService {
    private final UserDAO dao = new UserDAOImpl();

    public List<User> getAllUsers() {
        return dao.getAllUsers();
    }

    public User getUserById(int id) {
        return dao.getUserById(id);
    }

    public void addUser(User user) {
        dao.addUser(user);
    }

    public void updateUser(User user) {
        dao.updateUser(user);
    }

    public void deleteUser(int id) {
        dao.deleteUser(id);
    }
}