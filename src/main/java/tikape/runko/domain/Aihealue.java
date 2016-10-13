package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Aihealue {

    public Aihealue(Integer id, String otsikko) {
        this.id = id;
        this.otsikko = otsikko;
    }
    
    private Integer id;
    private String otsikko;

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
