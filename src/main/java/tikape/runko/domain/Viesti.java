package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Viesti {
    
    private Integer id;
    private String teksti;
    private String aika;

    public Viesti(Integer id, String teksti, String aika) {
        this.id = id;
        this.teksti = teksti;
        this.aika = aika;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeksti() {
        return teksti;
    }

    public void setTeksti(String teksti) {
        this.teksti = teksti;
    }

    public String getAika() {
        return aika;
    }

    public void setAika(String aika) {
        this.aika = aika;
    }
    
    
}
