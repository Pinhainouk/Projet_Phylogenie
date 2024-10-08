package reconstruction.arbre;

import java.util.ArrayList;

public class AlgoUpgma extends MethodeConstructionArbre{
	
	public AlgoUpgma(MatriceDeDistance m) {
	    super(m);
	}
	
	// Méthode qui exécute l'algo UPGMA, fusionne les clusters jusqu'à ce qu'il ne reste qu'un seul cluster (racine de l'arbre)
    public Noeud executerUpgma() {
        while (n > 1) {
            int[] clustersAFusionner = trouverClustersAFusionner(matrice);
            int cluster1 = clustersAFusionner[0];
            int cluster2 = clustersAFusionner[1];
            double distance = matrice.getDistance(cluster1, cluster2);
            calculerDistanceAuNoeud(distance);
            fusionnerClusters(cluster1, cluster2, distance); // n diminue ici après la fusion
            double[] nouvellesDistances = calculerNouvellesDistances(cluster1, cluster2); 
            majMatrice(cluster1, cluster2, nouvellesDistances);
        }
        return clusters.get(0); // Le noeud restant est la racine de l'arbre
    }
    
    // Méthode pour calculer la taille de la branche
    private double calculerDistanceAuNoeud(double dij) {
        return dij / 2;
    }
	
	// Méthode qui fusionne les clusters, crée un nouveau noeud pour le cluster met à jour la liste des clusters
	public Noeud fusionnerClusters(int cluster1, int cluster2, double distance) {
        Noeud clusterGauche = clusters.get(cluster1);
        Noeud clusterDroit = clusters.get(cluster2);
        Noeud nouveauCluster = new Noeud(clusterGauche.nom + "/" + clusterDroit.nom);
        // Ajouter les enfants et assigner les distances correctement
        double distanceGauche = distance / 2.0 - clusterGauche.getDistance();
        double distanceDroit = distance / 2.0 - clusterDroit.getDistance();
        nouveauCluster.ajouterEnfant(clusterGauche, distanceGauche);
        nouveauCluster.ajouterEnfant(clusterDroit, distanceDroit);
        // crée noeud enfant et la distance = taille de la branche // Ajouter les enfants et assigner les distances correctement
        //nouveauCluster.ajouterEnfant(clusterGauche);
        //nouveauCluster.ajouterEnfant(clusterGauche, clusterGauche.getDistance() + distance / 2.0);
        //nouveauCluster.ajouterEnfant(clusterDroit);
        //nouveauCluster.ajouterEnfant(clusterDroit, clusterDroit.getDistance() + distance / 2.0);
        nouveauCluster.setDistance(distance / 2.0); // Diviser par 2 pour la taille de la branche correcte
        // Mettre à jour la liste des clusters
        clusters.add(nouveauCluster);
        clusters.remove(cluster2); // Suppression dans l'ordre inverse pour éviter le décalage
        clusters.remove(cluster1);
        //clusters.remove(cluster1);
        //clusters.remove(cluster2-1); // attention : décalage dans l'arraylist       
        ArrayList<String> nouveauxNomsSequences = new ArrayList<>(matrice.getNomsSequences());
        nouveauxNomsSequences.add(nouveauCluster.nom);
        nouveauxNomsSequences.remove(cluster2);
        nouveauxNomsSequences.remove(cluster1);
        //nouveauxNomsSequences.remove(cluster1);
        //nouveauxNomsSequences.remove(cluster2-1); // attention : décalage dans l'arraylist
        for (int i = 0; i < nouveauxNomsSequences.size(); i++) {
   	        matrice.setNomsSequences(i, nouveauxNomsSequences.get(i));
        }  
        // Réduire le nombre de clusters
        n--;
        // Débogage
        System.out.println("Fusion des clusters " + clusterGauche.nom + " et " + clusterDroit.nom + " à une distance de " + distance/2);
        System.out.println("Nouveau cluster: " + nouveauCluster.nom);
        return nouveauCluster;
    }
	
	// Calcule la distance moyenne entre le nouveau cluster et les autres clusters
    private double[] calculerNouvellesDistances(int cluster1, int cluster2) {
        double[] nouvellesDistances = new double[matrice.getTailleMatrice()];
        int index = 0;
        for (int i = 0; i < matrice.getTailleMatrice(); i++) {
            if (i != cluster1 && i != cluster2) { // garantit que l'on n'inclut pas les distances des clusters fusionnés
                nouvellesDistances[index] = (matrice.getDistance(cluster1, i) + matrice.getDistance(cluster2, i)) / 2.0;
                index++;
           }
        }
        // Débogage
        System.out.println("Nouvelles distances après fusion des clusters " + cluster1 + " et " + cluster2 + ":");
        //System.out.println("Nouvelles distances après fusion des clusters " + matrice.getNomSequence(cluster1) + " et " + matrice.getNomSequence(cluster2) + ":"); à revoir
        for (int i = 0; i < nouvellesDistances.length; i++) {
            System.out.print(nouvellesDistances[i] + " ");
        }
        System.out.println();       
        return nouvellesDistances;
   }
	   
    // Méthode qui met à jour la matrice en supprimant lignes et colonnes des clusters fusionnés et ajoute une nouvelle ligne et colonne pour le nouveau cluster
 	public void majMatrice(int clusterA, int clusterB, double[] nouvellesDistances) {
 		matrice.diminuerTailleMatrice(clusterB);
 		matrice.diminuerTailleMatrice(clusterA);
 		matrice.ajouterLigneColonne(nouvellesDistances);
 		// Débogage
 		System.out.println("Matrice après mise à jour:");
 		matrice.afficherMatrice();
 	}
}