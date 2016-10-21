/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Aihealue;
import tikape.runko.domain.Viesti;

public class AihealueDao implements Dao<Aihealue, Integer> {

    private Database database;

    public AihealueDao(Database database) {
        this.database = database;
    }

    @Override
    public Aihealue findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihealue WHERE id = ?;");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String nimi = rs.getString("otsikko");
        Viesti uusinViesti = getNewestPost(id);

        Aihealue aihealue = new Aihealue(id, nimi, calculatePostCount(id), uusinViesti);

        rs.close();
        stmt.close();
        connection.close();

        return aihealue;
    }

    @Override
    public List<Aihealue> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Aihealue;");

        ResultSet rs = stmt.executeQuery();
        List<Aihealue> aihealueet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String nimi = rs.getString("otsikko");
            Viesti uusinViesti = getNewestPost(id);

            aihealueet.add(new Aihealue(id, nimi, calculatePostCount(id), uusinViesti));
        }

        rs.close();
        stmt.close();
        connection.close();

        return aihealueet;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Aihealue WHERE id = ?;");
        stmt.setObject(1, key);
        stmt.execute();
        
        stmt.close();
        
        //poistetaan myös viestiketjut, ks viestiketjuDao 
        ViestiketjuDao vkd = new ViestiketjuDao(this.database);
        stmt = conn.prepareStatement("SELECT * FROM Viestiketju WHERE aihealue_id = ?;");
        stmt.setObject(1, key);
        ResultSet rs = stmt.executeQuery();
        
        stmt.close();
        
        while (rs.next()) {
            Integer id = rs.getInt("id");
            vkd.delete(id);
        }
        stmt.close();
        conn.close();

    }
    
    public int calculatePostCount(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(Viesti.id) as postCount FROM Viestiketju, Viesti WHERE Viesti.viestiketju_id = Viestiketju.id AND Viestiketju.aihealue_id = ?");
        stmt.setObject(1, key);
         
        ResultSet rs = stmt.executeQuery();
        Integer count = rs.next() ? rs.getInt("postCount") : 0;

        rs.close();
        stmt.close();
        conn.close();

        return count;
    }

    public void create(String nimi) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Aihealue(otsikko) VALUES(?)");
        stmt.setObject(1, nimi);

        stmt.execute();
        stmt.close();
        conn.close();

    }
    
    private Viesti getNewestPost(int aihealueID)  throws SQLException  {
        Connection conn = database.getConnection();
      
        // tää hakee kaikki 'ketjuID' kuuluvat viestit, sorttaa ne ajan mukaan ja palauttaa vaan uusimman ('LIMIT 1')
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Viestiketju, Viesti WHERE Viesti.viestiketju_id = Viestiketju.id AND Viestiketju.aihealue_id = ? ORDER BY Viesti.aika DESC LIMIT 1");
        stmt.setObject(1, aihealueID);

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
