package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Aihealue {

    public Aihealue(Integer id, String otsikko, int viestienLukumaara) {
        this.id = id;
        this.otsikko = otsikko;
        this.viestienLukumaara = viestienLukumaara;
    }
    
    private Integer id;
    private String otsikko;
    private Integer viestienLukumaara;

    public Aihealue(Integer id, String otsikko, Integer viestiLkm) {
        this.id = id;
        this.otsikko = otsikko;
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
