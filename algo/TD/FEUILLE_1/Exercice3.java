// TD1 ex. 3. Multi-sous-ensemble de X, de somme S. 21/09/2023 -- rene.natowicz@esiee.fr
// Voir exemple d'exécution à la fin de ce texte.
import java.util.Arrays;
public class Exercice3{ // TD1, ex. 3 : sous-ensemble de somme S avec possibles répétitions
	public static void main(String[] args){ int nargs = args.length;
	// Entrée du programme : les éléments de X suivis de la somme S
		if (nargs == 0){ System.out.println("Usage : x_{0} ... x_{n-1} S");
			return;
		}
		int S = Integer.parseInt(args[nargs-1]);
		int n = nargs - 1; // nombre d'éléments de X
		int[] X = new int[n];
		for (int i = 0; i < n; i++) X[i] = Integer.parseInt(args[i]);
		System.out.printf("\nX = %s, S = %d\n", Arrays.toString(X), S);
		
		// Calcul de E, de terme général e(k,s), selon l'équation de récurrence 1.
		boolean[][] E1a = calculerE_version_1_a(X, S); 
		System.out.println("\nE1a :"); afficher(E1a); 
		System.out.println();
		
		// Calcul de E, de terme général e(k,s) selon l'équation de récurrence 1.
		// et A  de terme général a(k,s) = arg e(k,s) = nombre de répétitions du 
		// k-ème élément dans le multi-sous-ensemble de X[0:k] de somme s.
		EA ea = calculerEA_version_1b(X, S); 
		boolean[][] E1b = EA.E;
		System.out.println("E1b :"); afficher(E1b);
		if (!E1b[n][S]){
			System.out.printf("Il n'y a pas de multi-sous-ensemble de %s de somme %s\n\n",
				Arrays.toString(X), S);
		} 
		else { 
			System.out.printf(
					"Multi-sous-ensemble de %s, de somme %d :\n",
											Arrays.toString(X), S
			);
			int[][] A = ea.A; // A = arg E
			// afficher un multi-sous-ensemble de X de somme S
			asesspr(A, X, n, S); 
			System.out.println();	
		}		
		
		// Calcul de E selon l'équation de récurrence 2.
		boolean[][] E2 = calculerE_version_2(X, S); // calcul selon éq. de réc. 2
		System.out.println("E2 :"); afficher(E2);
		if (!E2[n][S]){
			System.out.printf("Il n'y a pas de multi-sous-ensemble de %s de somme %s\n\n",
				Arrays.toString(X), S);
		} 
		else {
			System.out.printf(
					"Multi-sous-ensemble de %s, de somme %d :\n",
											Arrays.toString(X), S
			);
			asesspr(E2, X, n, S); 	
			System.out.println();
		}
	}
	
	static boolean[][] calculerE_version_1_a(int[] X, int S){ int n = X.length;
	/* Calcul de E[0:n+1][0:S+1] de terme général E[k][s] = e(k,s) selon la première
	forme de la récurrence.
	— base k = 0 : 
		e(0,0) = vrai 
		qqsoit s, 1 ≤ s < S+1, e(k,s) = faux.
	— cas général : qqsoit k, 1 ≤ k < n+1, qqsoit s, 0 ≤ s < S+1
     e(k,s) = e(k-1,s) ou e(k-1,s-x_{k-1}) ou e(k-1,s-2.x_{k-1}) ou e(k-1,s-3.x_{k-1}) ...    
     (le k-ème élément n’est pas pris ou pris une fois ou deux fois ou trois fois...)
     autrement dit :
     e(k,s) = OU e( k-1, s - r.x_{k-1} ) sur tous les r ≥ 0 tels que s - r.x_{k-1} ≥ 0.
	*/
		boolean[][] E = new boolean[n+1][S+1];
		// base
		E[0][0] = true; 
		for (int s = 1; s < S+1; s++) E[0][s] = false; // inutile en Java
		// cas général
		for (int k = 1; k < n+1; k++)
			for (int s = 0; s < S+1; s++){
				E[k][s] = E[k-1][s]; // le k-ème élément n'est pas dans le sous-ensemble
				for (int r = 1; s - r * X[k-1] >= 0; r++) // le k-ème y est r fois
					E[k][s] = E[k][s] || E[k-1][s - r * X[k-1]];
			}
		return E;
	}
	
	/* 0n peut remarquer qu'il n'est pas utile de répéter pour tous les r
	car E[k][s] = vrai dès le premier r tel que E[k-1][s - r * X[k-1]] = vrai.
	On obtient la version 1b ci-dessous. L'équation de récurrence est la même.
	La programmation est différente.
	*/
	static boolean[][] calculerE_version_1_b(int[] X, int S){ int n = X.length;
	/* Calcul de E[0:n+1][0:S+1] de terme général E[k][s] = e(k,s) selon la première
	forme de la récurrence.
	— base k = 0 : 
		e(0,0) = vrai 
		qqsoit s, 1 ≤ s < S+1, e(k,s) = faux.
	— cas général : qqsoit k, 1 ≤ k < n+1, qqsoit s, 0 ≤ s < S+1
     e(k,s) = e(k-1,s) ou e(k-1,s-x_{k-1}) ou e(k-1,s-2.x_{k-1}) ou e(k-1,s-3.x_{k-1}) ...    
     (le k-ème élément n’est pas pris ou pris une fois ou deux fois ou trois fois...)
     autrement dit :
     e(k,s) = OU e( k-1, s - r.x_{k-1} ) sur tous les r ≥ 0 tels que s - r.x_{k-1} ≥ 0.
	*/
		boolean[][] E = new boolean[n+1][S+1];
		// base
		E[0][0] = true; 
		for (int s = 1; s < S+1; s++) E[0][s] = false; // inutile en Java
		// cas général
		for (int k = 1; k < n+1; k++)
			for (int s = 0; s < S+1; s++){
				E[k][s] = false; 
				int r = 0; 
				while (s - r * X[k-1] >= 0 && !E[k][s]){
					E[k][s] = E[k-1][s - r * X[k-1]];
					r++;
				} // sortie de la boucle while avec E[k][s] = vrai et r = le plus petit
				// facteur de répétition pour lequel E[k][s] est vrai
				// ou 
				// E[k][s] = faux si aucun facteur de répétition r ≥ 0 ne permet d'avoir 
				// E[k][s] = vrai.
			}
		return E;
	}
	
	static EA calculerEA_version_1b(int[] X, int S){ int n = X.length;
	/* Cette fonction calcule le tableau de booléens E de la même façon que la fonction 
	précédente (fonction calculerE_version_1_b(...)).
	Elle calcule aussi les arguments des valeurs e(k,s) dans un tableau d'entiers A.
	La valeur arg e(k,s) est le nombre de répétitions du k-ème élément dans le 
	multi-sous-ensemble de X[0:k] de somme s. 
	Elle retourne les deux tableaux E (booléens) et A (entiers) dans un objet de la 
	class EA. Cette classe n'a pas d'autre intérêt que de permettre de retourner
	deux tableaux de types différents. */
	/* Calcul du tableau de booléens E[0:n+1][0:S+1] de terme général E[k][s] = e(k,s) 
	selon la première forme de la récurrence, version 1b,
	et calcul du tableau d'entiers A[0:n+1][0:S+1] de terme général 
	A[k][s] = arg e(k,s) = nombre de répétitions du k-ème élément pour obtenir la somme s 
	avec des éléments de X[0:k]. 
	Rappel : X[0:k] est le sous-ensemble des k premiers éléments de X.
	Remarque : quand e(k,s) = faux, nous posons par définition arg e(k,s) = -1.
	— base k = 0 : 
		e(0,0) = vrai 
		qqsoit s, 1 ≤ s < S+1, e(k,s) = faux.
	— cas général : qqsoit k, 1 ≤ k < n+1, qqsoit s, 0 ≤ s < S+1
     e(k,s) = e(k-1,s) ou e(k, s - x_{k-1}) 
	*/
		boolean[][] E = new boolean[n+1][S+1];
		int[][] A = new int[n+1][S+1];
		// base
		E[0][0] = true; A[0][0] = 0;
		for (int s = 1; s < S+1; s++) {E[0][s] = false; A[0][s] = -1;}
		// cas général
		for (int k = 1; k < n+1; k++)
			for (int s = 0; s < S+1; s++){
				E[k][s] = false; A[k][s] = -1;
				int r = 0; 
				while (s - r * X[k-1] >= 0 && !E[k][s]){
					E[k][s] = E[k-1][s - r * X[k-1]];
					if (E[k][s])
						A[k][s] = r;
					else 
						r++;
				}
			}
		return new EA(E,A);
	}
		
	static boolean[][] calculerE_version_2(int[] X, int S){ int n = X.length;
	/* Calcul de E[0:n+1][0:S+1] de terme général E[k][s] = e(k,s) selon la seconde
	forme de la récurrence.
	— base k = 0 : 
		e(0,0) = vrai 
		qqsoit s, 1 ≤ s < S+1, e(k,s) = faux.
	— cas général : qqsoit k, 1 ≤ k < n+1, qqsoit s, 0 ≤ s < S+1
     e(k,s) = e(k-1,s) ou e(k, s - x_{k-1}) 
	*/
		boolean[][] E = new boolean[n+1][S+1];
		// base
		E[0][0] = true; 
		for (int s = 1; s < S+1; s++) E[0][s] = false; // inutile en Java
		// cas général
		for (int k = 1; k < n+1; k++)
			for (int s = 0; s < S+1; s++){
				if (s - X[k-1] >= 0)
					E[k][s] = E[k-1][s] || E[k][s-X[k-1]];
				else 
					E[k][s] = E[k-1][s] ;
			}
		return E;
	}
	
	static class EA{ static boolean[][] E; static int[][] A; 
	/* un objet de la classe EA contient un tableau de booléens et un tableau d'entiers */
		EA(boolean[][] E, int[][] A){this.E = E; this.A = A;}
	}

	static void afficher(boolean[][] T){int m = T.length, n = T[0].length;
		for (int k = m-1; k > -1; k--){ System.out.printf("k = %d : ", k);
			for (int c = 0; c < n; c++)
				if (T[k][c]) System.out.print("t ");
				else System.out.print("f ");
			System.out.println();
		}
	}
	static void asesspr(int[][] A, int[] X, int k, int s){
	/* Affiche un sous-ensemble de X[0:k] de somme s, avec possibles répétitions.
	Appel principal : asesspr(A, X, n, S).
	Soit S(k,s) un tel sous-ensemble.
	1) si k = 0 et s = 0, S(k,s) est vide. Sans rien faire il est affiché.
	2) si k ≥ 0 et le k-ème élément est répété r fois dans la somme s, 
	   alors S(k, s) = S(k - 1, s - r * x_{k-1}) U {r * x_{k-1}}
	   Donc, si S(k - 1, s - r * x_{k-1}) a été affiché et {r * x_{k-1}} a été imprimé
	         alors S(k, s) a été affiché.
	*/	
		if (k == 0 && s == 0) return; // S(k,s) est vide. Il a été affiché
		// k ≥ 1
		int r = A[k][s]; // le k-ème élément est répété r fois dans le multi-sous-ensemble
		// de X[0:k] de somme s. S(k, s) = S(k - 1, s - r * x_{k-1}) U {r * x_{k-1}}.
		asesspr(A, X, k-1, s - r * X[k-1]); // S(k - 1, s - r * x_{k-1}) a été affiché
		System.out.printf(
			"%d * X[%d] = %d * %d = %d\n", 
			 r,     k-1,  r,   X[k-1],  r*X[k-1]
		); // {r * x_{k-1}} a été affiché
		// donc  S(k - 1, s - r * x_{k-1}) U {r * x_{k-1}} a été affiché
		// donc S(k, s) a été affiché.
	}
	
	static void asesspr(boolean[][] E, int[] X, int k, int s){
	/* Affiche un multi-sous-ensemble de X[0:k] de somme s, selon l'équation de récurrence
	forme 2 :       e(k,s) = e(k-1,s) ou e(k, s-x_{k-1})
	Appel principal : asesspr(E, X, n, S).
	Soit S(k,s) un tel sous-ensemble.
	1) si k = 0 et s = 0, S(k,s) est vide. Sans rien faire il est affiché.
	2) si k ≥ 1 et e(k-1,s) : S(k,s) = S(k-1,s)
	3) si k ≥ 1 et !e(k-1,s) : S(k,x) = S(k-1,s-x_{k-1}) U {x_{k-1}}
	*/	
		if (k==0 && s==0) return; // S(k,s) = Ø. Sans rien faire, S(k,s) a été affiché.
		// k ≥ 1
		if (E[k-1][s]) // S(k,s) = S(k-1,s)
			asesspr(E,X,k-1,s);  // S(k-1,s) a été affiché donc S(k,s) a été affiché
		else {// S(k,s) = S(k,s-x_{k-1}) U {x_{k-1}}
			asesspr(E,X,k,s-X[k-1]); // S(k-1,s-x_{k-1}) a été affiché
			System.out.printf("X[%d] = %d\n", k-1, X[k-1]); // { x_{k-1} } a été imprimé
			// S(k-1,s-x_{k-1}) affiché, { x_{k-1} } imprimé, donc S(k,s) a été affiché
		}
	}
}
/* Compilation et exécution dans une fenêtre Terminal Unix.
% javac Exercice3.java     
% java Exercice3 2 3 1   21

X = [2, 3, 1], S = 21

E1a :
k = 3 : t t t t t t t t t t t t t t t t t t t t t t 
k = 2 : t f t t t t t t t t t t t t t t t t t t t t 
k = 1 : t f t f t f t f t f t f t f t f t f t f t f 
k = 0 : t f f f f f f f f f f f f f f f f f f f f f 

E1b :
k = 3 : t t t t t t t t t t t t t t t t t t t t t t 
k = 2 : t f t t t t t t t t t t t t t t t t t t t t 
k = 1 : t f t f t f t f t f t f t f t f t f t f t f 
k = 0 : t f f f f f f f f f f f f f f f f f f f f f 
Multi-sous-ensemble de [2, 3, 1], de somme 21 :
9 * X[0] = 9 * 2 = 18
1 * X[1] = 1 * 3 = 3
0 * X[2] = 0 * 1 = 0

E2 :
k = 3 : t t t t t t t t t t t t t t t t t t t t t t 
k = 2 : t f t t t t t t t t t t t t t t t t t t t t 
k = 1 : t f t f t f t f t f t f t f t f t f t f t f 
k = 0 : t f f f f f f f f f f f f f f f f f f f f f 
Multi-sous-ensemble de [2, 3, 1], de somme 21 :
X[0] = 2
X[0] = 2
X[0] = 2
X[0] = 2
X[0] = 2
X[0] = 2
X[0] = 2
X[0] = 2
X[0] = 2
X[1] = 3

% java Exercice3 2 4 6   13

X = [2, 4, 6], S = 13

E1a :
k = 3 : t f t f t f t f t f t f t f 
k = 2 : t f t f t f t f t f t f t f 
k = 1 : t f t f t f t f t f t f t f 
k = 0 : t f f f f f f f f f f f f f 

E1b :
k = 3 : t f t f t f t f t f t f t f 
k = 2 : t f t f t f t f t f t f t f 
k = 1 : t f t f t f t f t f t f t f 
k = 0 : t f f f f f f f f f f f f f 
Il n'y a pas de multi-sous-ensemble de [2, 4, 6] de somme 13

E2 :
k = 3 : t f t f t f t f t f t f t f 
k = 2 : t f t f t f t f t f t f t f 
k = 1 : t f t f t f t f t f t f t f 
k = 0 : t f f f f f f f f f f f f f 
Il n'y a pas de multi-sous-ensemble de [2, 4, 6] de somme 13

% 
*/