package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Viestiketju {
    
    private Integer id;
    private String otsikko;
    private Integer lkm;

    public Viestiketju(Integer id, String otsikko, Integer lkm) {
        this.id = id;
        this.otsikko = otsikko;
        this.lkm = lkm;
    }

    public Viestiketju(Integer id, String otsikko) {
        this.id = id;
        this.otsikko = otsikko;
    }

    public Integer getLkm() {
        return lkm;
    }

    public void setLkm(Integer lkm) {
        this.lkm = lkm;
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
    
}
