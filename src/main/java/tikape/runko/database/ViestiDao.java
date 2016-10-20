package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Viesti;

/**
 *
 * @author janne
 */
public class ViestiDao implements Dao<Viesti, Integer> {

    private Database database;

    public ViestiDao(Database database) {
        this.database = database;
    }

    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?;");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String teksti = rs.getString("teksti");
        String aika = rs.getTimestamp("aika").toString();

        Viesti viesti = new Viesti(id, teksti, aika);

        rs.close();
        stmt.close();
        connection.close();

        return viesti;
    }

    @Override
    public List<Viesti> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti;");

        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String teksti = rs.getString("teksti");
            String aika = rs.getTimestamp("aika").toString();

            viestit.add(new Viesti(id, teksti, aika));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }

    public List<Viesti> findAll(int ketjuId) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE viestiketju_id = ?;");
        stmt.setObject(1, ketjuId);

        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String teksti = rs.getString("teksti");
            String aika = rs.getString("aika");

            viestit.add(new Viesti(id, teksti, (aika == null) ? "aika on null" : aika));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }

    @Override
    public void delete(Integer key) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Viesti WHERE id = ?;");

        stmt.setObject(1, key);

        stmt.execute();
        stmt.close();
        conn.close();
    }

    public void create(String nimi, int ketjuId) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Viesti(teksti, viestiketju_id) VALUES(?, ?)");
        stmt.setObject(1, nimi);
        stmt.setObject(2, ketjuId);

        stmt.execute();
        stmt.close();
        conn.close();

    }
    
    //Haetaan ketjuId viestiId:n perusteella, poista komennon uudelleenohjaamista varten
    public int getKetjuId(int viestiId) throws SQLException{
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?;");
        stmt.setObject(1, viestiId);
        
        ResultSet rs = stmt.executeQuery();
        int ketjuId = -1;
        while (rs.next()) {
            ketjuId = rs.getInt("viestiketju_id");
        }

        rs.close();
        stmt.close();
        connection.close();

        return ketjuId;
    }

}
