/*
 * @author londes
 */
package ohtu.dao.impl;

import java.util.List;
import ohtu.domain.Work;
import ohtu.dao.WorkDao;
import ohtu.db.DatabaseManager;

public class WorkDaoImpl implements WorkDao {

    private DatabaseManager db;

    public WorkDaoImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Work create(Work work) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean delete(Integer key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Work> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Work read(Integer key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Work> searchByTag(String tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Work> searchByTag(List<String> tags) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Work update(Work work) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
