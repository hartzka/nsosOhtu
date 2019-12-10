package ohtu.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import ohtu.domain.Work;
import ohtu.dao.WorkDao;
import ohtu.db.DatabaseManager;
import ohtu.domain.WorkType;

public class WorkDaoImpl implements WorkDao {

    private DatabaseManager db;
    private Map<Integer, Work> works;

    public WorkDaoImpl(DatabaseManager db) {
        this.db = db;
        works = new HashMap<>();
        list();
    }

    @Override
    public Work create(Work work) {
        try {
            Connection conn = db.openConnection();
            PreparedStatement s = prepareStatement(conn, work);
            s.executeUpdate();
            ResultSet keys = s.getGeneratedKeys();
            setWorkId(keys, work);
            list();
            return work;
        } catch (SQLException ex) {
            Logger.getLogger(WorkDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return work;
    }

    private PreparedStatement prepareStatement(Connection conn, Work work) throws SQLException {
        PreparedStatement s = conn.prepareStatement("insert into Work ("
                + " author,"
                + " title,"
                + " code,"
                + " tags,"
                + " type,"
                + " read,"
                + " pages,"
                + " current_page"
                + ") values (?, ?, ?, ?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
        s.setString(1, work.getAuthor());
        s.setString(2, work.getTitle());
        s.setString(3, work.getCode());
        s.setString(4, work.getTags());
        s.setString(5, work.getType().toString());
        s.setBoolean(6, work.getRead());
        s.setInt(7, work.getPages());
        s.setInt(8, work.getCurrentPage());
        return s;
    }

    @Override
    public boolean delete(Integer key) {
        PreparedStatement stmt;
        try {
            Connection connection = db.openConnection();
            stmt = connection.prepareStatement("DELETE FROM Work WHERE id = ?");

            stmt.setInt(1, key);
            if (stmt.executeUpdate() == 0) {
                return false;
            }

            stmt.close();
            connection.close();
            list();
        } catch (SQLException ex) {
            Logger.getLogger(WorkDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public List<Work> list() {
        List<Work> ret = new ArrayList<>();
        try {
            Connection conn = db.openConnection();
            PreparedStatement s = conn.prepareStatement("select * from Work;");
            ResultSet results = s.executeQuery();
            while (results.next()) {
                Work work = new Work(
                        results.getInt("id"),
                        results.getString("author"),
                        results.getString("title"),
                        results.getString("code"),
                        results.getString("tags"),
                        mapType(results.getString("type")),
                        results.getBoolean("read"),
                        results.getInt("pages"),
                        results.getInt("current_page")
                );
                ret.add(work);
            }
            conn.close();
        } catch (SQLException e) {
            System.err.println("Database exception: " + e.getMessage());
        }
        populateMap(ret);
        return ret;
    }

    private void populateMap(List<Work> list) {
        works.clear();
        for (Work work : list) {
            works.put(work.getId(), work);
        }
    }

    private WorkType mapType(String str) {
        for (WorkType value : WorkType.values()) {
            if (value.toString().equalsIgnoreCase(str)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Work read(Integer key) {
        Work work = null;
        PreparedStatement stmt;
        try {
            Connection connection = db.openConnection();
            stmt = connection.prepareStatement("SELECT * FROM Work WHERE id = ?");

            stmt.setInt(1, key);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

            work = new Work(rs.getString("author"),
                    rs.getString("title"), rs.getString("code"), rs.getInt("pages"),
                    rs.getString("tags"),
                    mapType(rs.getString("type"))
            );

            work.setId(rs.getInt("id"));
            work.setCurrentPage(rs.getInt("current_page"));
            work.setRead(rs.getBoolean("read"));

            stmt.close();
            rs.close();
            works.put(work.getId(), work);
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(WorkDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return work;
    }

    @Override
    public List<Work> searchByTag(String tag) {
        List<String> list = new ArrayList<String>();
        list.add(tag);
        List<Work> results = searchByTag(list);
        return results;
    }

    @Override
    public List<Work> searchByTag(List<String> tags) {
        List<Work> results = new ArrayList<>();
        for (Work stored : works.values()) {
            if (containsSubstrings(tags, stored.getTags())) {
                Work copy = new Work(stored.getAuthor(), stored.getTitle(), stored.getCode(), stored.getPages(), stored.getTags(), stored.getType());
                copy.setId(stored.getId());
                results.add(copy);
            }
        }
        return results;
    }

    /**
     * A logical AND substring search
     * @param needles the list of substrings to look for
     * @param haystack the string to search
     */
    private boolean containsSubstrings(List<String> needles, String haystack) {
        for (String needle : needles) {
            if (!haystack.contains(needle)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Work update(Work work) {
        String sql = "UPDATE Work SET author = ? , "
                + "title = ? , "
                + "code = ? , "
                + "tags = ? , "
                + "type = ? , "
                + "read = ? , "
                + "pages = ? , "
                + "current_page = ? "
                + "WHERE id = ?";

        try (Connection conn = db.openConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, work.getAuthor());
            stmt.setString(2, work.getTitle());
            stmt.setString(3, work.getCode());
            stmt.setString(4, work.getTags());
            stmt.setString(5, work.getType().toString());
            stmt.setBoolean(6, work.getRead());
            stmt.setInt(7, work.getPages());
            stmt.setInt(8, work.getCurrentPage());
            stmt.setInt(9, work.getId());

            stmt.executeUpdate();
            conn.close();
            stmt.close();
            list();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return work;
    }

    private void setWorkId(ResultSet keys, Work work) throws SQLException {
        int id = -1;
        if (keys.next()) {
            id = keys.getInt(1);
        }
        work.setId(id);
    }
}
