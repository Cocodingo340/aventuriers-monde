package fr.umontpellier.iut.rails;

import com.google.gson.Gson;
import fr.umontpellier.iut.gui.GameServer;
import fr.umontpellier.iut.rails.data.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Jeu implements Runnable {
    /**
     * Liste des joueurs
     */
    private final List<Joueur> joueurs;
    /**
     * Le joueur dont c'est le tour
     */
    private Joueur joueurCourant;
    /**
     * Liste des villes disponibles sur le plateau de jeu
     */
    private final List<Ville> portsLibres;
    /**
     * Liste des routes disponibles sur le plateau de jeu
     */
    private final List<Route> routesLibres;
    /**
     * Pile de pioche et défausse des cartes wagon
     */
    private final PilesCartesTransport pilesDeCartesWagon;
    /**
     * Pile de pioche et défausse des cartes bateau
     */
    private final PilesCartesTransport pilesDeCartesBateau;
    /**
     * Cartes de la pioche face visible (normalement il y a 6 cartes face visible)
     */
    private final List<CarteTransport> cartesTransportVisibles;
    /**
     * Pile des cartes "Destination"
     */
    private final List<Destination> pileDestinations;
    /**
     * File d'attente des instructions recues par le serveur
     */
    private final BlockingQueue<String> inputQueue;
    /**
     * Messages d'information du jeu
     */
    private final List<String> log;

    private String instruction;
    private Collection<Bouton> boutons;

    public Jeu(String[] nomJoueurs) {
        // initialisation des entrées/sorties
        inputQueue = new LinkedBlockingQueue<>();
        log = new ArrayList<>();

        // création des villes et des routes
        Plateau plateau = Plateau.makePlateauMonde();
        portsLibres = plateau.getPorts();
        routesLibres = plateau.getRoutes();

        // création des piles de pioche et défausses des cartes Transport (wagon et
        // bateau)
        ArrayList<CarteTransport> cartesWagon = new ArrayList<>();
        ArrayList<CarteTransport> cartesBateau = new ArrayList<>();
        for (Couleur c : Couleur.values()) {
            if (c == Couleur.GRIS) {
                continue;
            }
            for (int i = 0; i < 4; i++) {
                // Cartes wagon simples avec une ancre
                cartesWagon.add(new CarteTransport(TypeCarteTransport.WAGON, c, false, true));
            }
            for (int i = 0; i < 7; i++) {
                // Cartes wagon simples sans ancre
                cartesWagon.add(new CarteTransport(TypeCarteTransport.WAGON, c, false, false));
            }
            for (int i = 0; i < 4; i++) {
                // Cartes bateau simples (toutes avec une ancre)
                cartesBateau.add(new CarteTransport(TypeCarteTransport.BATEAU, c, false, true));
            }
            for (int i = 0; i < 6; i++) {
                // Cartes bateau doubles (toutes sans ancre)
                cartesBateau.add(new CarteTransport(TypeCarteTransport.BATEAU, c, true, false));
            }
        }
        for (int i = 0; i < 14; i++) {
            // Cartes wagon joker
            cartesWagon.add(new CarteTransport(TypeCarteTransport.JOKER, Couleur.GRIS, false, true));
        }
        pilesDeCartesWagon = new PilesCartesTransport(cartesWagon);
        pilesDeCartesBateau = new PilesCartesTransport(cartesBateau);

        // création de la liste pile de cartes transport visibles
        // (les cartes seront retournées plus tard, au début de la partie dans run())
        cartesTransportVisibles = new ArrayList<>();

        // création des destinations
        pileDestinations = Destination.makeDestinationsMonde();
        Collections.shuffle(pileDestinations);

        // création des joueurs
        ArrayList<Joueur.CouleurJouer> couleurs = new ArrayList<>(Arrays.asList(Joueur.CouleurJouer.values()));
        Collections.shuffle(couleurs);
        joueurs = new ArrayList<>();
        for (String nomJoueur : nomJoueurs) {
            joueurs.add(new Joueur(nomJoueur, this, couleurs.remove(0)));
        }
        this.joueurCourant = joueurs.get(0);
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public List<Destination> getPileDestinations() {
        return pileDestinations;
    }

    public List<Ville> getPortsLibres() {
        return new ArrayList<>(portsLibres);
    }

    public List<Route> getRoutesLibres() {
        return new ArrayList<>(routesLibres);
    }

    public List<CarteTransport> getCartesTransportVisibles() {
        return new ArrayList<>(cartesTransportVisibles);
    }

    /**
     * Exécute la partie
     *
     * C'est cette méthode qui est appelée pour démarrer la partie. Elle doit intialiser le jeu
     * (retourner les cartes transport visibles, puis demander à chaque joueur de choisir ses destinations initiales
     * et le nombre de pions wagon qu'il souhaite prendre) puis exécuter les tours des joueurs en appelant la
     * méthode Joueur.jouerTour() jusqu'à ce que la condition de fin de partie soit réalisée.
     */


    public void run() {
        int tour = 0;
        boolean finPartie=false;

        //PREMIER TOUR
        distributionDebut();

        for(int i=0; i<joueurs.size(); i++){

            List<Destination> premieresDestinations = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                premieresDestinations.add(pileDestinations.remove(0));
            }
            joueurCourant.choisirDestinations(premieresDestinations,3);


            int wagon=this.joueurCourant.choisirNombre("Veuillez choisir le nombre de pions wagon que vous voulez, entre 10 et 25",10,25);

            this.joueurCourant.setNbPionsBateauEnReserve(this.joueurCourant.getNbPionsBateauEnReserve() - (60-wagon));
            this.joueurCourant.setNbPionsWagonEnReserve(this.joueurCourant.getNbPionsWagonEnReserve() - wagon);
            this.joueurCourant.setNbPionsBateau(60-wagon);
            this.joueurCourant.setNbPionsWagon(wagon);

            tour++;

            joueurCourant = joueurs.get(tour%joueurs.size());
        }

        //TOURS NORMAUX

        while (!finPartie) { // Tant que la partie est pas finie
            joueurCourant.jouerTour();
            tour++;
            if(joueurCourant.getNbPionsWagon()+joueurCourant.getNbPionsBateau() <= 6){
                finPartie = true;
            }
            joueurCourant = joueurs.get(tour%joueurs.size());
        }

        //DERNIERS TOURS

        for(int k=0; k<(joueurs.size()*2); k++){
            joueurCourant.jouerTour();
            tour++;
            joueurCourant = joueurs.get(tour%joueurs.size());
        }
        // FIN PARTIE
        prompt("Fin de la partie.", new ArrayList<>(), true);
    }
    public Destination piocherDestination() {
        // throw new RuntimeException("Méthode non implémentée !");
        Destination d = null;

        if(pileDestinations.isEmpty()){
            d = null;
        }else {
            d = pileDestinations.get(0);
            this.pileDestinations.remove(0);
        }

        return d;
    }

    public void distributionDebut(){
        for (int i = 0; i < 3; i++) {
            cartesTransportVisibles.add(piocherCarteWagon());
            cartesTransportVisibles.add(piocherCarteBateau());
        }
        for(int i=0; i<joueurs.size(); i++) {
            for (int j = 0; j < 2; j++) {
                this.joueurCourant.ajoutCarteTransport(piocherCarteWagon());
            }
            for (int j = 0; j < 6; j++) {
                this.joueurCourant.ajoutCarteTransport(piocherCarteBateau());
            }
        }

    }

    public void jouerTourPiocherDestination(){
        List<Destination> resultat = new ArrayList<Destination>();

        resultat.add(this.piocherDestination());
        resultat.add(this.piocherDestination());
        resultat.add(this.piocherDestination());
        resultat.add(this.piocherDestination());

        List<Destination> resultat2 = this.joueurCourant.choisirDestinations(resultat, 3);
        this.joueurCourant.ajouterDestinationDansJoueur(resultat2);
        for(Destination first : resultat){
            if(!resultat2.contains(first)){
                this.pileDestinations.add(first);
            }
        }
    }









    /**
     * Pioche une carte de la pile de pioche des cartes wagon.
     *
     * @return la carte qui a été piochée (ou null si aucune carte disponible)
     */
    public CarteTransport piocherCarteWagon() {
        return pilesDeCartesWagon.piocher();
    }

    public void piocherNbCarteBateau(int j) {
        for (int i = 0; i < j-1; i++) {
            this.piocherCarteWagon();
        }
    }

    public boolean piocheWagonEstVide() {
        return pilesDeCartesWagon.estVide();
    }


    public ArrayList<Destination> getRandomDestinationCard(int numberCardToGet){
        ArrayList<Destination> resultat = new ArrayList<>();

        for(int i=0; i<numberCardToGet; i++){
            Random rand = new Random();
            int randomNumber = rand.nextInt(this.pileDestinations.size());

            resultat.add(this.pileDestinations.get(randomNumber));
            this.pileDestinations.remove(randomNumber);
        }

        return resultat;
    }

    /**
     * Pioche une carte de la pile de pioche des cartes bateau.
     *
     * @return la carte qui a été piochée (ou null si aucune carte disponible)
     */
    public CarteTransport piocherCarteBateau() {
        return pilesDeCartesBateau.piocher();
    }

    public boolean piocheBateauEstVide() {
        return pilesDeCartesBateau.estVide();
    }

    /**
     * Ajoute un message au log du jeu
     */
    public void log(String message) {
        log.add(message);
    }

    /**
     * Ajoute un message à la file d'entrées
     */
    public void addInput(String message) {
        inputQueue.add(message);
    }




    /**
     * Lit une ligne de l'entrée standard
     * C'est cette méthode qui doit être appelée à chaque fois qu'on veut lire
     * l'entrée clavier de l'utilisateur (par exemple dans {@code Player.choisir})
     *
     * @return une chaîne de caractères correspondant à l'entrée suivante dans la
     * file
     */
    public String lireLigne() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Envoie l'état de la partie pour affichage aux joueurs avant de faire un choix
     *
     * @param instruction l'instruction qui est donnée au joueur
     * @param boutons     labels des choix proposés s'il y en a
     * @param peutPasser  indique si le joueur peut passer sans faire de choix
     */
    public void prompt(String instruction, Collection<Bouton> boutons, boolean peutPasser) {
        this.instruction = instruction;
        this.boutons = boutons;

        System.out.println();
        System.out.println(this);
        if (boutons.isEmpty()) {
            System.out.printf(">>> %s: %s <<<\n", joueurCourant.getNom(), instruction);
        } else {
            StringJoiner joiner = new StringJoiner(" / ");
            for (Bouton bouton : boutons) {
                joiner.add(bouton.toPrompt());
            }
            System.out.printf(">>> %s: %s [%s] <<<\n", joueurCourant.getNom(), instruction, joiner);
        }
        GameServer.setEtatJeu(new Gson().toJson(dataMap()));
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        for (Joueur j : joueurs) {
            joiner.add(j.toString());
        }
        return joiner.toString();
    }

    public Map<String, Object> dataMap() {
        return Map.ofEntries(
                Map.entry("joueurs", joueurs.stream().map(Joueur::dataMap).toList()),
                Map.entry("joueurCourant", joueurs.indexOf(joueurCourant)),
                Map.entry("piocheWagon", pilesDeCartesWagon.dataMap()),
                Map.entry("piocheBateau", pilesDeCartesBateau.dataMap()),
                Map.entry("cartesTransportVisibles", cartesTransportVisibles),
                Map.entry("nbDestinations", pileDestinations.size()),
                Map.entry("instruction", instruction),
                Map.entry("boutons", boutons),
                Map.entry("log", log));
    }
}
