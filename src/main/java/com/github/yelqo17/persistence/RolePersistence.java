package com.github.yelqo17.persistence;

import com.github.yelqo17.database.MyDataBase;

import java.util.Map;
public class RolePersistence {

    private final MyDataBase db = MyDataBase.getInstance();

    private static final String TABLE_NAME = "role";

    private static final String ID_NAME = "id";

    private static final String ROLE_NAME = "name";

    public void createRole(String role_name) {
        String sql = """
                insert into mafia.role
                (name)
                values
                ('%s')
                """;
        db.execute(String.format(sql, role_name));
    }

    public String getById(int role_id) {
        Map<String, String> fromDB = db.selectById(role_id, TABLE_NAME, ID_NAME, ROLE_NAME);
        if (fromDB == null || fromDB.isEmpty()) {
            return null;
        }
        return convertRole(fromDB);
    }

    protected String convertRole(Map<String, String> fromDB) {
        return String.valueOf(fromDB.get(ROLE_NAME));
    }
}