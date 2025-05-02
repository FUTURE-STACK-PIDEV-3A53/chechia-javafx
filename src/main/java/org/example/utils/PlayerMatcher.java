package org.example.utils;

import org.example.model.Player;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.AbstractMap;
import java.util.Map;

public class PlayerMatcher {
    
    public static List<Player> suggestOpponents(Player currentPlayer, List<Player> allPlayers, int numberOfSuggestions) {
        if (currentPlayer == null || allPlayers == null || allPlayers.isEmpty()) {
            return new ArrayList<>();
        }

        // Filtrer le joueur actuel de la liste
        List<Player> otherPlayers = allPlayers.stream()
            .filter(p -> !p.getUsername().equals(currentPlayer.getUsername()))
            .collect(Collectors.toList());

        // Créer une liste de joueurs avec leurs scores de différence
        return otherPlayers.stream()
            .map(p -> {
                Player clone = new Player();
                clone.setId(p.getId());
                clone.setUsername(p.getUsername());
                clone.setScore(p.getScore());
                clone.setGameRoomId(p.getGameRoomId());
                return new AbstractMap.SimpleEntry<>(clone, Math.abs(p.getScore() - currentPlayer.getScore()));
            })
            .sorted(Comparator.comparingInt(Map.Entry::getValue))
            .limit(numberOfSuggestions)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    public static String getMatchQuality(Player player1, Player player2) {
        int scoreDiff = Math.abs(player1.getScore() - player2.getScore());
        
        if (scoreDiff <= 10) {
            return "Match parfait !";
        } else if (scoreDiff <= 30) {
            return "Bon match";
        } else if (scoreDiff <= 50) {
            return "Match équilibré";
        } else {
            return "Match difficile";
        }
    }
} 