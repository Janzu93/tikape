package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Kayttaja;

/**
 *
 * @author janne
 */
public class KayttajaDao implements Dao<Kayttaja, Integer> {

    private Database database;

    public KayttajaDao(Database database) {
        this.database = database;
    }

    @Override
    public Kayttaja findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE id = ?;");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimimerkki");
        String salt = rs.getString("salt");
        String hash = rs.getString("hash");
        Integer tyyppi = rs.getInt("tyyppi");

        Kayttaja kayttaja = new Kayttaja(id, nimi, salt, hash, tyyppi);

        rs.close();
        stmt.close();
        connection.close();

        return kayttaja;
    }

    public Kayttaja findOne(String key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja WHERE nimimerkki = ?;");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("nimimerkki");
        String salt = rs.getString("salt");
        String hash = rs.getString("hash");
        Integer tyyppi = rs.getInt("tyyppi");

        Kayttaja kayttaja = new Kayttaja(id, nimi, salt, hash, tyyppi);

        rs.close();
        stmt.close();
        connection.close();

        return kayttaja;
    }

    @Override
    public List<Kayttaja> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Kayttaja;");

        ResultSet rs = stmt.executeQuery();
        List<Kayttaja> kayttajat = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("nimimerkki");
            String salt = rs.getString("salt");
            String hash = rs.getString("hash");
            Integer tyyppi = rs.getInt("tyyppi");

            Kayttaja kayttaja = new Kayttaja(id, nimi, salt, hash, tyyppi);
        }

        rs.close();
        stmt.close();
        connection.close();

        return kayttajat;
    }

    @Override
    public void delete(Integer key) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE * FROM Kayttaja WHERE id = ?;");
        stmt.setObject(1, key);

        stmt.execute();
        conn.close();

    }

    public void create(String nimi, String salt, String hash) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Kayttaja(nimimerkki, salt, hash) VALUES(?, ?, ?)");
        stmt.setObject(1, nimi);
        stmt.setObject(2, salt);
        stmt.setObject(3, hash);

        stmt.execute();
        conn.close();

    }

}
