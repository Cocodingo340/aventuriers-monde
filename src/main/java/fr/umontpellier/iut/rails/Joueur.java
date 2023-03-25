package fr.umontpellier.iut.rails;

import com.sun.source.tree.WhileLoopTree;
import fr.umontpellier.iut.rails.data.*;
import org.jetbrains.annotations.NotNull;

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

    public void cheat(){
        this.routes.addAll(jeu.getRoutesLibres());
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


        boolean choixValide=false;

        while (!choixValide) {


            List<String> choixPossibles = new ArrayList<>();
            List<String> choixVilles = new ArrayList<>();
            List<String> choixCartesVisibles = new ArrayList<>();
            List<String> choixRoutes = new ArrayList<>();


            //TOUTES LES VILLES
            for (Ville v : jeu.getPortsLibres()) {
                choixPossibles.add(v.nom());
                choixVilles.add(v.nom());

            }

            //TOUTES LES CARTES TRANSPORT VISIBLES
            for (CarteTransport c : jeu.getCartesTransportVisibles()) {
                choixPossibles.add(c.getNom());
                choixCartesVisibles.add(c.getNom());
            }

            //TOUTES LES ROUTES LIBRES
            for (Route r : jeu.getRoutesLibres()) {
                choixPossibles.add(r.getNom());
                choixRoutes.add(r.getNom());
            }

            choixPossibles.add("PIONS WAGON");
            choixPossibles.add("DESTINATION");
            choixPossibles.add("PIONS BATEAU");
            choixPossibles.add("BATEAU");
            choixPossibles.add("WAGON");

            String choixFinal = choisir(
                    "Veuillez choisir une option a effectuer ce tour :\n",
                    choixPossibles,
                    null,
                    false);


            if (choixFinal.equals("PIONS WAGON")) {
                choixValide = true;
                int nbPionsEchanges = choisirNombre("Choissisez combien de Pions Wagons voulez-vous prendre", 1, nbPionsBateauEnReserve);
                nbPionsBateauEnReserve += nbPionsEchanges;
                nbPionsWagon += nbPionsEchanges;
                nbPionsWagonEnReserve -= nbPionsEchanges;
                nbPionsBateau -= nbPionsEchanges;
                score -= nbPionsEchanges;
            } else if (choixFinal.equals("PIONS BATEAU")) {
                choixValide = true;
                int nbPionsEchanges = choisirNombre("Choissisez combien de Pions Bateau voulez-vous prendre", 1, nbPionsWagonEnReserve);
                nbPionsBateauEnReserve -= nbPionsEchanges;
                nbPionsWagon -= nbPionsEchanges;
                nbPionsWagonEnReserve += nbPionsEchanges;
                nbPionsBateau += nbPionsEchanges;
                score -= nbPionsEchanges;

            } else if (choixFinal.equals("DESTINATION")) {
                choixValide = true;
                jeu.jouerTourPiocherDestination();

            } else if (choixRoutes.contains(choixFinal)) {
                for (Route r : jeu.getRoutesLibres()) {
                    if (r.getNom().equals(choixFinal)) {
                        if(r.getClass().equals(RouteTerrestre.class)) {
                            if (r.getCouleur().equals(Couleur.GRIS)) {

                                if (!peutPayerWagonGrisAvecQuellesCouleurs(this.cartesTransport,r.getLongueur()).isEmpty()) {
                                    choixValide = true;
                                    prendreRouteTerrestreGrise(r);
                                }

                                else{
                                    log("Vous ne pouvez pas poser cette route terrestre");
                                    break;
                                }

                            }
                            else {
                                if (peutPrendreRouteTerrestre(r)) {
                                    choixValide = true;
                                    prendreRouteTerrestre(r);
                                }

                                else{
                                    log("Vous ne pouvez pas poser cette route terrestre");
                                    break;
                                }
                            }
                        }
                        else if(r.getClass().equals(RouteMaritime.class)){
                            if (r.getCouleur().equals(Couleur.GRIS)) {

                                if (!peutPayerBateauGrisAvecQuellesCouleurs(this.cartesTransport,r.getLongueur()).isEmpty()) {
                                    choixValide = true;
                                    prendreRouteMaritimeGrise(r);
                                }

                                else{
                                    log("Vous ne pouvez pas poser cette route terrestre");
                                    break;
                                }

                            }
                            else {
                                if (peutPrendreRouteMaritime(r)) {
                                    choixValide = true;
                                    prendreRouteMaritime(r);
                                }

                                else{
                                    log("Vous ne pouvez pas poser cette route terrestre");
                                    break;
                                }
                            }
                        }
                    }
                }

            } else if (choixFinal.equals("BATEAU")) {
                choixValide = true;
                piocherCarteBateauDAbord();

            } else if (choixFinal.equals("WAGON")) {
                choixValide = true;
                piocherCarteWagonDabord();

            } else if (choixCartesVisibles.contains(choixFinal)) {
                choixValide = true;
                for (CarteTransport a : jeu.getCartesTransportVisibles()) {
                    if (a.getNom().equals(choixFinal)) {
                        piocherCarteVisibleDabord(a);
                    }
                }
            } else if (choixVilles.contains(choixFinal)) {
                for (Ville v : jeu.getPortsLibres()) {
                    if (v.nom().equals(choixFinal)) {
                        if (peutPoserPort(v)) {
                            choixValide = true;
                            prendrePort(v);
                        } else {
                            log("Vous ne pouvez pas prendre le port de " + v.nom());
                            break;
                        }

                    }
                }
            }
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
        for(Route route: jeu.getRoutesLibres()){
            if (d.equals(jeu.getRoutesLibres())){
                return true;
            }
        }
        return false;
    }

    public int calculerScoreFinal() {
        int lg= Joueur.this.routes.size();
        if (lg==1){
            return this.score= score +1;
        }
        else if (lg==2) {
            return this.score= score +2;
        }
        else if (lg==3) {
            return this.score= score +4;
        }
        else if (lg==4) {
            return this.score= score +7;
        }
        else if (lg==5) {
            return this.score= score +10;
        }
        else if (lg==6) {
            return this.score= score +15;
        }
        else if (lg==7) {
            return this.score= score +18;
        }
        else if (lg==8) {
            return this.score= score +21;
        }
        return this.score;
    }
    public boolean possedeRouteVille(Ville ville){
        for (Route r: this.routes) {
            if(r.getVille1().equals(ville) || r.getVille2().equals(ville)){
                return true;
            }
        }
        return false;
    }



    public void piocherCarteWagonDabord(){
        jeu.defausserSi3CartesJokerSontPresent();
        this.cartesTransport.add(jeu.piocherCarteWagon());
        piocherDeuxiemeChoix(false);
    }

    public void piocherCarteBateauDAbord() {
        jeu.defausserSi3CartesJokerSontPresent();
        this.cartesTransport.add(jeu.piocherCarteBateau());
        piocherDeuxiemeChoix(false);
    }

    public void piocherCarteVisibleDabord(CarteTransport carteChosi){
        jeu.defausserSi3CartesJokerSontPresent();

        this.cartesTransport.add(carteChosi);

        if(carteChosi.getType().equals(TypeCarteTransport.JOKER)){
            jeu.enleverCarteVisible(carteChosi);
            remplacerCarte();
        }
        else{
            jeu.enleverCarteVisible(carteChosi);
            remplacerCarte();
            piocherDeuxiemeChoix(false);

        }
    }

    public void piocherDeuxiemeChoix(boolean aDejaPiocherJoker){
        jeu.defausserSi3CartesJokerSontPresent();
        if(aDejaPiocherJoker) {
            List<String> choixPossibles = new ArrayList<>();
            List<String> BateauOuWagon = new ArrayList<>();
            choixPossibles.add("WAGON"); BateauOuWagon.add("WAGON");
            choixPossibles.add("BATEAU"); BateauOuWagon.add("BATEAU");
            for (CarteTransport c : jeu.getCartesTransportVisibles()) {
                choixPossibles.add(c.getNom());
            }

            String choix = choisir("Quelle autre carte voulez-vous piocher ?", choixPossibles, null, true);
            if (choix.equals("BATEAU")) {
                this.cartesTransport.add(jeu.piocherCarteBateau());
            }
            if (choix.equals("WAGON")) {
                this.cartesTransport.add(jeu.piocherCarteWagon());
            }
            else {
                for (CarteTransport c: jeu.getCartesTransportVisibles()) {
                    if (c.getNom().equals(choix)) {
                        this.cartesTransport.add(c);
                        jeu.enleverCarteVisible(c);
                        remplacerCarte();
                        break;
                    }
                }

            }
        }
        else{
            List<String> choixPossibles = new ArrayList<>();
            List<String> BateauOuWagon = new ArrayList<>();
            choixPossibles.add("WAGON"); BateauOuWagon.add("WAGON");
            choixPossibles.add("BATEAU"); BateauOuWagon.add("BATEAU");
            for (CarteTransport c : jeu.getCartesTransportVisibles()) {
                if (!c.getType().equals(TypeCarteTransport.JOKER)) {
                    choixPossibles.add(c.getNom());
                }
            }
            String choix = choisir("Quelle autre carte voulez-vous piocher ?", choixPossibles, null, true);
            if (choix.equals("BATEAU")) {
                this.cartesTransport.add(jeu.piocherCarteBateau());
            }
            if (choix.equals("WAGON")) {
                this.cartesTransport.add(jeu.piocherCarteWagon());
            }
            else {
                for (CarteTransport c: jeu.getCartesTransportVisibles()) {
                    if (c.getNom().equals(choix)) {
                        this.cartesTransport.add(c);
                        jeu.enleverCarteVisible(c);
                        remplacerCarte();
                        break;
                    }
                }

            }
        }
    }

    public void remplacerCarte(){
        List<String> BateauOuWagon = new ArrayList<>();
        if(!jeu.piocheBateauEstVide()){
            BateauOuWagon.add("BATEAU");
        }
        if(!jeu.piocheWagonEstVide()){
            BateauOuWagon.add("WAGON");
        }
        if(BateauOuWagon.isEmpty()){
            return;
        }

        String choixRemplacement = choisir("Par quelle carte voulez vous remplacer la carte manquante  ?", BateauOuWagon, null, false);
        if (choixRemplacement.equals("BATEAU")) {
            jeu.remplacerCarteVisible(choixRemplacement);
        } else {
            jeu.remplacerCarteVisible(choixRemplacement);
        }
    }
    /**************************************************************************
     *  Gestion des ports
     **************************************************************************/

    public boolean peutPoserPort(Ville ville){
        if(ville.estPort()){
            if(possedeRouteVille(ville)){
                if(peutPayerPort()) {
                    log("Vous pouvez poser un port");
                    return true;
                }
            }
        }

        log("Vous pouvez pas poser un port");
        return false;

    }

    public void prendrePort(Ville ville){
        if(peutPoserPort(ville)){
            List<CarteTransport> listeCartesPourPayer = this.recupererCartesValidesPourPayerPort();
            this.payerPort(listeCartesPourPayer);
            this.ports.add(ville);
            jeu.enleverPortLibre(ville);
        }
    }


    public void payerPort(@NotNull List<CarteTransport> cartesPourPayer){
        int nbCartesWagonPourPayer=0;
        int nbCartesBateauPourPayer=0;
        List<String> nomListeCartesPourPayer = new ArrayList<String>();
        for (CarteTransport c: cartesPourPayer) {
            nomListeCartesPourPayer.add(c.getNom());
            if (c.getType().equals(TypeCarteTransport.BATEAU)){
                nbCartesBateauPourPayer++;
            }
            if (c.getType().equals(TypeCarteTransport.WAGON)){
                nbCartesWagonPourPayer++;
            }
        }

        int resteCartesBateauAPayer = 2;
        int resteCartesWagonAPayer = 2;
        int resteAPayer = 4;



        while (resteAPayer>0){
            String choix = choisir(
                    "Choissisez une carte pour payer",
                    nomListeCartesPourPayer,
                    new ArrayList<>(),
                    false);

            for (CarteTransport c: cartesPourPayer) {
                if ((c.getNom()).equals(choix)) {
                    if(c.getType().equals(TypeCarteTransport.BATEAU) ) {
                        jeu.defausserCarteBateau(c);
                        resteCartesBateauAPayer--;
                        resteAPayer--;
                        if(resteCartesBateauAPayer==0){
                            for (CarteTransport c2: cartesPourPayer) {
                                if (c2.getType().equals(TypeCarteTransport.BATEAU)) {
                                    nomListeCartesPourPayer.remove(c2.getNom());

                                }
                            }
                        }
                    }
                    else if (c.getType().equals(TypeCarteTransport.WAGON)){
                        jeu.defausserCarteWagon(c);
                        resteCartesWagonAPayer--;
                        resteAPayer--;
                        if(resteCartesWagonAPayer==0){
                            for (CarteTransport c2: cartesPourPayer) {
                                if (c2.getType().equals(TypeCarteTransport.WAGON)) {
                                    nomListeCartesPourPayer.remove(c2.getNom());
                                }
                            }
                        }
                    }
                    else if (c.getType().equals(TypeCarteTransport.JOKER) && nbCartesBateauPourPayer>=nbCartesWagonPourPayer){
                        jeu.defausserCarteWagon(c);
                        resteCartesWagonAPayer--;
                        resteAPayer--;
                        if(resteCartesWagonAPayer==0){
                            for (CarteTransport c2: cartesPourPayer) {
                                if (c2.getType().equals(TypeCarteTransport.WAGON)) {
                                    nomListeCartesPourPayer.remove(c2.getNom());
                                }
                            }
                        }
                    }
                    else if (c.getType().equals(TypeCarteTransport.JOKER) && nbCartesBateauPourPayer<=nbCartesWagonPourPayer){
                        jeu.defausserCarteWagon(c);
                        resteCartesBateauAPayer--;
                        resteAPayer--;
                        if(resteCartesBateauAPayer==0){
                            for (CarteTransport c2: cartesPourPayer) {
                                if (c2.getType().equals(TypeCarteTransport.BATEAU)) {
                                    nomListeCartesPourPayer.remove(c2.getNom());
                                }
                            }
                        }
                    }
                    this.cartesTransport.remove(c);
                    break;

                }


            }



        }

    }

    public List<CarteTransport> recupererCartesValidesPourPayerPort(){
        Couleur bonneCouleur=peutPayerPortAvecQuelleCouleur(this.cartesTransport);
        List<CarteTransport> listeCartesValidesPourPayer = new ArrayList<CarteTransport>();
        for (CarteTransport c: this.cartesTransport) {
            if(c.getCouleur().equals(bonneCouleur) && c.getAncre()){
                listeCartesValidesPourPayer.add(c);
            }
            if(c.getType().equals(TypeCarteTransport.JOKER)){
                listeCartesValidesPourPayer.add(c);
            }
        }
        return listeCartesValidesPourPayer;
    }

    public List<String> recupererStringCartesValidesPourPayerPort(){
        List<String> rep= new ArrayList<String>();
        for (CarteTransport c: recupererCartesValidesPourPayerPort()) {
            rep.add(c.getNom());
        }
        return rep;
    }
    public boolean peutPayerPort(){
        int sommeCouleur;
        List<CarteTransport> liste = new ArrayList<>(this.cartesTransport);
        List<Couleur> ListeCouleur = new ArrayList<Couleur>(EnumSet.allOf(Couleur.class));
        for (Couleur c: ListeCouleur) {
            sommeCouleur = nombreCouleurWagonJoueur(liste,c)+nombreCouleurBateauJoueur(liste,c)+nombreJoker(liste);
            if(sommeCouleur>=4){
                log("Vous avez assez de carte pour payer le port");
                return true;
            }
        }
        log("Vous n'avez pas assez de carte pour payer le port");
        return false;
    }

    //ON SUPPOSE QUE LE JOUEUR EST SUR DE PAYER LE PORT
    public Couleur peutPayerPortAvecQuelleCouleur(List<CarteTransport> liste){
        List<Couleur> ListeCouleur = new ArrayList<Couleur>(EnumSet.allOf(Couleur.class));
        Couleur bonneCoul=null;
        for (Couleur c: ListeCouleur) {
            if(nombreCouleurWagonJoueur(liste,c)+nombreCouleurBateauJoueur(liste,c)+nombreJoker(liste)>=4){
                bonneCoul=c;
            }
        }
        return bonneCoul;
    }



    public List<CarteTransport> getCartesTransport() {
        return cartesTransport;
    }



    public List<String> recupererNomCartesTransport(List<CarteTransport> cartesTransport){
        List<String> nomCartes = new ArrayList<>();
        for (CarteTransport c: cartesTransport) {
            nomCartes.add(c.getNom());
        }
        return nomCartes;
    }
    
    public List<Couleur> listeCouleur(){
        List<Couleur> couleurs = new ArrayList<>();
        for (Couleur couleur : Couleur.values()) {
            couleurs.add(couleur);
        }
        return couleurs;
    }

    public int nombreCouleurWagonJoueur(List<CarteTransport> cartesTransport,Couleur couleur){

        int nombreCouleurWagonJoueur = 0;
        List<CarteTransport> listeCartesWagon = new ArrayList<>();
        for (CarteTransport c: cartesTransport) {
            if(c.getType().equals(TypeCarteTransport.WAGON)){
                listeCartesWagon.add(c);
            }
        }
        for (CarteTransport c: cartesTransport) {
            if(c.getCouleur().equals(couleur)){
                nombreCouleurWagonJoueur++;
            }
        }
        return nombreCouleurWagonJoueur;
    }

    public List<CarteTransport> recupereCarteWagonDeCouleur(List<CarteTransport> cartesTransport,Couleur couleur){
        List<CarteTransport> listeCartesWagon = new ArrayList<>();
        for (CarteTransport c: cartesTransport) {
            if(c.getType().equals(TypeCarteTransport.WAGON)){
                listeCartesWagon.add(c);
            }
        }
        List<CarteTransport> listeCartesWagonCouleur = new ArrayList<>();
        for (CarteTransport c: listeCartesWagon) {
            if(c.getCouleur().equals(couleur)){
                listeCartesWagonCouleur.add(c);
            }
        }
        return listeCartesWagonCouleur;
    }

    public int nombreCouleurBateauJoueur(List<CarteTransport> cartesTransport,Couleur couleur){
        List<CarteTransport> listeCartesBateau = new ArrayList<>();
        int nombreCouleurBateauJoueur = 0;
        for (CarteTransport c: cartesTransport) {
            if(c.getType().equals(TypeCarteTransport.BATEAU)){
                listeCartesBateau.add(c);
            }
        }
        for (CarteTransport c: cartesTransport) {
            if(c.getCouleur().equals(couleur)){
                nombreCouleurBateauJoueur++;
            }
        }

        return nombreCouleurBateauJoueur;
    }

    public List<CarteTransport> recupererCarteBateauDeCouleur(List<CarteTransport> cartesTransport,Couleur couleur){
        List<CarteTransport> listeCartesBateau = new ArrayList<>();
        for (CarteTransport c: cartesTransport) {
            if(c.getType().equals(TypeCarteTransport.BATEAU)){
                listeCartesBateau.add(c);
            }
        }
        List<CarteTransport> listeCartesBateauCouleur = new ArrayList<>();
        for (CarteTransport c: listeCartesBateau) {
            if(c.getCouleur().equals(couleur)){
                listeCartesBateauCouleur.add(c);
            }
        }
        return listeCartesBateauCouleur;
    }

    public int nombreJoker(List<CarteTransport> cartesTransport){
        List<CarteTransport> listeCartesJoker = new ArrayList<>();
        for (CarteTransport c: cartesTransport) {
            if(c.getType().equals(TypeCarteTransport.JOKER)){
                listeCartesJoker.add(c);
            }
        }
        return listeCartesJoker.size(); //on renvoie le nombre de joker
    }

    public List<CarteTransport> recupererCarteJoker(List<CarteTransport> cartesTransport){
        List<CarteTransport> listeCartesJoker = new ArrayList<>();
        for (CarteTransport c: cartesTransport) {
            if(c.getType().equals(TypeCarteTransport.JOKER)){
                listeCartesJoker.add(c);
            }
        }
        return listeCartesJoker;
    }

    /**************************************************************************
     * Gestion des Routes
     *************************************************************************/

    /* if(route.getClass().equals(RouteTerrestre.class)){

        }*/

    public int nombreCouleurBateauJoueurAvecDoubles(Couleur couleur){
        int nb=0;
        for (CarteTransport c: this.cartesTransport) {
            if(c.getType().equals(TypeCarteTransport.BATEAU)){
                if(c.getCouleur().equals(couleur)){
                    if(c.estDouble()){
                        nb+=2;
                    }
                    else{
                        nb++;
                    }
                }
            }
        }
        return nb;
    }

    public boolean peutPrendreRouteTerrestre(Route route){
        if(this.nombreCouleurWagonJoueur(this.cartesTransport,route.getCouleur()) + this.nombreJoker(this.cartesTransport)>=route.getLongueur() && this.nbPionsWagon>=route.getLongueur()){
            return true;
        }
        return false;
    }

    public void prendreRouteTerrestre(Route route){
        List<CarteTransport> copieCarteTransport= new ArrayList<>(this.cartesTransport);

        int prix = route.getLongueur();
        List<String> cartesPourPayerString=recupererNomCartesTransport(this.cartesTransport);
        while (prix>0) {
            String choix = choisir("Veuillez Payer pour la route", cartesPourPayerString, null, false);
            for (CarteTransport c : copieCarteTransport) {
                if (c.getNom().equals(choix)) {
                    if(c.getType().equals(TypeCarteTransport.WAGON) || c.getType().equals(TypeCarteTransport.JOKER)){
                        if(c.getType().equals(TypeCarteTransport.WAGON)){
                            if(c.getCouleur().equals(route.getCouleur())){
                                prix--;
                                this.cartesTransport.remove(c);
                                jeu.defausserCarteWagon(c);
                                copieCarteTransport.remove(c);
                                break;
                            }
                            else{
                                break;
                            }
                        }
                        else{
                            prix--;
                            this.cartesTransport.remove(c);
                            jeu.defausserCarteWagon(c);
                            copieCarteTransport.remove(c);
                            break;
                        }
                    }
                    else{
                        copieCarteTransport.remove(c);
                        break;
                    }
                }
            }

        }
        this.nbPionsWagon-=route.getLongueur();
        this.score+=route.getScore();
        this.routes.add(route);

    }

    public boolean peutPrendreRouteMaritime(Route route){
        if(this.nombreCouleurBateauJoueurAvecDoubles(route.getCouleur()) + this.nombreJoker(this.cartesTransport) >=route.getLongueur() && this.nbPionsBateau>=route.getLongueur()){
            return true;
        }
        return false;
    }

    public void prendreRouteMaritime(Route route){
        List<CarteTransport> copieCarteTransport= new ArrayList<>(this.cartesTransport);
        List<CarteTransport> CarteTransportDefausse= new ArrayList<>();
        int prix = route.getLongueur();
        List<String> cartesPourPayerString=recupererNomCartesTransport(this.cartesTransport);
        while (prix>0) {
            String choix = choisir("Veuillez Payer pour la route", cartesPourPayerString, null, false);
            for (CarteTransport c : copieCarteTransport) {
                if (c.getNom().equals(choix)) {
                    if(c.getType().equals(TypeCarteTransport.BATEAU)){
                        if(c.getCouleur().equals(route.getCouleur())) {
                            if (c.estDouble() && prix==1 && combienDeBateauSimpleRestant(CarteTransportDefausse, route.getCouleur())>0) {
                                copieCarteTransport.remove(c);
                                break;
                            }
                            else if(c.estDouble() && prix==1 && combienDeBateauSimpleRestant(CarteTransportDefausse, route.getCouleur())==0){
                                prix--;
                                this.cartesTransport.remove(c);
                                CarteTransportDefausse.add(c);
                                jeu.defausserCarteBateau(c);
                                copieCarteTransport.remove(c);
                                break;
                            }
                            else if(c.estDouble() && prix>1){
                                prix-=2;
                                this.cartesTransport.remove(c);
                                jeu.defausserCarteBateau(c);
                                CarteTransportDefausse.add(c);
                                copieCarteTransport.remove(c);
                                break;
                            }
                            else if(!c.estDouble() && prix%2==0 && combienDeBateauDoubleRestant(copieCarteTransport, route.getCouleur())>=prix/2 && combienDeBateauSimpleRestant(copieCarteTransport, route.getCouleur())<prix/2){
                                copieCarteTransport.remove(c);
                                break;
                            }
                            else{
                                prix--;
                                this.cartesTransport.remove(c);
                                jeu.defausserCarteBateau(c);
                                CarteTransportDefausse.add(c);
                                copieCarteTransport.remove(c);
                                break;
                            }
                        }
                        else{
                            copieCarteTransport.remove(c);
                            break;
                        }
                    }
                    else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                        prix--;
                        this.cartesTransport.remove(c);
                        CarteTransportDefausse.add(c);
                        copieCarteTransport.remove(c);
                        jeu.defausserCarteWagon(c);
                        break;
                    }
                    else{
                        copieCarteTransport.remove(c);
                        break;
                    }
                }
            }
        }
        this.nbPionsBateau-=route.getLongueur();
        this.score+=route.getScore();
        this.routes.add(route);
        jeu.enleverRouteLibre(route);
    }

    public void prendreRouteTerrestreGrise(Route route){
        if(!listeCouleur().isEmpty()) {


            List<Couleur> ListeBonneCouleur = peutPayerWagonGrisAvecQuellesCouleurs(this.cartesTransport, route.getLongueur());
            List<CarteTransport> copieCarteTransport = new ArrayList<>(this.cartesTransport);
            Couleur couleurFinal=null;
            int prix = route.getLongueur();
            List<String> cartesPourPayerString = recupererNomCartesTransport(this.cartesTransport);
            while (prix > 0) {
                while(couleurFinal==null) {
                    String choix = choisir("Veuillez Payer pour la route", cartesPourPayerString, null, false);
                    for (CarteTransport c : copieCarteTransport) {
                        if (c.getNom().equals(choix)) {
                            if (c.getType().equals(TypeCarteTransport.WAGON)) {
                                if (ListeBonneCouleur.contains(c.getCouleur())) {
                                    couleurFinal = c.getCouleur();
                                    prix--;
                                    this.cartesTransport.remove(c);
                                    jeu.defausserCarteWagon(c);
                                    copieCarteTransport.remove(c);
                                    break;
                                } else {
                                    copieCarteTransport.remove(c);
                                    break;
                                }

                            } else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                                prix--;
                                this.cartesTransport.remove(c);
                                jeu.defausserCarteWagon(c);
                                copieCarteTransport.remove(c);
                                break;
                            } else {
                                copieCarteTransport.remove(c);
                                break;
                            }
                        }
                    }
                }
                String choix = choisir("Veuillez Payer pour la route", cartesPourPayerString, null, false);
                for (CarteTransport c : copieCarteTransport) {
                    if (c.getNom().equals(choix)) {
                        if (c.getType().equals(TypeCarteTransport.WAGON)) {
                            if (c.getCouleur().equals(couleurFinal)) {
                                prix--;
                                this.cartesTransport.remove(c);
                                jeu.defausserCarteWagon(c);
                                copieCarteTransport.remove(c);
                                break;
                            } else {
                                copieCarteTransport.remove(c);
                                break;
                            }

                        } else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            prix--;
                            this.cartesTransport.remove(c);
                            jeu.defausserCarteWagon(c);
                            copieCarteTransport.remove(c);
                            break;
                        } else {
                            copieCarteTransport.remove(c);
                            break;
                        }
                    }
                }

            }
            this.nbPionsWagon-=route.getLongueur();
            this.score+=route.getScore();
            this.routes.add(route);
            jeu.enleverRouteLibre(route);
        }

    }

    public void prendreRouteMaritimeGrise(Route route) {
        if (!listeCouleur().isEmpty()) {
            List<Couleur> ListeBonneCouleur = peutPayerBateauGrisAvecQuellesCouleurs(this.cartesTransport, route.getLongueur());
            List<CarteTransport> CarteTransportDefausse= new ArrayList<>();
            List<CarteTransport> copieCarteTransport = new ArrayList<>(this.cartesTransport);
            Couleur couleurFinal = null;
            int prix = route.getLongueur();
            List<String> cartesPourPayerString = recupererNomCartesTransport(this.cartesTransport);
            while (prix > 0) {
                while (couleurFinal == null) {
                    String choix = choisir("Veuillez Payer pour la route", cartesPourPayerString, null, false);
                    for (CarteTransport c : copieCarteTransport) {
                        if (c.getNom().equals(choix)) {
                            if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                                if (ListeBonneCouleur.contains(c.getCouleur())) {
                                    if (c.estDouble() && prix == 1 && combienDeBateauSimpleRestant(CarteTransportDefausse, route.getCouleur()) > 0) {
                                        copieCarteTransport.remove(c);
                                        break;
                                    } else if (c.estDouble() && prix == 1 && combienDeBateauSimpleRestant(CarteTransportDefausse, route.getCouleur()) == 0) {
                                        prix--;
                                        couleurFinal = c.getCouleur();
                                        this.cartesTransport.remove(c);
                                        CarteTransportDefausse.add(c);
                                        jeu.defausserCarteBateau(c);
                                        copieCarteTransport.remove(c);
                                        break;
                                    } else if (c.estDouble() && prix > 1) {
                                        prix -= 2;
                                        couleurFinal = c.getCouleur();
                                        this.cartesTransport.remove(c);
                                        jeu.defausserCarteBateau(c);
                                        CarteTransportDefausse.add(c);
                                        copieCarteTransport.remove(c);
                                        break;
                                    } else if (!c.estDouble() && prix % 2 == 0 && combienDeBateauDoubleRestant(copieCarteTransport, route.getCouleur()) >= prix / 2 && combienDeBateauSimpleRestant(copieCarteTransport, route.getCouleur()) < prix / 2) {
                                        copieCarteTransport.remove(c);
                                        break;
                                    } else {
                                        prix--;
                                        couleurFinal = c.getCouleur();
                                        this.cartesTransport.remove(c);
                                        jeu.defausserCarteBateau(c);
                                        CarteTransportDefausse.add(c);
                                        copieCarteTransport.remove(c);
                                        break;
                                    }
                                } else {
                                    copieCarteTransport.remove(c);
                                    break;
                                }
                            } else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                                prix--;
                                this.cartesTransport.remove(c);
                                CarteTransportDefausse.add(c);
                                copieCarteTransport.remove(c);
                                jeu.defausserCarteWagon(c);
                                break;
                            } else {
                                copieCarteTransport.remove(c);
                                break;
                            }
                        }
                    }
                }
                String choix = choisir("Veuillez Payer pour la route", cartesPourPayerString, null, false);
                for (CarteTransport c : copieCarteTransport) {
                    if (c.getNom().equals(choix)) {
                        if(c.getType().equals(TypeCarteTransport.BATEAU)){
                            if(c.getCouleur().equals(couleurFinal)) {
                                if (c.estDouble() && prix==1 && combienDeBateauSimpleRestant(CarteTransportDefausse, route.getCouleur())>0) {
                                    copieCarteTransport.remove(c);
                                    break;
                                }
                                else if(c.estDouble() && prix==1 && combienDeBateauSimpleRestant(CarteTransportDefausse, route.getCouleur())==0){
                                    prix--;
                                    this.cartesTransport.remove(c);
                                    CarteTransportDefausse.add(c);
                                    jeu.defausserCarteBateau(c);
                                    copieCarteTransport.remove(c);
                                    break;
                                }
                                else if(c.estDouble() && prix>1){
                                    prix-=2;
                                    this.cartesTransport.remove(c);
                                    jeu.defausserCarteBateau(c);
                                    CarteTransportDefausse.add(c);
                                    copieCarteTransport.remove(c);
                                    break;
                                }
                                else if(!c.estDouble() && prix%2==0 && combienDeBateauDoubleRestant(copieCarteTransport, route.getCouleur())>=prix/2 && combienDeBateauSimpleRestant(copieCarteTransport, route.getCouleur())<prix/2){
                                    copieCarteTransport.remove(c);
                                    break;
                                }
                                else{
                                    prix--;
                                    this.cartesTransport.remove(c);
                                    jeu.defausserCarteBateau(c);
                                    CarteTransportDefausse.add(c);
                                    copieCarteTransport.remove(c);
                                    break;
                                }
                            }
                            else{
                                copieCarteTransport.remove(c);
                                break;
                            }
                        }
                        else if (c.getType().equals(TypeCarteTransport.JOKER)) {
                            prix--;
                            this.cartesTransport.remove(c);
                            CarteTransportDefausse.add(c);
                            copieCarteTransport.remove(c);
                            jeu.defausserCarteWagon(c);
                            break;
                        }
                        else{
                            copieCarteTransport.remove(c);
                            break;
                        }
                    }
                }
            }
            this.nbPionsBateau-=route.getLongueur();
            this.score+=route.getScore();
            this.routes.add(route);
            jeu.enleverRouteLibre(route);
        }
    }

    public List<Couleur> peutPayerWagonGrisAvecQuellesCouleurs(List<CarteTransport> list, int prixRoute){
        List<Couleur> ListeCouleur = new ArrayList<Couleur>(EnumSet.allOf(Couleur.class));
        List<Couleur> ListeCouleurFinal = new ArrayList<>();
        for(Couleur couleur : ListeCouleur){
            int cpt=0;
            for (CarteTransport c : list) {
                if (c.getType().equals(TypeCarteTransport.WAGON)) {
                    if (c.getCouleur().equals(couleur)) {
                        cpt+=1;
                    }
                }
            }
            if(cpt+nombreJoker(list)>=prixRoute){
                ListeCouleurFinal.add(couleur);
            }
        }
        return ListeCouleurFinal;
    }

    public List<Couleur> peutPayerBateauGrisAvecQuellesCouleurs(List<CarteTransport> list, int prixRoute){
        List<Couleur> ListeCouleur = new ArrayList<Couleur>(EnumSet.allOf(Couleur.class));
        List<Couleur> ListeCouleurFinal = new ArrayList<>();
        for(Couleur couleur : ListeCouleur){
            int cpt=0;
            for (CarteTransport c : list) {
                if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                    if (c.getCouleur().equals(couleur)) {
                        if(c.estDouble()){
                            cpt += 2;
                        }
                        else {
                            cpt += 1;
                        }
                    }
                }
            }
            if(cpt+nombreJoker(list)>=prixRoute){
                ListeCouleurFinal.add(couleur);
            }
        }
        return ListeCouleurFinal;
    }


    public int combienDeBateauSimpleRestant(List<CarteTransport> list, Couleur couleur) {
        int nb = 0;
        for (CarteTransport c : list) {
            if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                if (c.getCouleur().equals(couleur)) {
                    if (!c.estDouble()) {
                        nb++;
                    }
                }
            }
        }
        return nb;
    }

    public int combienDeBateauDoubleRestant(List<CarteTransport> list, Couleur couleur) {
        int nb = 0;
        for (CarteTransport c : list) {
            if (c.getType().equals(TypeCarteTransport.BATEAU)) {
                if (c.getCouleur().equals(couleur)) {
                    if (c.estDouble()) {
                        nb++;
                    }
                }
            }
        }
        return nb;
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
