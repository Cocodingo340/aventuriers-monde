package fr.umontpellier.iut.rails;

import fr.umontpellier.iut.rails.data.*;

import java.util.*;

public class Joueur {
    public enum CouleurJouer {
        JAUNE, ROUGE, BLEU, VERT, ROSE;
    }

    /**
     * Jeu auquel le joueur est rattaché
     */
    private final Jeu jeu;
    /**
     * Nom du joueur
     */
    private final String nom;
    /**
     * CouleurJouer du joueur (pour représentation sur le plateau)
     */
    private final CouleurJouer couleur;
    /**
     * Liste des villes sur lesquelles le joueur a construit un port
     */
    private final List<Ville> ports;
    /**
     * Liste des routes capturées par le joueur
     */
    private final List<Route> routes;
    /**
     * Nombre de pions wagons que le joueur peut encore poser sur le plateau
     */
    private int nbPionsWagon;
    /**
     * Nombre de pions wagons que le joueur a dans sa réserve (dans la boîte)
     */
    private int nbPionsWagonEnReserve;
    /**
     * Nombre de pions bateaux que le joueur peut encore poser sur le plateau
     */
    private int nbPionsBateau;
    /**
     * Nombre de pions bateaux que le joueur a dans sa réserve (dans la boîte)
     */
    private int nbPionsBateauEnReserve;
    /**
     * Liste des destinations à réaliser pendant la partie
     */
    private final List<Destination> destinations;
    /**
     * Liste des cartes que le joueur a en main
     */
    private final List<CarteTransport> cartesTransport;
    /**
     * Liste temporaire de cartes transport que le joueur est en train de jouer pour
     * payer la capture d'une route ou la construction d'un port
     */
    private final List<CarteTransport> cartesTransportPosees;
    /**
     * Score courant du joueur (somme des valeurs des routes capturées, et points
     * perdus lors des échanges de pions)
     */
    private int score;

    public Joueur(String nom, Jeu jeu, CouleurJouer couleur) {
        this.nom = nom;
        this.jeu = jeu;
        this.couleur = couleur;
        this.ports = new ArrayList<>();
        this.routes = new ArrayList<>();
        this.nbPionsWagon = 0;
        this.nbPionsWagonEnReserve = 25;
        this.nbPionsBateau = 0;
        this.nbPionsBateauEnReserve = 50;
        this.cartesTransport = new ArrayList<>();
        this.cartesTransportPosees = new ArrayList<>();
        this.destinations = new ArrayList<>();
        this.score = 0;
    }

    public String getNom() {
        return nom;
    }

    public int getNbPionsWagon() {
        return nbPionsWagon;
    }

    public void setNbPionsWagon(int nbPionsWagon) {
        this.nbPionsWagon = nbPionsWagon;
    }

    public int getNbPionsWagonEnReserve() {
        return nbPionsWagonEnReserve;
    }

    public void setNbPionsWagonEnReserve(int nbPionsWagonEnReserve) {
        this.nbPionsWagonEnReserve = nbPionsWagonEnReserve;
    }

    public int getNbPionsBateau() {
        return nbPionsBateau;
    }

    public void setNbPionsBateau(int nbPionsBateau) {
        this.nbPionsBateau = nbPionsBateau;
    }

    public int getNbPionsBateauEnReserve() {
        return nbPionsBateauEnReserve;
    }

    public void setNbPionsBateauEnReserve(int nbPionsBateauEnReserve) {
        this.nbPionsBateauEnReserve = nbPionsBateauEnReserve;
    }

    public void enleverDestination(Destination destination) {
        this.destinations.remove(destination);
    }

    public void ajouterDestination(Destination destination) {
        this.destinations.add(destination);
    }

    /**
     * Cette méthode est appelée à tour de rôle pour chacun des joueurs de la partie.
     * Elle doit réaliser un tour de jeu, pendant lequel le joueur a le choix entre 5 actions possibles :
     *  - piocher des cartes transport (visibles ou dans la pioche)
     *  - échanger des pions wagons ou bateau
     *  - prendre de nouvelles destinations
     *  - capturer une route
     *  - construire un port
     */
    void jouerTour() {
        // IMPORTANT : Le corps de cette fonction est à réécrire entièrement
        // Un exemple très simple est donné pour illustrer l'utilisation de certaines méthodes

        List<Bouton> boutons = Arrays.asList(
                new Bouton("PIONS WAGON"),
                new Bouton("PIONS BATEAU"),
                new Bouton("DESTINATION"),
                new Bouton("Capturer une route"),
                new Bouton("Construire un port"));

        String choix = choisir(
                "Veuillez choisir une option a effectuer ce tour :\n",
                null,
                boutons,
                false);

        if (choix.equals("PIONS WAGON")) {
            int nbPionsEchanges=choisirNombre("Choissisez combien de Pions Wagons voulez-vous prendre",1,nbPionsBateauEnReserve);
            nbPionsBateauEnReserve+=nbPionsEchanges;
            nbPionsWagon+=nbPionsEchanges;
            nbPionsWagonEnReserve-=nbPionsEchanges;
            nbPionsBateau-=nbPionsEchanges;
            score-=nbPionsEchanges;
        }
        else if (choix.equals("PIONS BATEAU")) {
            int nbPionsEchanges=choisirNombre("Choissisez combien de Pions Bateau voulez-vous prendre",1,nbPionsWagonEnReserve);
            nbPionsBateauEnReserve-=nbPionsEchanges;
            nbPionsWagon-=nbPionsEchanges;
            nbPionsWagonEnReserve+=nbPionsEchanges;
            nbPionsBateau+=nbPionsEchanges;
            score-=nbPionsEchanges;

        }
        else if (choix.equals("DESTINATION")) {
           jeu.jouerTourPiocherDestination();
        }
        else if (choix.equals("UN NOM DE VILLE")) {
            log(String.format("%s a choisi %s", toLog(), choix));
        }
        else if (choix.equals("Construire un port")) {
            log(String.format("%s a choisi %s", toLog(), choix));
        }

    }

    /**
     * Attend une entrée de la part du joueur (au clavier ou sur la websocket) et
     * renvoie le choix du joueur.
     *
     * Cette méthode lit les entrées du jeu (`Jeu.lireligne()`) jusqu'à ce
     * qu'un choix valide (un élément de `choix` ou de `boutons` ou
     * éventuellement la chaîne vide si l'utilisateur est autorisé à passer) soit
     * reçu.
     * Lorsqu'un choix valide est obtenu, il est renvoyé par la fonction.
     *
     * Exemple d'utilisation pour demander à un joueur de répondre à une question
     * par "oui" ou "non" :
     *
     * ```
     * List<String> choix = Arrays.asList("Oui", "Non");
     * String input = choisir("Voulez-vous faire ceci ?", choix, null, false);
     * ```
     *
     * Si par contre on voulait proposer les réponses à l'aide de boutons, on
     * pourrait utiliser :
     *
     * ```
     * List<Bouton> boutons = Arrays.asList(new Bouton("Un", "1"), new Bouton("Deux", "2"), new Bouton("Trois", "3"));
     * String input = choisir("Choisissez un nombre.", null, boutons, false);
     * ```
     *
     * @param instruction message à afficher à l'écran pour indiquer au joueur la
     *                    nature du choix qui est attendu
     * @param choix       une collection de chaînes de caractères correspondant aux
     *                    choix valides attendus du joueur
     * @param boutons     une collection de `Bouton` représentés par deux String (label,
     *                    valeur) correspondant aux choix valides attendus du joueur
     *                    qui doivent être représentés par des boutons sur
     *                    l'interface graphique (le label est affiché sur le bouton,
     *                    la valeur est ce qui est envoyé au jeu quand le bouton est
     *                    cliqué)
     * @param peutPasser  booléen indiquant si le joueur a le droit de passer sans
     *                    faire de choix. S'il est autorisé à passer, c'est la
     *                    chaîne de caractères vide ("") qui signifie qu'il désire
     *                    passer.
     * @return le choix de l'utilisateur (un élement de `choix`, ou la valeur
     * d'un élément de `boutons` ou la chaîne vide)
     */
    public String choisir(
            String instruction,
            Collection<String> choix,
            Collection<Bouton> boutons,
            boolean peutPasser) {
        if (choix == null)
            choix = new ArrayList<>();
        if (boutons == null)
            boutons = new ArrayList<>();

        HashSet<String> choixDistincts = new HashSet<>(choix);
        choixDistincts.addAll(boutons.stream().map(Bouton::valeur).toList());
        if (peutPasser || choixDistincts.isEmpty()) {
            choixDistincts.add("");
        }

        String entree;
        // Lit l'entrée de l'utilisateur jusqu'à obtenir un choix valide
        while (true) {
            jeu.prompt(instruction, boutons, peutPasser);
            entree = jeu.lireLigne();
            // si une réponse valide est obtenue, elle est renvoyée
            if (choixDistincts.contains(entree)) {
                return entree;
            }
        }
    }


    public List<Destination> choisirDestinations(List<Destination> destinationsPossibles, int n) {
        List<Destination> destinationsNonChoisies = new ArrayList<>();
        String choix = null;
        while (choix == null || !choix.equals("") && destinationsPossibles.size() > n) {
            List<Bouton> choixDestination = new ArrayList<>();
            for (Destination destination : destinationsPossibles) {
                choixDestination.add(new Bouton(destination.getNom()));
            }
            choix = choisir("Quelles destinations voulez-vous enlever (2 max) ?", new ArrayList<>(), choixDestination, true);
            for (Destination destination : destinationsPossibles) {
                if (destination.getNom().equals(choix)) {
                    destinationsPossibles.remove(destination);
                    destinationsNonChoisies.add(destination);
                    break;
                }
            }
        }
        this.destinations.addAll(destinationsPossibles);


        return destinationsNonChoisies;
    }

    public int choisirNombre(String instruction, int min, int max ) {

        while (true) {
            jeu.prompt(instruction, new ArrayList<>(), false);
            String entree = jeu.lireLigne();

            try {
                int choix = Integer.parseInt(entree);
                if (choix >= min && choix <= max) {
                    return choix;
                } else {
                    log(String.format("Veuillez entrer un nombre entre %d et %d", min, max));
                }
            } catch (NumberFormatException e) {
                log("Veuillez entrer un nombre valide");
            }
        }
    }

    public void ajoutCarteTransport(CarteTransport carte){
        this.cartesTransport.add(carte);
    }

    public void ajouterDestinationDansJoueur(List<Destination> destination){
        this.destinations.addAll(destination);
    }

    /**
     * Affiche un message dans le log du jeu (visible sur l'interface graphique)
     *
     * @param message le message à afficher (peut contenir des balises html pour la
     *                mise en forme)
     */
    public void log(String message) {
        jeu.log(message);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(String.format("=== %s (%d pts) ===", nom, score));
        joiner.add(String.format("  Wagons: %d  Bateaux: %d", nbPionsWagon, nbPionsBateau));
        return joiner.toString();
    }

    /**
     * @return une chaîne de caractères contenant le nom du joueur, avec des balises
     * HTML pour être mis en forme dans le log
     */
    public String toLog() {
        return String.format("<span class=\"joueur\">%s</span>", nom);
    }

    boolean destinationEstComplete(Destination d) {
        // Cette méthode pour l'instant renvoie false pour que le jeu puisse s'exécuter.
        // À vous de modifier le corps de cette fonction pour qu'elle retourne la valeur attendue.
        return false;
    }

    public int calculerScoreFinal() {
        throw new RuntimeException("Méthode pas encore implémentée !");
    }

    public boolean peutPoserPort(Ville ville){
        List<Couleur> ListeCouleur = new ArrayList<Couleur>(EnumSet.allOf(Couleur.class));
        boolean peut=true;
        if(!ville.estPort()){
            return false;
        }

        ArrayList<CarteTransport> cartesAvecEncres = new ArrayList<>();
        for (int i = 0; i < this.cartesTransport.size(); i++) {
            if (this.cartesTransport.get(i).getAncre() || this.cartesTransport.get(i).getType() == TypeCarteTransport.JOKER ){
                cartesAvecEncres.add(this.cartesTransport.get(i));
            }
        }

        for (int i=0; i < ListeCouleur.size(); i++) {
            if(nombreCouleurWagonJoueur(cartesAvecEncres,ListeCouleur.get(i))+nombreCouleurBateauJoueur(cartesAvecEncres,ListeCouleur.get(i))+nombreJoker(cartesAvecEncres)<4){
                return false;
            }

        }

        for (Route route: routes) {
            if(route.getVille1().equals(ville) || route.getVille2().equals(ville)){
                return true;
            }
        }

        return false;

    }

    public void prendrePort(Ville ville){
        if(peutPoserPort(ville)){
            ArrayList<CarteTransport> cartesPosables = new ArrayList<>();
            for (int i = 0; i < this.cartesTransport.size(); i++) {
                if (this.cartesTransport.get(i).getAncre() || this.cartesTransport.get(i).getType() == TypeCarteTransport.JOKER ){
                    cartesPosables.add(this.cartesTransport.get(i));
                }
            }
            this.ports.add(ville);
        }
    }

    public List<CarteTransport> getCartesTransport() {
        return cartesTransport;
    }

    public int nombreCouleurWagonJoueur(List<CarteTransport> cartesTransport,Couleur couleur){
        List<CarteTransport> listeCartesWagon = new ArrayList<>();
        for(int i=0; i<this.cartesTransport.size(); i++){
            if(cartesTransport.get(i).getType().equals(TypeCarteTransport.WAGON)){
                listeCartesWagon.add(cartesTransport.get(i));
            }
        }
        return Collections.frequency(listeCartesWagon, couleur);
    }

    public int nombreCouleurBateauJoueur(List<CarteTransport> cartesTransport,Couleur couleur){
        List<CarteTransport> listeCartesBateau = new ArrayList<>();
        for(int i=0; i<this.cartesTransport.size(); i++){
            if(cartesTransport.get(i).getType().equals(TypeCarteTransport.BATEAU)){
                listeCartesBateau.add(cartesTransport.get(i));
            }
        }
        return Collections.frequency(listeCartesBateau, couleur);
    }

    public int nombreJoker(List<CarteTransport> cartesTransport){
        List<CarteTransport> listeCartesJoker = new ArrayList<>();
        for(int i=0; i<this.cartesTransport.size(); i++){
            if(cartesTransport.get(i).getType().equals(TypeCarteTransport.JOKER)){
                listeCartesJoker.add(cartesTransport.get(i));
            }
        }
        return listeCartesJoker.size();
    }






    /**
     * Renvoie une représentation du joueur sous la forme d'un dictionnaire de
     * valeurs sérialisables
     * (qui sera converti en JSON pour l'envoyer à l'interface graphique)
     */
    Map<String, Object> dataMap() {
        return Map.ofEntries(
                Map.entry("nom", nom),
                Map.entry("couleur", couleur),
                Map.entry("score", score),
                Map.entry("pionsWagon", nbPionsWagon),
                Map.entry("pionsWagonReserve", nbPionsWagonEnReserve),
                Map.entry("pionsBateau", nbPionsBateau),
                Map.entry("pionsBateauReserve", nbPionsBateauEnReserve),
                Map.entry("destinationsIncompletes",
                        destinations.stream().filter(d -> !destinationEstComplete(d)).toList()),
                Map.entry("destinationsCompletes", destinations.stream().filter(this::destinationEstComplete).toList()),
                Map.entry("main", cartesTransport.stream().sorted().toList()),
                Map.entry("inPlay", cartesTransportPosees.stream().sorted().toList()),
                Map.entry("ports", ports.stream().map(Ville::nom).toList()),
                Map.entry("routes", routes.stream().map(Route::getNom).toList()));
    }
}
