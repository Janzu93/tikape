package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Viestiketju;

/**
 *
 * @author janne
 */

public class ViestiketjuDao implements Dao<Viestiketju, Integer> {

    private Database database;

    public ViestiketjuDao(Database database) {
        this.database = database;
    }

    @Override
    public Viestiketju findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE id = ?;");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimi");

        Viestiketju kayttaja = new Viestiketju(id, nimi);

        rs.close();
        stmt.close();
        connection.close();

        return kayttaja;
    }

    @Override
    public List<Viestiketju> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju;");

        ResultSet rs = stmt.executeQuery();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("otsikko");

            viestiketjut.add(new Viestiketju(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestiketjut;
    }
    
    
    public List<Viestiketju> findAll(int aihealueId) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE aihealue_id = ?;");
        stmt.setObject(1, aihealueId);

        ResultSet rs = stmt.executeQuery();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("otsikko");

            viestiketjut.add(new Viestiketju(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestiketjut;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        
        // W.I.P
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE * FROM Viestiketju WHERE id = ?;");

    }
    public void create(String nimi, int a) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Viestiketju(otsikko, aihealue_id) VALUES(?, ?)");
        stmt.setObject(1, nimi);
        stmt.setObject(2, a);
        
        stmt.execute();
        
        
    }

}
