package dataaccess.ram;

import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.bean.UserBean;

import java.util.HashMap;
import java.util.Map;

public class RAMUserDAO implements UserDAO {

    private static final UserDAO INSTANCE = new RAMUserDAO();
    private final Map<String, UserBean> table = new HashMap<>();

    private RAMUserDAO() {
        table.put(DatabaseManager.AI_USERNAME, new UserBean(DatabaseManager.AI_USERNAME, "aiPassw0rd", "ai"));
    }

    public static UserDAO getInstance() {
        return INSTANCE;
    }

    @Override
    public void insert(UserBean bean) {
        table.put(bean.getUsername(), bean);
    }

    @Override
    public UserBean find(String username) {
        return table.get(username);
    }

    @Override
    public void update(UserBean bean) {
        insert(bean);
    }

    @Override
    public void delete(String username) {
        table.remove(username);
    }

    @Override
    public void clear() {
        table.clear();
        table.put(DatabaseManager.AI_USERNAME, new UserBean(DatabaseManager.AI_USERNAME, "aiPassw0rd", "ai"));
    }
}
