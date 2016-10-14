package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.AihealueDao;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;
import tikape.runko.database.ViestiketjuDao;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:forum.db");
        database.init();
        AihealueDao ad = new AihealueDao(database);
        OpiskelijaDao opiskelijaDao = new OpiskelijaDao(database);
        ViestiketjuDao vkd = new ViestiketjuDao(database);

        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("aihealueet", ad.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        post("/", (req, res) -> {
            ad.create(req.queryParams("otsikko"));
            res.redirect("/");
            return "ok";
        });
        get("/aihealue/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("ketjut", vkd.findAll(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "aihealue");
        }, new ThymeleafTemplateEngine());
        post("/aihealue/:id", (req, res) -> {
            vkd.create(req.queryParams("otsikko"), Integer.parseInt(req.params(":id")));
            res.redirect("/aihealue/"+req.params(":id"));
            return "ok";
        });
        

        get("/opiskelijat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelijat", opiskelijaDao.findAll());

            return new ModelAndView(map, "opiskelijat");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelija", opiskelijaDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "opiskelija");
        }, new ThymeleafTemplateEngine());
    }
}
