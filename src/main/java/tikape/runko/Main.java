package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AihealueDao;
import tikape.runko.database.Database;
import tikape.runko.database.ViestiDao;
import tikape.runko.database.ViestiketjuDao;

public class Main {

    public static void main(String[] args) throws Exception {
        
        Database database = new Database("jdbc:sqlite:forum.db");
        database.init();
        AihealueDao ad = new AihealueDao(database);
        ViestiketjuDao vkd = new ViestiketjuDao(database);
        ViestiDao vd = new ViestiDao(database);

        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        
        // Etusivu - Listaa aihealueet
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("aihealueet", ad.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        // Luo uusi aihealue (POST Index)
        post("/", (req, res) -> {
            ad.create(req.queryParams("otsikko"));
            res.redirect("/");
            return "ok";
        });
        
        
        // Listaa aihealueen kaikki viestiketjut
        get("/aihealue/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("ketjut", vkd.findAll(Integer.parseInt(req.params(":id"))));
            map.put("otsikko", ad.findOne(Integer.parseInt(req.params(":id"))).getOtsikko());

            return new ModelAndView(map, "aihealue");   
        }, new ThymeleafTemplateEngine());
        
        // Luo uusi viestiketju (POST Aihealue)
        post("/aihealue/:id", (req, res) -> {
            vkd.create(req.queryParams("otsikko"), Integer.parseInt(req.params(":id")));
            res.redirect("/aihealue/"+req.params(":id"));
            return "ok";
        });
        
        
        
        // Listaa viestiketjun kaikki viestit
        get("/ketju/:ketjuid", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viestit", vd.findAll(Integer.parseInt(req.params(":ketjuid"))));
            map.put("otsikko", vkd.findOne(Integer.parseInt(req.params(":ketjuid"))).getOtsikko());
            map.put("aihealueId", vkd.findAihealueId(Integer.parseInt(req.params(":ketjuid"))));
            
            return new ModelAndView(map, "viestiketju");
        }, new ThymeleafTemplateEngine());
        
        // Luo uusi viesti (POST Viestiketju)
        post("/ketju/:ketjuid", (req, res) -> {
            vd.create(req.queryParams("teksti"), Integer.parseInt(req.params(":ketjuid")));
            
            res.redirect("/ketju/"+req.params(":ketjuid"));
            return "ok";
        });        

    }
}
