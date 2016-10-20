package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Viestiketju {
    
    private Integer id;
    private String otsikko;
    private Integer viestienLkm;
    private Viesti uusinViesti;

    public Viestiketju(Integer id, String otsikko, Integer viestienLkm, Viesti uusinViesti) {
        this.id = id;
        this.otsikko = otsikko;
        this.viestienLkm = viestienLkm;
        this.uusinViesti = uusinViesti;
    }

    public Integer getViestienLkm() {
        return viestienLkm;
    }

    public void setViestienLkm(Integer lkm) {
        this.viestienLkm = viestienLkm;
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
    
    public Viesti getUusinViesti() {
        return uusinViesti;
    }
    
}
