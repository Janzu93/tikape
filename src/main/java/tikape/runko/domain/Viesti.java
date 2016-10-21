package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Viesti {

    private Integer id;
    private String teksti;
    private String aika;
    private Integer lahettajaId;
    private String lahettaja;

    public Viesti(Integer id, String teksti, String aika, Integer lahettajaId) {
        this.id = id;
        this.teksti = teksti;
        this.aika = aika;
        this.lahettajaId = lahettajaId;
    }

    public Viesti(Integer id, String teksti, String aika, Integer lahettajaId, String lahettaja) {
        this.id = id;
        this.teksti = teksti;
        this.aika = aika;
        this.lahettajaId = lahettajaId;
        this.lahettaja = lahettaja;
    }

    public String getLahettaja() {
        return lahettaja;
    }

    public void setLahettaja(String lahettaja) {
        this.lahettaja = lahettaja;
    }

    public Integer getId() {
        return id;
    }

    public Integer getLahettajaId() {
        return lahettajaId;
    }

    public void setLahettajaId(Integer lahettajaId) {
        this.lahettajaId = lahettajaId;
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
