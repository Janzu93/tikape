package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AihealueDao;
import tikape.runko.database.Database;
import tikape.runko.database.KayttajaDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.database.ViestiketjuDao;
import java.security.MessageDigest;
import java.security.SecureRandom;
import tikape.runko.domain.Kayttaja;

public class Main {

    public static void main(String[] args) throws Exception {

        Database database = new Database("jdbc:sqlite:forum.db");
        database.init();
        AihealueDao ad = new AihealueDao(database);
        ViestiketjuDao vkd = new ViestiketjuDao(database);
        ViestiDao vd = new ViestiDao(database);
        KayttajaDao kd = new KayttajaDao(database);
        // Boolean kirjautunut = false; ?

        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        // Etusivu - Listaa aihealueet
        get("/", (req, res) -> {
            HashMap data = new HashMap<>();
            data.put("aihealueet", ad.findAll());

            if (req.cookie("login") != null) {
                String nimi = req.cookie("login").split(" ")[0];
                data.put("login", "Olet kirjautunut käyttäjänä: " + nimi);
            } else {
                data.put("login", "Et ole kirjautunut");
            }

            return new ModelAndView(data, "index");
        }, new ThymeleafTemplateEngine());

        // Luo uusi aihealue (POST Index)
        post("/", (req, res) -> {
            ad.create(req.queryParams("otsikko"));
            res.redirect("/");
            return "ok";
        });
        // Poista Aihealue
        post("/aihealue/:id/delete", (req, res) -> {
            ad.delete(Integer.parseInt(req.params(":id")));
            res.redirect("/");
            return "ok";
        });

        // Listaa aihealueen kaikki viestiketjut
        get("/aihealue/:id", (req, res) -> {
            HashMap data = new HashMap<>();
            data.put("ketjut", vkd.findAll(Integer.parseInt(req.params(":id"))));
            data.put("otsikko", ad.findOne(Integer.parseInt(req.params(":id"))).getOtsikko());

            return new ModelAndView(data, "aihealue");
        }, new ThymeleafTemplateEngine());

        // Luo uusi viestiketju (POST Aihealue)
        post("/aihealue/:id", (req, res) -> {
            vkd.create(req.queryParams("otsikko"), Integer.parseInt(req.params(":id")));
            res.redirect("/aihealue/" + req.params(":id"));
            return "ok";
        });
        // Poista viestiketju
        post("/ketju/:id/delete", (req, res) -> {
            int aihealueId = vkd.findAihealueId(Integer.parseInt(req.params(":id")));
            vkd.delete(Integer.parseInt(req.params(":id")));
            res.redirect("/aihealue/" + aihealueId);
            return "ok";
        });

        // Listaa viestiketjun kaikki viestit
        get("/ketju/:ketjuid", (req, res) -> {
            HashMap data = new HashMap<>();
            data.put("viestit", vd.findAll(Integer.parseInt(req.params(":ketjuid"))));
            data.put("otsikko", vkd.findOne(Integer.parseInt(req.params(":ketjuid"))).getOtsikko());
            data.put("aihealueId", vkd.findAihealueId(Integer.parseInt(req.params(":ketjuid"))));

            return new ModelAndView(data, "viestiketju");
        }, new ThymeleafTemplateEngine());

        // Luo uusi viesti (POST Viestiketju)
        post("/ketju/:ketjuid", (req, res) -> {
            vd.create(req.queryParams("teksti"), Integer.parseInt(req.params(":ketjuid")));

            res.redirect("/ketju/" + req.params(":ketjuid"));
            return "ok";
        });

        // Poista viesti
        post("/viesti/:id/delete", (req, res) -> {
            int ketjuId = vd.getKetjuId(Integer.parseInt(req.params(":id")));
            vd.delete(Integer.parseInt(req.params(":id")));

            // Teoriassa: jos poistaa viestiketjun viimeisen viestin, uudelleen ohjaa alkusivulle
            // Jos poistaa viimeisen viestin ketjusta niin eikö ketjukin kuulu poistua?
            if (vkd.countViestit(ketjuId) > 0) {
                res.redirect("/ketju/" + ketjuId);
            } else {
                res.redirect("/");
                vkd.delete(ketjuId);
            }
            return "ok";
        });

        get("/login", (req, res) -> {
            HashMap data = new HashMap<>();

            return new ModelAndView(data, "login");
        }, new ThymeleafTemplateEngine());

        post("/login", (req, res) -> {
            Kayttaja kayttaja = kd.findOne(req.queryParams("kayttajanimi"));

            if (kayttaja != null) {
                String hashattava = req.queryParams("salasana").hashCode() + kayttaja.getSalt();
                System.out.println(kayttaja.getSalt());

                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(hashattava.getBytes());
                byte byteData[] = md.digest();

                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < byteData.length; i++) {
                    String hex = Integer.toHexString(0xff & byteData[i]);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                System.out.println(hexString.toString());
                System.out.println(kayttaja.getHash());
                if (kayttaja.getHash().equals(hexString.toString())) {
                    res.cookie("login", req.queryParams("kayttajanimi") + " " + hexString.toString());
                    return "kirjauduttu käyttäjällä " + req.queryParams("kayttajanimi");
                }
            }

            return "Väärä käyttäjätunnus tai salasana";
        });

        get("/logout", (req, res) -> {

            res.removeCookie("login");
            res.redirect("/");
            return "ok";
        });

        get("/register", (req, res) -> {
            HashMap data = new HashMap<>();

            return new ModelAndView(data, "register");
        }, new ThymeleafTemplateEngine());

        post("/register", (req, res) -> {

            // Eihän käyttäjänimi ole varattu? Jos on siirrytään Elseen
            if (kd.findOne(req.queryParams("kayttajanimi")) == null) {

                /*Luodaan 128 merkkiä pitkä alfanumeerinen merkkijono joka tallennetaan suolaksi
                Tämän EI OLE pakko olla uniikki (koska hash muodostetaan salasana+salt=hash kaavalla)
                 */
                String salt = genSalt(128);
                String hashattava = req.queryParams("salasana").hashCode() + salt;

                /*
                Kutsutaan Javan MessageDigest luokkaa jolla pystytään cryptataan merkkijonoja
                Tässä tapauksessa SHA-256 joka on todettu teoriassa murtamattomaksi
                MessageDigest käsittelee merkkijonoja bitteinä
                 */
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(hashattava.getBytes());

                // Tässä kohti bittijono muutetaan cryptattuun muotoon
                byte byteData[] = md.digest();

                /* Ja sitten hexa muotoiseksi merkkijonoksi, voisi toki muuttaa kaiketi myös
                suoraan String muotoon mutta toimii se näinkin
                 */
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < byteData.length; i++) {
                    String hex = Integer.toHexString(0xff & byteData[i]);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }

                // Ja lopuksi luodaan käyttäjä ja muunnetaan hexString Stringiksi :)
                kd.create(req.queryParams("kayttajanimi"), salt, hexString.toString());

                res.redirect("/");
                return "ok";

            } else {
                return "Error (Onko käyttäjänimi jo varattu?)";

            }
        });

    }

    // Luo satunnaisen alfanumeerisen merkkijonon suolaksi
    public static String genSalt(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

}
