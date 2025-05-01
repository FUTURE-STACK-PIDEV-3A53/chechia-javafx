package chechia.tn.test;

/**
 * Classe utilitaire pour la validation des données
 */
public class ValidationUtils {
    
    /**
     * Vérifie si une chaîne de caractères contient des chiffres
     * @param text La chaîne à vérifier
     * @return true si la chaîne contient au moins un chiffre, false sinon
     */
    public static boolean containsDigit(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Vérifie si la localisation est valide (ne contient pas de chiffres)
     * @param localisation La localisation à vérifier
     * @return true si la localisation est valide, false sinon
     */
    public static boolean isValidLocalisation(String localisation) {
        // Vérifier que la localisation n'est pas null et n'est pas vide
        if (localisation == null || localisation.trim().isEmpty()) {
            return false;
        }
        
        // Vérifier que la localisation ne contient pas de chiffres
        return !containsDigit(localisation);
    }
} 