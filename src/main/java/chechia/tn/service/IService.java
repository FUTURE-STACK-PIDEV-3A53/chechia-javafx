package chechia.tn.service;

import java.sql.SQLException;
import java.util.List;
import chechia.tn.entities.Opportunite;
import chechia.tn.entities.Candidature;

public interface IService<T> {
    void add(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(T t) ;
    List<T> afficher() throws SQLException;

}