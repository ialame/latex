/* Plus longue sous-séquences croissante (plssc).
Il s'agit de sous-séquences strictement croissantes.

S[0:n] un tableau d'entiers représente la séquence. Calculer une plssc de S.
Exemple : S = [10, 1, 20, 2, 30, 1, 2, 3]
Les plssc finissent en j = 4 et en j = 7.
On en donne une.

Longueur des plus longues séquences croissantes : 3
Une PLSSC de finit en S[4]=30 : S[1]=1 S[3]=2 S[4]=30 

• Remarque 1 : il est facile de donner une plssc finissant en j = 4 et une en j = 7. Dans 
ce programme, on n'en donnera qu'une.
• Remarque 2 : donner toutes les plssc finissant en un j donné fait appel à des algorithmes 
de "retour sur ses pas" alias algorithmes de "backtracking" que nous n'aurons pas le temps 
d'étudier dans l'unité d'algorithmique de deuxième année. Ils sont vus dans une unité 
d'algorithmique de la filière informatique.

Pour résoudre ce problème, << une plus longue sous-séquence croissante d'une séquence S >> 
on construit un graphe G(S) : dont les sommets sont les indices de la séquence S. 
Dans ce graphe il existe un arc i --> j ssi i < j et S[i] = S[j]. Avec cette représentation, 
une plssc de S est un plus long chemin dans G.

Sur l'exemple : les sommets sont [0:n=8] = {0, 1, ..., 7}. Le sommet 0 envoie un arc vers
les sommets 2 et 4. Le sommet 1 envoie un arc vers les sommets 3, 6 et 7. Les sommets 30
et 3 n'envoient d'arc vers aucun sommet.   

Equation de récurrence des valeurs l(j), 0 ≤ j < n.
------------------------------------------------------------------------------------
[...]]
L'équation de récurrence fait référence aux sommets prédécesseurs du sommet j.
Nous devrons donc construire le graphe G' symétrique du graphe G et calculer
les valeurs l(j) dans le graphe G'.
*/

import java.util.Arrays;
public class PLSSC{

	public static void main(String[] args){ 
		int[] S = toIntTab(args);
		System.out.printf("S = %s\n", Arrays.toString(S));
		Liste[] G = graphe(S);
		System.out.println("G ="); afficher(G, S);
		Liste[] Gprime = symetrique(G);
		System.out.println("G' ="); 
		afficher(Gprime, S);
		int[][] LA = calculerLA(Gprime);
		int[] L = LA[0], A = LA[1];
		System.out.printf("S = %s\n", Arrays.toString(S));
		System.out.printf("L = %s\n", Arrays.toString(L));
		System.out.printf("A = %s\n", Arrays.toString(A));
		System.out.printf("Une plssc de S = %s : %s\n", Arrays.toString(S), plssc(LA, S));
	}

	static void afficher(Liste[] G, int[] S){int n = G.length;
	// affiche le graphe G (table de liste d'arcs). Fonction vue en cours.
		for (int i = 0; i < n; i++){
			System.out.printf("S[%d]=%d : ", i, S[i]);
			for (Liste l = G[i]; !vide(l); l = reste(l)){
				System.out.printf("S[%d]=%d ", premier(l), S[premier(l)]);
			}
			System.out.println();
		}
	}
	static Liste[] graphe(int[] S){ int n = S.length;
	// construit le graphe G
		Liste[] G = new Liste[n];
		for (int i = 0; i < n; i++) G[i] = null;
		for (int j = 1; j < n; j++) 
			for (int i = 0; i < j; i++)
				[...]
		return G;
	}
	
	// La classe Liste et les fonctions vide, premier et reste ont été vues en cours.
	static class Liste{ int p; 
		Liste r;
		Liste(int p, Liste r){
			this.p = p; 
			this.r = r;
		}
	}
	static boolean vide(Liste l){ return l == null; }
	static int premier(Liste l){ return l.p; }
	static Liste reste(Liste l){ return l.r; }
	
	static Liste[] symetrique(Liste[] G){ int n = G.length;
	// retourne le graphe symétrique du graphe G. Vue en cours.
		Liste[] Gprime = new Liste[n];
		[...]
		return Gprime;
	}
	static int[][] calculerLA(Liste[] Gprime){ int n = Gprime.length;
	// retourne L[0:n] de terme général l(j) et A[0:n] de terme général a(j) = arg l(j)
		int[] L = new int[n], A = new int[n];
		// L[0:n] : l(j) = longueur du plus long chemin de 0 à j
		// A[0:n] : a(j) = arg l(j)
		[...]
		return new int[][] {L, A};
	}

	static String plssc(int[] A, int[] S, int j){
	// retourne une plus longue séquence croissante de 0 à j inclus
		if (A[j]==-1) return "S["+j+"]" + "=" + S[j] + " ";  // << S[j]=s_j" " >>
		return [...];
	}

	static String plssc(int[][] LA, int[] S){ int n = S.length;
	// retourne une plssc de S. Sur l'exemple de l'énoncé, la chaîne retournée est :
	// "S[2]=1 S[3]=2 S[4]=3 S[5]=4 S[6]=5 S[9]=6 S[10]=8" 
	// Voir les exemples en fin de fichier.
		if (n == 0) return ""; // S est vide. Une plssc de S est la séquence vide.
		/* ici : S  est non vide.
		Soit jstar = argmax(L), indice du premier maximum de L. Une plssc finit en jstar.
		1) calculer jstar = argmax(L)
		2) retourner une plssc finissant en jstar */
		[...]
	}
	
	static int argmaxWhile(int[] L){ int n = L.length; 
		if (n == 0) return -1; // on pose : arg max L[0:0] = -1.
	/* Invariant I(jstar, j): jstar = argmax(L[0:j]
	Initialisation : jstar = -1, j = 0; 
	Progression : I(jstar, j) et j+1 != n et ...
			... L[j+1] > L[jstar] ==> I(j+1, j+1)
			... L[j+1] <= L[jstar] ==> I(jstar, j+1)
 
 	*/
		int j = 1, jstar = 0; // I(jstar, j)
		while (j+1 != n-1) // I(jstar, j) et j+1 != n-1
			if (L[j+1] > L[jstar]){ // I(j+1, j+1)
				jstar = j+1; // I(jstar,j+1)
				j = j+1; // I(jstar,j)			
			}
			else // I(jstar, j+1)
				j = j+1; // I(jstar,j)
		return jstar;
	}
	/* Le calcul de argmax(L) étant trivial, une boucle "for" aurait fait l'affaire : */
	static int argmaxFor(int[] L){ int n = L.length;
		int jstar = 0;
		for (int j = 2 ; j < n; j++)
			if (L[jstar] < L[j]) 
				jstar = j;
		return jstar;
	}	
	
		
	static int[] toIntTab(String[] args){ int n = args.length;
	// retourne les données de la ligne de commande dans un tableau d'entiers.
		int[] T = new int[n];
		for (int i = 0; i < n; i++)
			T[i] = Integer.parseInt(args[i]);
		return T;
	}
}
/*
% javac PLSSC.java             
% java PLSSC 10 1 20 2 30 1 2 3
S = [10, 1, 20, 2, 30, 1, 2, 3]
G =
S[0]=10 : S[4]=30 S[2]=20 
S[1]=1 : S[7]=3 S[6]=2 S[4]=30 S[3]=2 S[2]=20 
S[2]=20 : S[4]=30 
S[3]=2 : S[7]=3 S[4]=30 
S[4]=30 : 
S[5]=1 : S[7]=3 S[6]=2 
S[6]=2 : S[7]=3 
S[7]=3 : 
G' =
S[0]=10 : 
S[1]=1 : 
S[2]=20 : S[1]=1 S[0]=10 
S[3]=2 : S[1]=1 
S[4]=30 : S[3]=2 S[2]=20 S[1]=1 S[0]=10 
S[5]=1 : 
S[6]=2 : S[5]=1 S[1]=1 
S[7]=3 : S[6]=2 S[5]=1 S[3]=2 S[1]=1 
S = [10, 1, 20, 2, 30, 1, 2, 3]
L = [1, 1, 2, 2, 3, 1, 2, 3]
A = [-1, -1, 1, 1, 3, -1, 5, 6]
Longueur des plssc : 3
Une plssc finit en j = 4
Une plssc de S = [10, 1, 20, 2, 30, 1, 2, 3] : S[1]=1 S[3]=2 S[4]=30 
% 
% java PLSSC 6 5 1 2 3 4 5 2 7 6 8 0 1             <<< l'exemple de l'énoncé.             
S = [6, 5, 1, 2, 3, 4, 5, 2, 7, 6, 8, 0, 1]
G =
S[0]=6 : S[10]=8 S[8]=7 
S[1]=5 : S[10]=8 S[9]=6 S[8]=7 
S[2]=1 : S[10]=8 S[9]=6 S[8]=7 S[7]=2 S[6]=5 S[5]=4 S[4]=3 S[3]=2 
S[3]=2 : S[10]=8 S[9]=6 S[8]=7 S[6]=5 S[5]=4 S[4]=3 
S[4]=3 : S[10]=8 S[9]=6 S[8]=7 S[6]=5 S[5]=4 
S[5]=4 : S[10]=8 S[9]=6 S[8]=7 S[6]=5 
S[6]=5 : S[10]=8 S[9]=6 S[8]=7 
S[7]=2 : S[10]=8 S[9]=6 S[8]=7 
S[8]=7 : S[10]=8 
S[9]=6 : S[10]=8 
S[10]=8 : 
S[11]=0 : S[12]=1 
S[12]=1 : 
G' =
S[0]=6 : 
S[1]=5 : 
S[2]=1 : 
S[3]=2 : S[2]=1 
S[4]=3 : S[3]=2 S[2]=1 
S[5]=4 : S[4]=3 S[3]=2 S[2]=1 
S[6]=5 : S[5]=4 S[4]=3 S[3]=2 S[2]=1 
S[7]=2 : S[2]=1 
S[8]=7 : S[7]=2 S[6]=5 S[5]=4 S[4]=3 S[3]=2 S[2]=1 S[1]=5 S[0]=6 
S[9]=6 : S[7]=2 S[6]=5 S[5]=4 S[4]=3 S[3]=2 S[2]=1 S[1]=5 
S[10]=8 : S[9]=6 S[8]=7 S[7]=2 S[6]=5 S[5]=4 S[4]=3 S[3]=2 S[2]=1 S[1]=5 S[0]=6 
S[11]=0 : 
S[12]=1 : S[11]=0 
S = [6, 5, 1, 2, 3, 4, 5, 2, 7, 6, 8, 0, 1]
L = [1, 1, 1, 2, 3, 4, 5, 2, 6, 6, 7, 1, 2]
A = [-1, -1, -1, 2, 3, 4, 5, 2, 6, 6, 9, -1, 11]
Une plssc finit en j = 10, sa longueur est l(10) = 7
Une plssc de S = [6, 5, 1, 2, 3, 4, 5, 2, 7, 6, 8, 0, 1] : S[2]=1 S[3]=2 S[4]=3 S[5]=4 S[6]=5 S[9]=6 S[10]=8 
% 
*/