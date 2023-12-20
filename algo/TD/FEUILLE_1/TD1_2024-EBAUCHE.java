import java.util.Arrays;
public class TD1_2024{ 
// feuille de TD1 -- Septembre 2023 - Ebauche de programme pour les trois exercices
static final int plusInfini = Integer.MAX_VALUE, moinsInfini = Integer.MIN_VALUE;

/* Exercice 1 : spots et slots */
	static int[][] calculerM(int[] G, int[] D, int T){int K = G.length; 
		int[][] M = new int[K+1][T+1];
		...		
		return M;
	} 
	static void asegtm(int[][] M, int[] G, int[] D, int k, int t){
	/* Affiche  sous-ensemble des k premiers spots de gain total maximum
	pour le slot de durée t. Appel principal : asegtm(M,G,D,K,T). */
	}

/* Exercice 2 : sous-ensemble de X, de somme S */
	static boolean[][] calculerE(int[] X, int S){ int n = X.length;
		boolean E[][] = new boolean[n+1][S+1]; // de terme général E[k][s] = e(k,s)
		...
		return E;
	}
	static void ase(boolean[][] E, int[] X, int k, int s){
	/* Affiche un sous-ensemble de X[0:k] de somme s */
		...
	}
/* Exercice 3 : sous-ensemble de X, de somme S, répétitions autorisées */
	static boolean[][] calculerEar(int[] X, int S){ int n = X.length; // ar : avec répétitions
		boolean E[][] = new boolean[n+1][S+1]; // de terme général E[k][s] = e(k,s)
		...		
		return E;
	}
	static void asear(boolean[][] E, int[] X, int k, int s){
	/* Affiche un sous-ensemble de X[0:k] de somme s, avec répétitions autorisées */
		...
	}		

	public static void main(String[] args){
		{	System.out.println("Exercice 1 : spots & slots");
			int[] D = {20, 20, 70, 10, 10, 40, 10, 80, 10, 40, 45}, // durées des spots
				  G = {25, 25, 65, 15, 5, 35, 15, 75, 15, 45, 50}; // gains des spots
			int T = 100 ; // durée du slot
			System.out.println("durées des spots : " + Arrays.toString(D));
			System.out.println("gains des spots  : " + Arrays.toString(G));
			System.out.printf("durée du slot : %d\n", T);
			int[][] M = calculerM(G,D,T);
			int n = D.length; // nombre de spots
			System.out.printf("gain maximum du slot : %d\n", M[n][T]);
			System.out.println("sous-ensemble de spots de gain maximum :");
			asegtm(M,G,D,n,T);
			System.out.println(); System.out.println();
		}		
		
		{	System.out.println("Exercice 2 : sous-ensemble de somme S");
			int[] X = new int[] {6,2,4};
			int n = X.length;
			System.out.println("X = " + Arrays.toString(X)); 
			int sommeX = somme(X);
			for (int S = 0; S < sommeX + 2; S++){
				boolean[][] E = calculerE(X,S);
				if (E[n][S]) {System.out.printf("sous-ensemble de somme %d : ", S);
					ase(E,X,n,S); System.out.println();
				} 
				else System.out.printf("--> il n'y a pas de sous-ensemble de somme %d\n", S);
			}
			System.out.println(); System.out.println();
		}
		
		{	System.out.println("Exercice 3 : sous-ensemble de somme S avec répétitions");
			int[] X = new int[] {6,2,4};
			System.out.println("X = " + Arrays.toString(X));
			int sommeX = somme(X), n = X.length;
			for (int S = 0; S < sommeX + 5; S++){
				boolean[][] E = calculerEar(X,S); // ar : avec répétition
				if (E[n][S]) {System.out.printf("Sous-ensemble de somme %d : ", S);
					asear(E,X,n,S); System.out.println(); // ar : idem.
				} 
				else System.out.printf("--> Il n'y a pas de sous-ensemble de somme %d\n", S);
			}
			System.out.println();System.out.println();
		}
	}
	static void afficher(int[][] T){int n = T.length;
	/* Affiche le tableau T. */
		for (int i = n-1; i > -1; i--)
			System.out.println(Arrays.toString(T[i]));
	}
	static int somme(int[] T){ int n = T.length;
	/* Retourne la somme des valeurs de T */
		int s = 0; 
		for (int i = 0; i < n; i++) s = s + T[i];
		return s;
	}
	static int max(int x, int y){ if (x >= y) return x; return y;}		
} // end classe

/* Compilation et exécution dans une fenêtre terminal Unix

% javac TD1_2024.java
% java TD1_2024      
Exercice 1 : spots & slots
durées des spots : [20, 20, 70, 10, 10, 40, 10, 80, 10, 40, 45]
gains des spots  : [25, 25, 65, 15, 5, 35, 15, 75, 15, 45, 50]
durée du slot : 100
gain maximum du slot : 125
sous-ensemble de spots de gain maximum :
Spot 0 : durée : 20, gain : 25
Spot 1 : durée : 20, gain : 25
Spot 3 : durée : 10, gain : 15
Spot 6 : durée : 10, gain : 15
Spot 9 : durée : 40, gain : 45


Exercice 2 : sous-ensemble de somme S
X = [6, 2, 4]
sous-ensemble de somme 0 : 
--> il n'y a pas de sous-ensemble de somme 1
sous-ensemble de somme 2 : X[1]=2 
--> il n'y a pas de sous-ensemble de somme 3
sous-ensemble de somme 4 : X[2]=4 
--> il n'y a pas de sous-ensemble de somme 5
sous-ensemble de somme 6 : X[0]=6 
--> il n'y a pas de sous-ensemble de somme 7
sous-ensemble de somme 8 : X[0]=6 X[1]=2 
--> il n'y a pas de sous-ensemble de somme 9
sous-ensemble de somme 10 : X[0]=6 X[2]=4 
--> il n'y a pas de sous-ensemble de somme 11
sous-ensemble de somme 12 : X[0]=6 X[1]=2 X[2]=4 
--> il n'y a pas de sous-ensemble de somme 13


Exercice 3 : sous-ensemble de somme S avec répétitions
X = [6, 2, 4]
Sous-ensemble de somme 0 : 
--> Il n'y a pas de sous-ensemble de somme 1
Sous-ensemble de somme 2 : X[1]=2 
--> Il n'y a pas de sous-ensemble de somme 3
Sous-ensemble de somme 4 : X[1]=2 X[1]=2 
--> Il n'y a pas de sous-ensemble de somme 5
Sous-ensemble de somme 6 : X[0]=6 
--> Il n'y a pas de sous-ensemble de somme 7
Sous-ensemble de somme 8 : X[0]=6 X[1]=2 
--> Il n'y a pas de sous-ensemble de somme 9
Sous-ensemble de somme 10 : X[0]=6 X[1]=2 X[1]=2 
--> Il n'y a pas de sous-ensemble de somme 11
Sous-ensemble de somme 12 : X[0]=6 X[0]=6 
--> Il n'y a pas de sous-ensemble de somme 13
Sous-ensemble de somme 14 : X[0]=6 X[0]=6 X[1]=2 
--> Il n'y a pas de sous-ensemble de somme 15
Sous-ensemble de somme 16 : X[0]=6 X[0]=6 X[1]=2 X[1]=2 

%
*/

