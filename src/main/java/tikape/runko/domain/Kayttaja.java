package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Kayttaja {

    private Integer id;
    private String nimimerkki;

    public Kayttaja(Integer id, String nimimerkki) {
        this.id = id;
        this.nimimerkki = nimimerkki;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNimimerkki() {
        return nimimerkki;
    }

    public void setNimimerkki(String nimimerkki) {
        this.nimimerkki = nimimerkki;
    }

}
