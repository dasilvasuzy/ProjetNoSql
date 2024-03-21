import com.mongodb.BasicDBList;
import com.mongodb.MongoException;
import com.mongodb.client.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;

import javax.print.Doc;
import javax.swing.*;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MongoDB {

    public static MongoDatabase ConnexionMongoDB (String $uri_mongodb, String $database_name) {
        /* Connexion à la bdd local MongoDB */
        String uri = $uri_mongodb;
        MongoClient mongoClient = MongoClients.create(uri);
        System.out.println("-> Connexion ouverte");
        return mongoClient.getDatabase($database_name);

        /*
        String uri = $uri_mongodb;
        MongoClient mongoClient = null
        try (mongoClient = MongoClients.create(uri)) {
            System.out.println("-> Connexion ouverte");
            return mongoClient.getDatabase($database_name);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        */
    }

    public static boolean InsérerArticle (MongoCollection<Document> collection, String designation_article,
                                      String description_article, double prix_unitaire_article, String image_article,
                                      Integer stock_article, String type_article, String marque_article,
                                      String liste_couleur_article ) {
        // Insérer 1 document article
        try {
            InsertOneResult result = collection.insertOne(new Document()
                    .append("_id", new ObjectId())
                    .append("designation", designation_article)
                    .append("description", description_article)
                    .append("prix_unitaire", prix_unitaire_article)
                    .append("image", image_article)
                    .append("stock", stock_article)
                    .append("type", type_article)
                    .append("marque", marque_article)
                    .append("liste_couleurs", liste_couleur_article)
            );
            System.out.println("Success! Inserted document id: " + result.getInsertedId());
            return true;
        } catch (MongoException e) {
            System.err.println("Unable to insert due to an error: " + e);
            return false;
        }
    }

    public static FindIterable<Document> RechercheDesignationArticle (MongoCollection<Document> collection, String designation_recherchee) {
        Document filtre;
        if (designation_recherchee == "") {
            // Récup tout les articles
           filtre = new Document();
        } else  {
             // Créer un filtre pour trouver les articles à la designation correspondante
            filtre = new Document("designation", designation_recherchee);
        }
        // Rechercher
        FindIterable<Document> resultat = collection.find(filtre);
        return resultat;
    }

    public static boolean SupprimerArticle (MongoCollection<Document> collection, String id_a_supprimer ) {
        // Créer un filtre pour retrouver l'article à supprimer
        ObjectId objectId = new ObjectId(id_a_supprimer);
        Document filtre = new Document("_id", objectId);

        // Supprimer
        DeleteResult deleteResult = collection.deleteOne(filtre);

        // Vérifier que ça a bien été supprimer
        if (deleteResult.wasAcknowledged() && deleteResult.getDeletedCount() > 0) {
            System.out.println("Document avec l'ID " + id_a_supprimer + " a été supprimé.");
            return true;
        } else {
            System.out.println("Aucun document avec l'ID " + id_a_supprimer + " n'a été trouvé ou supprimé.");
            return false;
        }
    }

    public static boolean ModifierArticle (MongoCollection<Document> collection, String id_a_modifier, String designation_article,
                                           String description_article, double prix_unitaire_article, String image_article,
                                           Integer stock_article, String type_article, String marque_article,
                                           String liste_couleur_article ) {
        // Créer un filtre pour retrouver l'article à modifier
        ObjectId objectId = new ObjectId(id_a_modifier);
        Document filtre = new Document("_id", objectId);

        // Créer un document avec les nouvelles valeurs
        Document update = new Document("$set", new Document("designation", designation_article)
                                                            .append("description", description_article)
                                                            .append("prix_unitaire", prix_unitaire_article)
                                                            .append("image", image_article)
                                                            .append("stock", stock_article)
                                                            .append("type", type_article)
                                                            .append("marque", marque_article)
                                                            .append("liste_couleurs", liste_couleur_article)
                                     );

        UpdateResult updateResult = collection.updateOne(filtre, update);

        // Vérifier que l'article a bien été modifié
        if (updateResult.wasAcknowledged() && updateResult.getModifiedCount() > 0) {
            System.out.println("Document avec l'ID " + id_a_modifier + " a été modifié.");
            return true;
        } else {
            System.out.println("Aucun document avec l'ID " + id_a_modifier + " n'a été trouvé ou modifié.");
            return false;
        }
    }

    public static void main(String[] args) {
        // Connexion à la base mongoDB
        MongoDatabase database = ConnexionMongoDB("mongodb://localhost:27017", "ProjetNoSql");

        // Récupérer la collection
        MongoCollection<Document> collection_articles =  null;
        collection_articles = database.getCollection("articles");
        System.out.println("-> Collection 'articles' récupérée");

        // Supprimer la totalité des articles
        // collection_articles.drop();

        // Insérer un article
        boolean resultInsert = InsérerArticle(collection_articles, "Happy Chenille",
                "Happy Chenille est un fil 100% polyester spécialement conçu pour la confection de petits amigurumis et autres objets au crochet.",
                1.65, "image", 40, "Chenille yarn", "DMC", "");

        // Rechercher un article
        FindIterable<Document> resultRecherche = RechercheDesignationArticle(collection_articles, "Happy Chenille");
        // Parcourir les résultats
        for (Document document : resultRecherche) {
            // Faire quelque chose avec chaque document
            System.out.println(document.toJson());
        }

        // Supprimer un article
        //boolean resultDelete = SupprimerArticle(collection_articles, "659d8f0a626619027e1e4dcc");

        // Modifier un article
        boolean resultModifier = ModifierArticle(collection_articles, "659d9107f28c09759ed238b6", "Happy Chenille MODIFIER",
                "Happy Chenille est un fil 100% polyester spécialement conçu pour la confection de petits amigurumis et autres objets au crochet.",
                1.65, "image", 40, "Chenille yarn", "DMC", "");
    }
 /* public static void main(String[] args) {

        String url = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(url)) {
            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
            databases.forEach(db -> System.out.println(db.toJson()));

            // Se connecter à la database local
            MongoDatabase database = mongoClient.getDatabase("local"); // si on essaie de récupérer un base qui n'existe pas ils va la créer automatiquement
            MongoCollection<Document> collection = database.getCollection("DesPersonnes");

            // InsérerPersonnes(collection);

            // Récupérer une donnée
            Document people1 = collection.find(new Document("name","Suzy")).first();
            System.out.println(people1.toJson());

            // Récupérer les données de la base
            List<Document> ListePersonnes = collection.find().into(new ArrayList<>());
            ListePersonnes.forEach(list -> System.out.println(list.toJson()));
            
            
            // Update 1 données (1 données = 1 doc en fait) 
            Bson filter = eq("name","Suzy"); //  filtre pr trouver la donnée
            Bson updateOperation = set("comment","Alternance en développement informatique"); // la commande de modif du doc
            UpdateResult updateResult = collection.updateOne(filter,updateOperation);

            // Update plrs docs
            Bson filterGirls = eq("sexe","f");
            Bson updateOperationAll = set("comment", "Etudiante à l'IUT d'Amiens");
            UpdateResult updateResultAll = collection.updateMany(filterGirls,updateOperationAll);

            Bson filterBoys = eq("sexe","h");
            Bson updateOperationAll1 = set("comment", "Etudiant à l'IUT d'Amiens");
            UpdateResult updateResultAll1 = collection.updateMany(filterGirls,updateOperationAll1);

            // Delete un document
            Bson filterDeleteOne = eq("name","Suzy");
            DeleteResult DeleteOne = collection.deleteOne(filterDeleteOne);

            // Delete plrs document
            Bson filterDeleteMany = eq("sexe","f");
            DeleteResult DeleteMany = collection.deleteMany(filterDeleteMany);

            // Delete all
            // collection.drop();


        }  catch (Exception e) {

        }
    }*/
}
