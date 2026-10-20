package main.service;

import dao.User.UserDAO;
import dao.User.UserDAOImpl;
import model.User;
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