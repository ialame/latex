import java.util.Arrays;
public class Exercice6{ 
static final int plusInfini = Integer.MAX_VALUE;
/* Feuille 2, exercice 6 : trajet de coût minimum, il existe un trajet direct entre tout 
couple de villes (i, j), i < j.
Equation de récurrence des valeurs m(j), coût min. d'un trajet de 0 à j inclus.
base m(0) = 0, hérédité m(j) = min {m(i) + Dij}   sur tous les i tels que 0 ≤ i < j 
*/	
	static int[][] calculerMA(int[][] D){ int n = D.length;
	/* calcule et retourne MA = {M, A}. où A = arg M */
		int[] M = new int[n], A = new int[n];
		M[0] = 0; // base (la valeur A[0] est quelconque)
		// cas général m(j) = max {m(i) + Dij}   sur tous les i tels que 0 ≤ i < j 
		for (int j = 1; j < n; j++){ // pour j dans [1:n]
			// calcul de m(j) = min {m(i) + Dij} sur tous les i tels que 0 ≤ i < j
			M[j] = plusInfini;
			for (int i = 0; i < j; i++){ int mij = M[i]+D[i][j];
				/* mij est le coût du chemin 0 ------m(i)-----> i --Dij-> j 
				Si mij est inférieur aux coûts minimum calculés jusqu'ici : 
				mettre à jour M[j] et A[j] */
				if (mij < M[j]){
					M[j] = mij;
					A[j] = i; // A[j] est la dernière ville avant j sur le trajet de 
					// coût minimum de la ville 0 à la ville j incluses.
				}
			}
		}
		return new int[][] {M, A};
	} // Forme du terme dominant du temps de calcul : alpha x n**2 (n**2 est "n au carré")
	  // car corps de boucle en temps majoré par une constante, exécuté
	  // 1 + 2 + ... + n-1 fois = n x (n-1) / 2 fois
	
	static void afficherTrajectMinimum(int[] M, int[] A, int[][] D, int j){
	// Affiche un trajet de coût minimum de 0 à j inclus.
		if (j==0){ // le trajet ne contient que la ville 0. L'afficher.
			System.out.print(j);
			return; 
		}
		int aj = A[j]; // aj est la dernière ville avant j sur le trajet min de 0 à j.
		// Le trajet min de 0 à j est : trajet min de 0 à aj puis direct aj -> j 
			// 1) afficher le trajet min. de 0 à aj
			afficherTrajectMinimum(M, A, D, aj); // 0 -------m(aj)----->aj affiché
			// 2) afficher "--D[aj][j]--> j" /
			System.out.printf("--(%d)-->%d", D[aj][j], j);
			// 0 -------m(aj)------>aj->D[aj][j]->j affiché (trajet coût min de 0 à j)
	} // Pire cas : toutes les villes sont sur le trajet de coût minimum.
	  // Dans ce pire cas il y a n appels, chacun en temps majoré par une constante.
	  // Donc forme du temps de calcul dans le pire cas : alpha x n + beta
	
	public static void main(String[] args){		
		{	System.out.println("Exercice 6 : trajet de coût minimum");
			final int inf = plusInfini;
			int[][] D = { // les valeurs dij, i ≥ j, sont quelconques. Ici : infini.
				{inf, 40, 40, 90, 150}, // coûts des  trajets directs de 0 à 1,2,3,4
				{inf, inf, 50, 50, 100}, // de 1 à 2,3,4
				{inf, inf, inf, 40, 100}, // de 2 à 3,4
				{inf, inf, inf, inf, 50}, // de 3 à 4
				{inf, inf, inf, inf, inf} // de 4 à ... aucun autre sommet
			};
			System.out.println("tableau D des coûts directs :"); 
			afficherD(D); 
			int n = D.length;
			System.out.println("Nombre de villes : " + n);
			int[][] MA = calculerMA(D);
			int[] M = MA[0], A = MA[1];
			System.out.printf("M = %s\n", Arrays.toString(M));
			System.out.printf("trajet de coût minmum de la ville 0 à la ville %d :\n",n-1);
			afficherTrajectMinimum(M,A,D,n-1); System.out.println();
			System.out.printf("Coût de ce trajet : %d\n", M[n-1]);
			System.out.println();
		}
	}// end main()

	static void afficherD(int[][] D){int m = D.length, n = D[0].length;
		for (int i = m-1; i > -1; i--){ System.out.print(i + " : ");
			for (int j = 0; j < n; j++)
				if (D[i][j] == plusInfini) 
					System.out.print("inf"+" ");
				else 
					System.out.print(D[i][j] + " ");
			System.out.println();
		}
	}
} // end class

/* Compilation et exécution dans un terminal Unix

% javac Exercice6.java
% java Exercice6     
Exercice 6 : trajet de coût minimum
tableau D des coûts directs :     
4 : inf inf inf inf inf           
3 : inf inf inf inf 50 
2 : inf inf inf 40 100 
1 : inf inf 50 50 100 
0 : inf 40 40 90 150 
Nombre de villes : 5
M = [0, 40, 40, 80, 130]
trajet de coût minmum de la ville 0 à la ville 4 :
0--(40)-->2--(40)-->3--(50)-->4
Coût de ce trajet : 130
% 
*/

