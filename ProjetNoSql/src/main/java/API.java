import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.json.JsonObject;

import javax.ws.rs.*; // Import les methods de routes @
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/product/")
public class API {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static String RecupererToutLesArticles() {
        // Connexion à la base mongoDB
        MongoDatabase database = MongoDB.ConnexionMongoDB("mongodb://localhost:27017", "ProjetNoSql");

        // Récupérer la collection
        MongoCollection<Document> collection_articles =  null;
        collection_articles = database.getCollection("articles");

        // Récupérer les articles
        FindIterable<Document> resultRecherche = MongoDB.RechercheDesignationArticle(collection_articles, "");
       List<String> list_articles = new ArrayList<>();
        for (Document document : resultRecherche) {
            list_articles.add(document.toJson());
        }
        // Renvoyer les articles
        return list_articles.toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public static String CreerNouvelArticle(@FormParam("designation") String designation, @FormParam("description") String description, @FormParam("prix_unitaire") Float prix_unitaire,
                                            @FormParam("image") String image, @FormParam("stock") int stock, @FormParam("type") String type, @FormParam("marque") String marque) {
       // InsérerArticle
        return "inserted";
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public static String SupprimerArticle(@QueryParam("") int id) {
        return "supprime";
    }

    public static void main(String[] args) {
        System.out.println(RecupererToutLesArticles()) ;
    }
}
