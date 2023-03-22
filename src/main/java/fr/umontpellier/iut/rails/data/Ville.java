package fr.umontpellier.iut.rails.data;

import fr.umontpellier.iut.rails.Joueur;

public record Ville(
        String nom,
        boolean estPort) {

    @Override
    public String toString() {
        return nom;
    }

    public String toLog() {
        return String.format("<span class=\"ville\">%s</span>", nom);
    }


}
