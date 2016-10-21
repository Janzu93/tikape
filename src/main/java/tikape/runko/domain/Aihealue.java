package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Aihealue {
    
    private Integer id;
    private String otsikko;
    private Integer viestienLukumaara;
    private Viesti uusinViesti;
    
    public Aihealue(Integer id, String otsikko, int viestienLukumaara, Viesti uusinViesti) {
        this.id = id;
        this.otsikko = otsikko;
        this.viestienLukumaara = viestienLukumaara;
        this.uusinViesti = uusinViesti;
    }

    public Viesti getUusinViesti() {
        return uusinViesti;
    }

    public void setUusinViesti(Viesti uusinViesti) {
        this.uusinViesti = uusinViesti;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }
    
    public Integer getViestienLukumaara() {
        return viestienLukumaara;
    }
    
}
