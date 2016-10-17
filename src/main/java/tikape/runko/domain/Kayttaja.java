package tikape.runko.domain;

/**
 *
 * @author janne
 */
public class Kayttaja {

    private Integer id;
    private String nimimerkki;
    private String hash;
    private String salt;
    private Integer tyyppi;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(Integer tyyppi) {
        this.tyyppi = tyyppi;
    }

    public Kayttaja(Integer id, String nimimerkki, String salt, String hash, Integer tyyppi) {
        this.id = id;
        this.nimimerkki = nimimerkki;
        this.hash = hash;
        this.salt = salt;
        this.tyyppi = tyyppi;
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
