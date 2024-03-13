package dataAccess.ram;

import dataAccess.UserDAO;
import model.bean.UserBean;

import java.util.HashMap;
import java.util.Map;

public class RAMUserDAO implements UserDAO {

    private static final UserDAO instance = new RAMUserDAO();
    private final Map<String, UserBean> table = new HashMap<>();

    public static UserDAO getInstance() {
        return instance;
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
    }
}
