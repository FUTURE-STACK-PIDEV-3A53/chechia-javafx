package chechia.tn.controllers;

import chechia.tn.entities.Post;
import chechia.tn.services.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FrontController {
    @FXML
    private VBox postFormContainer;
    @FXML
    private TextField titreTextField;
    @FXML
    private TextArea contenuTextArea;
    @FXML
    private TextField imageTextField;
    @FXML
    private TextField videoTextField;
    @FXML
    private ListView<Post> postListView;
    @FXML
    private VBox commentaireContainer;
    @FXML
    private HBox toolbarContainer;
    @FXML
    private Button btnNouveau;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    
    private PostService postService;
    private Post selectedPost;
    private CommentaireController commentaireController;

    @FXML
    private void initialize() {
        postService = new PostService();
        
        // Configuration des boutons de la barre d'outils
        btnNouveau.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnModifier.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chechia/tn/commentaire.fxml"));
            commentaireContainer.getChildren().add(fxmlLoader.load());
            commentaireController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        refreshPosts();
        
        postListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPost = newVal;
            if (newVal != null) {
                titreTextField.setText(newVal.getTitre());
                contenuTextArea.setText(newVal.getContenu());
                imageTextField.setText(newVal.getImage());
                videoTextField.setText(newVal.getVideo());
                
                btnModifier.setDisable(false);
                btnSupprimer.setDisable(false);
                
                if (commentaireController != null) {
                    commentaireController.setCurrentPostId(newVal.getId());
                }
            } else {
                btnModifier.setDisable(true);
                btnSupprimer.setDisable(true);
            }
        });
    }

    @FXML
    private void handleNouveauPost() {
        postFormContainer.setVisible(true);
        postFormContainer.setManaged(true);
        viderFormulaire();
    }

    private boolean validerChamps(String titre, String contenu) {
        if (titre.isEmpty() || contenu.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs obligatoires");
            return false;
        }
        if (titre.length() < 3 || titre.length() > 100) {
            afficherErreur("Le titre doit contenir entre 3 et 100 caractères");
            return false;
        }
        if (contenu.length() < 10 || contenu.length() > 1000) {
            afficherErreur("Le contenu doit contenir entre 10 et 1000 caractères");
            return false;
        }
        if (!titre.matches("^[\\p{L}\\p{N}\\s.,!?'\"()-]+$")) {
            afficherErreur("Le titre contient des caractères non autorisés");
            return false;
        }
        return true;
    }

    @FXML
    private void handlePublierPost() {
        String titre = titreTextField.getText().trim();
        String contenu = contenuTextArea.getText().trim();
        
        if (validerChamps(titre, contenu)) {
            Post post = new Post();
            post.setTitre(titre);
            post.setContenu(contenu);
            post.setImage(imageTextField.getText().trim());
            post.setVideo(videoTextField.getText().trim());
            post.setDate_post(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            postService.ajouter(post);
            viderFormulaire();
            postFormContainer.setVisible(false);
            postFormContainer.setManaged(false);
            refreshPosts();
        }
    }

    @FXML
    private void handleAnnulerPost() {
        viderFormulaire();
        postFormContainer.setVisible(false);
        postFormContainer.setManaged(false);
    }

    @FXML
    private void handleChoisirImage() {
        File file = ouvrirSelecteurFichier("Images", "*.png", "*.jpg", "*.jpeg");
        if (file != null) {
            imageTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleChoisirVideo() {
        File file = ouvrirSelecteurFichier("Vidéos", "*.mp4", "*.avi", "*.mov");
        if (file != null) {
            videoTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleModifierPost() {
        if (selectedPost != null) {
            // Afficher le formulaire avec les données actuelles
            postFormContainer.setVisible(true);
            postFormContainer.setManaged(true);
            
            // Ajouter un bouton de sauvegarde spécifique pour la modification
            Button btnSauvegarder = new Button("Sauvegarder les modifications");
            btnSauvegarder.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
            btnSauvegarder.setOnAction(e -> sauvegarderModifications());
            
            // Ajouter le bouton au formulaire
            if (!postFormContainer.getChildren().contains(btnSauvegarder)) {
                postFormContainer.getChildren().add(btnSauvegarder);
            }
        }
    }

    private void sauvegarderModifications() {
        String titre = titreTextField.getText().trim();
        String contenu = contenuTextArea.getText().trim();
        
        if (validerChamps(titre, contenu)) {
            try {
                Post postModifie = new Post();
                postModifie.setId(selectedPost.getId());
                postModifie.setTitre(titre);
                postModifie.setContenu(contenu);
                postModifie.setImage(imageTextField.getText().trim());
                postModifie.setVideo(videoTextField.getText().trim());
                postModifie.setDate_post(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                
                postService.modifier(postModifie);
                
                // Rafraîchir l'affichage et fermer le formulaire
                refreshPosts();
                postFormContainer.setVisible(false);
                postFormContainer.setManaged(false);
                
                // Nettoyer le formulaire
                viderFormulaire();
                
                // Retirer le bouton de sauvegarde
                postFormContainer.getChildren().removeIf(node -> node instanceof Button && 
                    ((Button) node).getText().equals("Sauvegarder les modifications"));
            } catch (Exception e) {
                afficherErreur("Erreur lors de la modification du post: " + e.getMessage());
            }
        } else {
            afficherErreur("Veuillez remplir tous les champs obligatoires");
        }
    }
    
    @FXML
    private void handleSupprimerPost() {
        if (selectedPost != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce post ?");
            alert.setContentText("Cette action supprimera également tous les commentaires associés.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                if (commentaireController != null) {
                    commentaireController.getCommentaireService().supprimerParPostId(selectedPost.getId());
                }
                postService.supprimer(selectedPost.getId());
                viderFormulaire();
                selectedPost = null;
                refreshPosts();
            }
        }
    }
    
    private void refreshPosts() {
        ObservableList<Post> posts = FXCollections.observableArrayList(postService.afficher());
        postListView.setItems(posts);
        
        postListView.setCellFactory(lv -> new ListCell<Post>() {
            private VBox content;
            
            {
                content = new VBox(15);
                content.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 5; -fx-border-radius: 5;");
            }
            
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                
                if (empty || post == null) {
                    setGraphic(null);
                } else {
                    Label titleLabel = new Label(post.getTitre());
                    titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 22; -fx-text-fill: #2c3e50; -fx-padding: 0 0 8 0;");
                    
                    Label contentLabel = new Label(post.getContenu());
                    contentLabel.setWrapText(true);
                    contentLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #34495e; -fx-font-size: 15; -fx-line-spacing: 1.6;");
                    
                    content.getChildren().setAll(titleLabel, contentLabel);
                    
                    if (post.getImage() != null && !post.getImage().isEmpty()) {
                        try {
                            ImageView imageView = new ImageView(new File(post.getImage()).toURI().toString());
                            imageView.setFitWidth(400);
                            imageView.setPreserveRatio(true);
                            content.getChildren().add(imageView);
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                        }
                    }
                    
                    Label dateLabel = new Label(post.getDate_post());
                    dateLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-style: italic; -fx-font-size: 12; -fx-text-fill: #7f8c8d; -fx-padding: 10 0 0 0;");
                    content.getChildren().add(dateLabel);
                    
                    setGraphic(content);
                }
            }
        });
    }

    private void viderFormulaire() {
        titreTextField.clear();
        contenuTextArea.clear();
        imageTextField.clear();
        videoTextField.clear();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private File ouvrirSelecteurFichier(String titre, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(titre);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
            titre + " files", extensions);
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(null);
    }
}