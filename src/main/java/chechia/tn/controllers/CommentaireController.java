package chechia.tn.controllers;

import chechia.tn.entities.Commentaire;
import chechia.tn.services.CommentaireService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentaireController {
    @FXML
    private TextArea contenuTextArea;
    
    @FXML
    private ListView<Commentaire> commentaireListView;
    
    private CommentaireService commentaireService;
    private Commentaire selectedCommentaire;
    private int currentPostId;
    
    public void initialize() {
        commentaireService = new CommentaireService();
        refreshCommentaires();
    }
    
    public void setCurrentPostId(int postId) {
        this.currentPostId = postId;
        refreshCommentaires();
    }
    
    private boolean validerCommentaire(String contenu) {
        if (contenu.isEmpty()) {
            afficherErreur("Le commentaire ne peut pas être vide");
            return false;
        }
        if (contenu.length() < 2) {
            afficherErreur("Le commentaire doit contenir au moins 2 caractères");
            return false;
        }
        if (contenu.length() > 500) {
            afficherErreur("Le commentaire ne peut pas dépasser 500 caractères");
            return false;
        }
        if (!contenu.matches("^[\\p{L}\\p{N}\\s.,!?'\"()-]+$")) {
            afficherErreur("Le commentaire contient des caractères non autorisés");
            return false;
        }
        return true;
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAjouterCommentaire() {
        String contenu = contenuTextArea.getText().trim();
        if (currentPostId <= 0) {
            afficherErreur("Aucun post n'est sélectionné");
            return;
        }
        
        if (validerCommentaire(contenu)) {
            Commentaire commentaire = new Commentaire();
            commentaire.setPost_id(currentPostId);
            commentaire.setContenu(contenu);
            commentaire.setDate_commentaire(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            try {
                commentaireService.ajouter(commentaire);
                contenuTextArea.clear();
                refreshCommentaires();
            } catch (Exception e) {
                afficherErreur("Erreur lors de l'ajout du commentaire: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleModifierCommentaire() {
        String contenu = contenuTextArea.getText().trim();
        if (selectedCommentaire == null) {
            afficherErreur("Aucun commentaire n'est sélectionné");
            return;
        }
        
        if (validerCommentaire(contenu)) {
            selectedCommentaire.setContenu(contenu);
            selectedCommentaire.setDate_commentaire(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            try {
                commentaireService.modifier(selectedCommentaire);
                contenuTextArea.clear();
                selectedCommentaire = null;
                refreshCommentaires();
            } catch (Exception e) {
                afficherErreur("Erreur lors de la modification du commentaire: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleSupprimerCommentaire() {
        if (selectedCommentaire == null) {
            afficherErreur("Aucun commentaire n'est sélectionné");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce commentaire ?");

        if (alert.showAndWait().get().getButtonData().isDefaultButton()) {
            try {
                commentaireService.supprimer(selectedCommentaire.getId());
                contenuTextArea.clear();
                selectedCommentaire = null;
                refreshCommentaires();
            } catch (Exception e) {
                afficherErreur("Erreur lors de la suppression du commentaire: " + e.getMessage());
            }
        }
    }
    
    private void refreshCommentaires() {
        ObservableList<Commentaire> commentaires = FXCollections.observableArrayList(
            commentaireService.getByPostId(currentPostId)
        );
        commentaireListView.setItems(commentaires);
        
        commentaireListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedCommentaire = newVal;
            if (newVal != null) {
                contenuTextArea.setText(newVal.getContenu());
            }
        });
    }
    
    public CommentaireService getCommentaireService() {
        return commentaireService;
    }
}