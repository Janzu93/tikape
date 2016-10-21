package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import tikape.runko.domain.Viesti;
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
        String nimi = rs.getString("otsikko");

        Viestiketju kayttaja = new Viestiketju(id, nimi, countViestit(id), getNewestPost(id));

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

            viestiketjut.add(new Viestiketju(id, nimi, countViestit(id), getNewestPost(id)));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestiketjut;
    }

    public List<Viestiketju> findAll(int aihealueId) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE aihealue_id = ?");
        stmt.setObject(1, aihealueId);

        ResultSet rs = stmt.executeQuery();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("otsikko");

            viestiketjut.add(new Viestiketju(id, nimi, countViestit(id), getNewestPost(id)));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestiketjut;
    }

    // palauttaa sortattuna uusimpien viestien perusteella
    public List<Viestiketju> findAllFromAihealue(int aihealueId) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viestiketju WHERE aihealue_id = ?");
        stmt.setObject(1, aihealueId);

        ResultSet rs = stmt.executeQuery();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("otsikko");
            viestiketjut.add(new Viestiketju(id, nimi, countViestit(id), getNewestPost(id)));
        }

        rs.close();
        stmt.close();
        connection.close();

        // sortataan sen mukaan milloin uusin viesti on tullut. kai tän vois tehä SQL:ssäkin, mä_en_osaa
        viestiketjut.sort(Comparator.comparing(ketju -> ketju.getUusinViesti().getAika(), Comparator.reverseOrder()));
        return viestiketjut;
    }

    public List<Viestiketju> findAllFromAihealue(int aihealueId, int offset) throws SQLException {

        Connection connection = database.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM Viestiketju LEFT JOIN Viesti ON Viesti.viestiketju_id = Viestiketju.id WHERE Viestiketju.aihealue_id = ? GROUP BY Viestiketju.id ORDER BY Viesti.id DESC LIMIT 10 OFFSET ?");
        stmt.setObject(1, aihealueId);
        stmt.setObject(2, offset);

        ResultSet rs = stmt.executeQuery();
        List<Viestiketju> viestiketjut = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("otsikko");
            viestiketjut.add(new Viestiketju(id, nimi, countViestit(id), getNewestPost(id)));
        }

        rs.close();
        stmt.close();
        connection.close();

        // sortataan sen mukaan milloin uusin viesti on tullut. kai tän vois tehä SQL:ssäkin, mä_en_osaa
      //  viestiketjut.sort(Comparator.comparing(ketju -> ketju.getUusinViesti().getAika(), Comparator.reverseOrder()));
        return viestiketjut;
    }

    @Override
    public void delete(Integer key) throws SQLException {

        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Viestiketju WHERE id = ?");

        stmt.setObject(1, key);

        stmt.execute();
        stmt.close();

        //poistetaan myös kaikki viestiketjuun liittyvät viestit (toiminnalisuuden voi siirtää myös mainiin, mutta siistimpi täällä)
        ViestiDao vd = new ViestiDao(this.database);
        stmt = conn.prepareStatement("SELECT * FROM Viesti WHERE viestiketju_id = ?");
        stmt.setObject(1, key);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Integer id = rs.getInt("id");
            vd.delete(id);
        }
        stmt.close();
        conn.close();

    }

    public void create(String nimi, int a) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Viestiketju(otsikko, aihealue_id) VALUES(?, ?)");
        stmt.setObject(1, nimi);
        stmt.setObject(2, a);

        stmt.execute();
        stmt.close();
        conn.close();

    }

    public int findAihealueId(int vkId) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Viestiketju WHERE id = ?");
        stmt.setObject(1, vkId);

        ResultSet rs = stmt.executeQuery();
        Integer id = -1;

        while (rs.next()) {
            id = rs.getInt("aihealue_id");
        }

        stmt.close();
        conn.close();

        return id;
    }

    // Tämän metodin pitäisi toimia, miten hyödynnetään viestien laskussa
    public int countViestit(int vkId) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS lkm FROM Viesti WHERE Viesti.viestiketju_id = ?");
        stmt.setObject(1, vkId);

        ResultSet rs = stmt.executeQuery();
        Integer viestiLkm = -1;

        while (rs.next()) {
            viestiLkm = rs.getInt("lkm");
        }
        stmt.close();
        conn.close();
        return viestiLkm;
    }

    private Viesti getNewestPost(int ketjuId) throws SQLException {
        Connection conn = database.getConnection();

        // tää hakee kaikki 'ketjuID' kuuluvat viestit, sorttaa ne ajan mukaan ja palauttaa vaan uusimman ('LIMIT 1')
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Viestiketju, Viesti WHERE Viesti.viestiketju_id = Viestiketju.id AND Viestiketju.id = ? ORDER BY Viesti.aika DESC LIMIT 1");
        stmt.setObject(1, ketjuId);

        ResultSet rs = stmt.executeQuery();
        Integer viestiID = -1;
        String teksti = "";
        String aika = "";
        Integer lahettajaId = 0;

        while (rs.next()) {
            viestiID = rs.getInt("id");
            teksti = rs.getString("teksti");
            aika = rs.getString("aika");
            lahettajaId = rs.getInt("kayttaja_id");
        }

        stmt.close();
        conn.close();
        return new Viesti(viestiID, teksti, aika, lahettajaId);
    }

}
