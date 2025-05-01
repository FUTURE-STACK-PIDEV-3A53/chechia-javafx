package chechia.tn.controllers.fedi;

import chechia.tn.entities.Commentaire;
import chechia.tn.entities.Post;
import chechia.tn.service.fedi.PostService;
import chechia.tn.service.fedi.CommentaireService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackController {
    @FXML
    private TableView<Post> postTableView;
    @FXML
    private TableView<Commentaire> commentaireTableView;
    
    @FXML
    private TableColumn<Post, String> postTitreColumn;
    @FXML
    private TableColumn<Post, String> postContenuColumn;
    @FXML
    private TableColumn<Post, String> postDateColumn;
    
    @FXML
    private TableColumn<Commentaire, String> commentaireContenuColumn;
    @FXML
    private TableColumn<Commentaire, String> commentaireDateColumn;
    @FXML
    private TableColumn<Commentaire, Integer> commentairePostIdColumn;
    
    private PostService postService;
    private CommentaireService commentaireService;
    
    @FXML
    public void initialize() {
        postService = new PostService();
        commentaireService = new CommentaireService();
        
        // Configuration des colonnes pour les posts
        postTitreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        postContenuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContenu()));
        postDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate_post()));
        
        // Configuration des colonnes pour les commentaires
        commentaireContenuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContenu()));
        commentaireDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate_commentaire()));
        commentairePostIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPost_id()).asObject());
        
        // Ajout des boutons d'action
        addActionButtonsToTables();
        
        // Chargement initial des données
        refreshData();
    }
    
    private void addActionButtonsToTables() {
        TableColumn<Post, Void> postActionsColumn = new TableColumn<>("Actions");
        postActionsColumn.setCellFactory(param -> new TableCell<Post, Void>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(5, editButton, deleteButton);
            
            {
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                
                editButton.setOnAction(event -> {
                    Post post = getTableView().getItems().get(getIndex());
                    showEditPostDialog(post);
                });
                
                deleteButton.setOnAction(event -> {
                    Post post = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(post, true);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        TableColumn<Commentaire, Void> commentaireActionsColumn = new TableColumn<>("Actions");
        commentaireActionsColumn.setCellFactory(param -> new TableCell<Commentaire, Void>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttons = new HBox(5, editButton, deleteButton);
            
            {
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                
                editButton.setOnAction(event -> {
                    Commentaire commentaire = getTableView().getItems().get(getIndex());
                    showEditCommentaireDialog(commentaire);
                });
                
                deleteButton.setOnAction(event -> {
                    Commentaire commentaire = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(commentaire, false);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        postTableView.getColumns().add(postActionsColumn);
        commentaireTableView.getColumns().add(commentaireActionsColumn);
    }
    
    private boolean validerPost(String titre, String contenu) {
        if (titre.isEmpty() || contenu.isEmpty()) {
            showAlert("Erreur", "Les champs titre et contenu sont obligatoires.");
            return false;
        }
        if (titre.length() < 3 || titre.length() > 100) {
            showAlert("Erreur", "Le titre doit contenir entre 3 et 100 caractères.");
            return false;
        }
        if (contenu.length() < 10 || contenu.length() > 1000) {
            showAlert("Erreur", "Le contenu doit contenir entre 10 et 1000 caractères.");
            return false;
        }
        if (!titre.matches("^[\\p{L}\\p{N}\\s.,!?'\"()-]+$")) {
            showAlert("Erreur", "Le titre contient des caractères non autorisés.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showEditPostDialog(Post post) {
        Dialog<Post> dialog = new Dialog<>();
        dialog.setTitle("Modifier le post");
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        TextField titreField = new TextField(post.getTitre());
        TextArea contenuArea = new TextArea(post.getContenu());
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Titre:"), titreField,
            new Label("Contenu:"), contenuArea
        );
        
        dialogPane.setContent(content);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String titre = titreField.getText().trim();
                String contenu = contenuArea.getText().trim();
                
                if (validerPost(titre, contenu)) {
                    post.setTitre(titre);
                    post.setContenu(contenu);
                    post.setDate_post(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return post;
                }
                return null;
            }
            return null;
        });

        
        dialog.showAndWait().ifPresent(updatedPost -> {
            postService.modifier(updatedPost);
            refreshData();
        });
    }
    
    private boolean validerCommentaire(String contenu) {
        if (contenu.isEmpty()) {
            showAlert("Erreur", "Le contenu du commentaire est obligatoire.");
            return false;
        }
        if (contenu.length() < 2 || contenu.length() > 500) {
            showAlert("Erreur", "Le commentaire doit contenir entre 2 et 500 caractères.");
            return false;
        }
        if (!contenu.matches("^[\\p{L}\\p{N}\\s.,!?'\"()-]+$")) {
            showAlert("Erreur", "Le commentaire contient des caractères non autorisés.");
            return false;
        }
        return true;
    }

    private void showEditCommentaireDialog(Commentaire commentaire) {
        Dialog<Commentaire> dialog = new Dialog<>();
        dialog.setTitle("Modifier le commentaire");
        
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        TextArea contenuArea = new TextArea(commentaire.getContenu());
        
        dialogPane.setContent(new VBox(10, new Label("Contenu:"), contenuArea));
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String contenu = contenuArea.getText().trim();
                
                if (validerCommentaire(contenu)) {
                    commentaire.setContenu(contenu);
                    commentaire.setDate_commentaire(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return commentaire;
                }
                return null;
            }
            return null;
        });

        
        dialog.showAndWait().ifPresent(updatedCommentaire -> {
            commentaireService.modifier(updatedCommentaire);
            refreshData();
        });
    }
    
    private void showDeleteConfirmation(Object item, boolean isPost) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet élément ?");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (isPost) {
                    Post post = (Post) item;
                    postService.supprimer(post.getId());
                } else {
                    Commentaire commentaire = (Commentaire) item;
                    commentaireService.supprimer(commentaire.getId());
                }
                refreshData();
            }
        });
    }
    
    private void refreshData() {
        ObservableList<Post> posts = FXCollections.observableArrayList(postService.afficher());
        ObservableList<Commentaire> commentaires = FXCollections.observableArrayList(commentaireService.afficher());
        
        postTableView.setItems(posts);
        commentaireTableView.setItems(commentaires);
    }
}