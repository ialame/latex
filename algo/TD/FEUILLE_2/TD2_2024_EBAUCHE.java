import java.util.Arrays;
class TD2_2024{ 
static final int plusInfini = Integer.MAX_VALUE, moinsInfini = Integer.MIN_VALUE;

/* Exercice 4 : 2 sacs.
Equation de récurrence des valeurs m(k,c0,c1) :
...
*/
	static int[][][] calculerM(int[] V, int[] T, int C0, int C1){ int n = V.length;
		int[][][] M = new int[n+1][C0+1][C1+1];
		// base
		...
		// cas général
		for (int k = 1; k < n+1; k++)
			for (int c0 = 0; c0 < C0+1; c0++)
				for (int c1 = 0; c1 < C1+1; c1++)
					 ...
		return M;
	} // Forme du terme dominant du temps de calcul : alpha x (n x C0 x C1) 
	  // où alpha est une constante.
	  
	static void acsm(int[][][] M, int[] V, int[] T, int k, int c0, int c1){ // affiche le 
	// contenu des sacs de contenance c0 et c1 de valeur maximum, contenant un sous-ensemble  
	// des k premiers objets.
		if (k == 0) return;
		// le k-ème objet n'est dans aucun des sacs
		if (...) { 
			...
			return;
		}
		// // le k-ème objet est dans le sac S0
		if (...) {
			acsm(M,V,T,...);
			System.out.printf("Sac 0 : objet %d, valeur %d, taille %d\n",k-1,V[k-1],T[k-1]);
			return;
		}
		// le k-ème objet est dans le sac S1
		acsm(M,V,T,...);
		System.out.printf("Sac 1 : objet %d, valeur %d, taille %d\n",k-1,V[k-1],T[k-1]);
		return;
	}  // Forme du terme dominant du temps de calcul : alpha x n 
	  // car n appels récursifs, chacun en temps majoré par une constante.

/* Exercice 5 : trajet de coût minimum, 2 lignes de bus
Equation de récurrence des valeurs m(j) :
...
*/	
	static int[] calculerM(int[] D1, int[] D2){ int n = D1.length;
		int[] M = new int[n];
		M[0] = ...; M[1] = ...;
		for (int j = 2; j < n; j++)
			M[j] = min(..., ...);
		return M;
	} // Forme du terme dominant du temps de calcul : alpha x n
	  // car corps de boucle en temps majoré par une constante, exécuté n-2 fois
	
	static void afficherTrajectMinimum(int[] M, int[] D1, int[] D2, int j){
	// Affiche un trajet de coût minimum de 0 à j inclus.
		if (j==0){ // le trajet ne contient que la ville 0. L'afficher.
			System.out.print(j);
			return; 
		}
		// trajet min jusqu'en j-1 puis --(d1(j-1))--> j
		if (M[j] == ...) { 
			// 1) affichier le trajet min. de 0 à j-1
			afficherTrajectMinimum(M, D1, D2, j-1);
			// 2) afficher "--(d1(j-1))--> j"
			System.out.printf("--(%d)-->%d", D1[j-1], j);
		}
		else{ // trajet min jusqu'en j-2 puis --(d2(j-2))--> j
			// 1) afficher le trajet min. de 0 à j-1
			afficherTrajectMinimum(M, D1, D2, j-2);
			// 2) afficher "--(d2(j-2))--> j-2"
			System.out.printf("--(%d)-->%d", D2[j-2], j);
		}
	} // Forme du terme dominant du temps de calcul :  alpha x n
 	 // car n appels récursifs, chacun en temps constant.
	
/* Exercice 6 : trajet de coût minimum, version version 2, il existe un trajet direct
entre tout couple de villes (i, j), i < j
Equation de récurrence des valeurs m(j)
...
*/	
	static int[][] calculerMA(int[][] D){ int n = D.length;
	/* calcule et retourne MA = {M, A}. où A = arg M */
		int[] M = new int[n], A = new int[n];
		M[0] = 0; // base (la valeur A[0] est quelconque)
		for (int j = 1; j < n; j++){ // pour j dans [1:n-1]
			// calcul de m(j)
			M[j] = plusInfini;
			for (int i = 0; i < j; i++){ 
				La ville i est la dernière étape avant la ville j.
				Si le trajet 0 ------m(i)-----> i -d(i,j)-> j est de coût inférieur
				aux coûts minimum calculés jusqu'ici : mettre à jour M[j] et A[j]
				}
			}
		}
		return new int[][] {M, A};
	} // Forme du terme dominant du temps de calcul : alpha x n**2 (n**2 est "n au carré")
	  // car corps de boucle en temps majoré par une constante, exécuté
	  // 1 + 2 + ... + n fois = n x (n+1) / 2 fois
	
	static void afficherTrajectMinimum(int[] M, int[] A, int[][] D, int j){
	// Affiche un trajet de coût minimum de 0 à j inclus.
		if (j==0){ // le trajet ne contient que la ville 0. L'afficher.
			System.out.print(j);
			return; 
		}
		int aj = A[j]; // trajet min jusqu'en aj puis direct de aj à j 
			// 1) afficher le trajet min. de 0 à j-1
			afficherTrajectMinimum(M, A, D, aj);
			// 2) afficher "--(d1(j-1))--> j"
			System.out.printf("--(%d)-->%d", D[aj][j], j);
	} // Forme du terme dominant du temps de calcul : alpha n
	 //  car n appels récursifs, chacun en temps majoré par une constante
	
	public static void main(String[] args){
			
		{	System.out.println("Exercice 1 : deux sacs");
			int[] T = {20, 20, 70, 10, 10, 40, 10, 80, 10, 40, 45}, // tailles des objets
				  V = {25, 25, 65, 15, 5, 35, 15, 75, 15, 45, 50}; // valeurs des objets
			int C0 = 25, C1 = 75  ; // contenance des sacs
			System.out.println("Tailles des objets: " + Arrays.toString(T));
			System.out.println("Valeurs des objets  : " + Arrays.toString(V));
			System.out.printf("Contenances des sacs : C0 = %d, C1 = %d\n", C0,C1);
			int[][][] M = calculerM(V,T,C0,C1);
			int n = T.length; // nombre de spots
			System.out.printf("gain maximum des sacs : %d\n", M[n][C0][C1]);
			System.out.println("sous-ensemble d'objets de gain toal maximum :");
			acsm(M,V,T,n,C0,C1);
			System.out.println(); System.out.println();
		}		

		{	System.out.println("Exercice 2 : trajet de coût minimum");
			// D1 : 0--(10)-->1, 1--(20)-->2, 2--(100)-->3, 3--(30)-->4 
			int[] D1 = {10,20,100,30,-1}, 
			// D2 : 0--(40)-->2, 1--(50)-->3, 2--(60)-->4
				  D2 = {40,50,60,-1,-1};
			System.out.printf("D1 = %s\n", Arrays.toString(D1));
			System.out.printf("D2 = %s\n", Arrays.toString(D2));
			int[] M = calculerM(D1,D2);
			System.out.printf("M = %s\n", Arrays.toString(M));
			int n = D1.length;
			System.out.printf("trajet de coût minmum de la ville 0 à la ville %d :\n",n-1);
			afficherTrajectMinimum(M,D1,D2,n-1); System.out.println();
			System.out.printf("Coût de ce trajet : %d\n", M[n-1]);
			System.out.println();
		} 
		
		{	System.out.println("Exercice 3 : trajet de coût minimum");
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
					System.out.print("inf"+" " );
				else 
					System.out.print(D[i][j] + " ");
			System.out.println();
		}
	}
	static int max(int x, int y){ if (x >= y) return x; return y;}
	static int max(int x, int y, int z){ if (x >= max(y,z)) return x; 
		if (y >= z) return y; 
		return z;
	}	
	static int min(int x, int y){ if (x <= y) return x; return y;}
	static int min(int x, int y, int z){ if (x <= min(y,z)) return x; 
		if (y <= z) return y; 
		return z;
	}
} // end class

/* Compilation et exécution dans un terminal Unix

% javac TD2_2024.java
% java TD2_2024      
Exercice 1 : deux sacs
Tailles des objets: [20, 20, 70, 10, 10, 40, 10, 80, 10, 40, 45]
Valeurs des objets  : [25, 25, 65, 15, 5, 35, 15, 75, 15, 45, 50]
Contenances des sacs : C0 = 25, C1 = 75
gain maximum des sacs : 120
sous-ensemble d'objets de gain toal maximum :
Sac 1 : objet 0, valeur 25, taille 20
Sac 1 : objet 3, valeur 15, taille 10
Sac 0 : objet 6, valeur 15, taille 10
Sac 0 : objet 8, valeur 15, taille 10
Sac 1 : objet 10, valeur 50, taille 45


Exercice 2 : trajet de coût minimum
D1 = [10, 20, 100, 30, -1]
D2 = [40, 50, 60, -1, -1]
M = [0, 10, 30, 60, 90]
trajet de coût minmum de la ville 0 à la ville 4 :
0--(10)-->1--(50)-->3--(30)-->4
Coût de ce trajet : 90

Exercice 3 : trajet de coût minimum
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

