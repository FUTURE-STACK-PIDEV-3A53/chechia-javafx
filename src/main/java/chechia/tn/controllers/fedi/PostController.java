package chechia.tn.controllers.fedi;

import chechia.tn.entities.Post;
import chechia.tn.service.fedi.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostController {
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
    
    private PostService postService;
    private Post selectedPost;
    
    public void initialize() {
        postService = new PostService();
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/fedi/commentaire.fxml"));
            commentaireContainer.getChildren().add(fxmlLoader.load());
            commentaireController = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        refreshPosts();
    }
    
    @FXML
    private void handlePublierPost() {
        String titre = titreTextField.getText().trim();
        String contenu = contenuTextArea.getText().trim();

        
        if (!titre.isEmpty() && !contenu.isEmpty()) {
            Post post = new Post();
            post.setTitre(titre);
            post.setContenu(contenu);
            post.setImage(imageTextField.getText().trim());
            post.setVideo(videoTextField.getText().trim());
            post.setDate_post(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            postService.ajouter(post);
            clearFields();
            refreshPosts();
        }
    }
    
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validerPost(String titre, String contenu) {
        if (titre.isEmpty()) {
            afficherErreur("Le titre ne peut pas être vide");
            return false;
        }
        if (titre.length() < 3) {
            afficherErreur("Le titre doit contenir au moins 3 caractères");
            return false;
        }
        if (contenu.isEmpty()) {
            afficherErreur("Le contenu ne peut pas être vide");
            return false;
        }
        if (contenu.length() < 10) {
            afficherErreur("Le contenu doit contenir au moins 10 caractères");
            return false;
        }
        return true;
    }

    @FXML
    private void handleModifierPost() {
        if (selectedPost == null) {
            afficherErreur("Aucun post n'est sélectionné");
            return;
        }

        String titre = titreTextField.getText().trim();
        String contenu = contenuTextArea.getText().trim();
        
        if (validerPost(titre, contenu)) {
            Post postModifie = new Post();
            postModifie.setId(selectedPost.getId());
            postModifie.setTitre(titre);
            postModifie.setContenu(contenu);
            postModifie.setImage(imageTextField.getText().trim());
            postModifie.setVideo(videoTextField.getText().trim());
            postModifie.setDate_post(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            try {
                postService.modifier(postModifie);
                refreshPosts();
                
                // Resélectionner le post modifié
                for (Post post : postListView.getItems()) {
                    if (post.getId() == postModifie.getId()) {
                        postListView.getSelectionModel().select(post);
                        break;
                    }
                }
            } catch (Exception e) {
                afficherErreur("Erreur lors de la modification du post: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSupprimerPost() {
        if (selectedPost != null) {
            postService.supprimer(selectedPost.getId());
            clearFields();
            selectedPost = null;
            refreshPosts();
        }
    }
    
    @FXML
    private void handleChoisirImage() {
        File file = showFileChooser("Image", "*.png", "*.jpg", "*.jpeg", "*.gif");
        if (file != null) {
            imageTextField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void handleChoisirVideo() {
        File file = showFileChooser("Vidéo", "*.mp4", "*.avi", "*.mov");
        if (file != null) {
            videoTextField.setText(file.getAbsolutePath());
        }
    }

    
    private File showFileChooser(String title, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
            title + " files", extensions
        );
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(new Stage());
    }
    
    private void clearFields() {
        titreTextField.clear();
        contenuTextArea.clear();
        imageTextField.clear();
        videoTextField.clear();
    }
    
    @FXML
    private VBox commentaireContainer;
    
    private CommentaireController commentaireController;
    
    private void refreshPosts() {
        int selectedIndex = postListView.getSelectionModel().getSelectedIndex();
        ObservableList<Post> posts = FXCollections.observableArrayList(postService.afficher());
        postListView.setItems(posts);
        
        if (selectedIndex >= 0 && selectedIndex < posts.size()) {
            postListView.getSelectionModel().select(selectedIndex);
        }
        
        postListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && commentaireController != null) {
                commentaireController.setCurrentPostId(newVal.getId());
            }
        });
        
        postListView.setCellFactory(lv -> new ListCell<Post>() {
            private VBox content;
            
            {
                content = new VBox(20);
                content.setStyle("-fx-padding: 25; -fx-background-color: white; -fx-border-color: #e8e8e8; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 2); -fx-background-radius: 8; -fx-border-radius: 8;");
                content.setOnMouseEntered(e -> content.setStyle("-fx-padding: 25; -fx-background-color: #fafafa; -fx-border-color: #e8e8e8; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 20, 0, 0, 4); -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;"));
                content.setOnMouseExited(e -> content.setStyle("-fx-padding: 25; -fx-background-color: white; -fx-border-color: #e8e8e8; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 2); -fx-background-radius: 8; -fx-border-radius: 8;"));
            }
            
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                
                if (empty || post == null) {
                    setGraphic(null);
                } else {
                    content = new VBox(15);
                    content.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 5; -fx-border-radius: 5;");
                    
                    Label titleLabel = new Label(post.getTitre());
                    titleLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 22; -fx-text-fill: #2c3e50; -fx-padding: 0 0 8 0;");
                    content.getChildren().add(titleLabel);
                    
                    Label contentLabel = new Label(post.getContenu());
                    contentLabel.setWrapText(true);
                    contentLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #34495e; -fx-font-size: 15; -fx-line-spacing: 1.6;");
                    content.getChildren().add(contentLabel);
                    
                    if (post.getImage() != null && !post.getImage().isEmpty()) {
                        try {
                            Image image = new Image(new FileInputStream(post.getImage()), 600, 450, true, true);
                            ImageView imageView = new ImageView(image);
                            imageView.setFitWidth(600);
                            imageView.setFitHeight(450);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setCache(true);
                            imageView.setStyle("-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 2); -fx-padding: 12; -fx-background-radius: 10; -fx-border-radius: 10;");
                            content.getChildren().add(imageView);
                        } catch (FileNotFoundException e) {
                            Label imageLabel = new Label("⚠️ Image non trouvée: " + post.getImage());
                            imageLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #e74c3c; -fx-font-size: 13; -fx-font-style: italic;");
                            content.getChildren().add(imageLabel);
                        }
                    }
                    
                    if (post.getVideo() != null && !post.getVideo().isEmpty()) {
                        Label videoLabel = new Label("🎥 Vidéo: " + post.getVideo());
                        videoLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #3498db; -fx-font-size: 13; -fx-font-weight: bold;");
                        content.getChildren().add(videoLabel);
                    }
                    
                    Label dateLabel = new Label(post.getDate_post());
                    dateLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-style: italic; -fx-text-fill: #95a5a6; -fx-font-size: 12; -fx-padding: 15 0 0 0;");
                    content.getChildren().add(dateLabel);
                    
                    setGraphic(content);
                }
            }
        });
        
        postListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedPost = newVal;
            if (newVal != null) {
                titreTextField.setText(newVal.getTitre());
                contenuTextArea.setText(newVal.getContenu());
                imageTextField.setText(newVal.getImage() != null ? newVal.getImage() : "");
                videoTextField.setText(newVal.getVideo() != null ? newVal.getVideo() : "");
            }
        });
    }
}