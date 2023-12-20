import java.util.Arrays;
public class Exercice2{ 

/* Exercice 2 : sous-ensemble de X, de somme S.
Supposons le problème résolu. Deux possiblités : le dernier élément est ou n'est pas dans
le sous-ensemble de X de somme S. 
-- S'il n'y est pas : e(n,S) est vrai si est seulement si e(n-1,S) est vrai 
(il existait "déjà" un sous-ensemble de X[0:n-1] de somme S.)
-- S'il y est : e(n,S) est vrai si et seulement si e(n-1,S-X[n-1]) est vrai
(il existe un sous-ensemble E' des n-1 premiers éléments de X dont la somme vaut S-X[n-1].
Alors, E' U {X[n-1]} est de somme S-X[n-1]+X[n-1] = S)
Ainsi : e(n,S) = e(n-1,S) ou e(n-1,S-x(n-1))
Généralisation : e(k,s) = e(k-1,s) ou e(k-1, s-x(k-1)), 1 ≤ k < n+1, 0 ≤ s < S+1
Base : k=0. La somme des éléments de l'ensemble vide vaut 0. Donc 
	e(0,0) = vrai et pour tout s, 1 ≤ s < S+1, e(0,s) = faux
*/

	public static void main(String[] Args){ int nargs = Args.length;
		if (nargs == 0){
		System.out.println("Usage : java Exercice2 ... xi ... S"); 
		return;}
		int n = nargs-1; // taille de X
		int[] X = new int[n]; 
		for (int i = 0; i < n; i++) 
			X[i] = Integer.parseInt(Args[i]);
		int S = Integer.parseInt(Args[n]);
		System.out.printf("n = %d, X = %s, S = %d\n", n, Arrays.toString(X), S);
		boolean[][] E = calculerE(X,S);
		if (!E[n][S]) 
			System.out.println(" Il n'y a pas de sous-ensemble de X de somme S");
		else { System.out.println("Un sous-ensemble de X, de somme S : "); 
			ase(E, X, n, S);
			System.out.println();
		}
	}
	static boolean[][] calculerE(int[] X, int S){ int n = X.length;
		boolean E[][] = new boolean[n+1][S+1]; // de terme général E[k][s] = e(k,s)
		// base
		E[0][0] = true; for (int s = 1; s < S+1; s++) E[0][s] = false;
		// cas général : e(k,s) = e(k-1,s) ou e(k-1, s-x(k-1)), 1 ≤ k < n+1, 0 ≤ s < S+1
		for (int k = 1; k < n+1; k++)
			for (int s = 0; s < S+1; s++)
				if (s - X[k-1] >= 0) 
					E[k][s] = E[k-1][s] || E[k-1][s-X[k-1]];
				else E[k][s] = E[k-1][s];
		return E;
	}
	static void ase(boolean[][] E, int[] X, int k, int s){
	// Affiche un sous-ensemble de X[0:k] de somme s
	// Notation : se(k,s) = sous-ensemble de X[0:k] de somme s
		if (k == 0 && s == 0) return; // se(0,0) = {}. Sans rien faire, il a été affiché.
		// E[0:k] est non vide
		if (E[k-1][s]) // se(k,s) = se(k-1,s)
			ase(E,X,k-1,s); // se(k-1,s) a été affiché donc se(k,s) a été affiché
		else  {// se(k,s) = se(k-1,s-x(k-1)) U {x(k-1)}
			ase(E,X,k-1,s-X[k-1]); // se(k-1,s-x(k-1)) a été affiché
			System.out.printf("X[%d]=%d ", k-1, X[k-1]); // x(k-1) a été imprimé 
			// se(k-1,s-x(k-1)) affiché et x(k-1) imprimé donc se(k,s) affiché.
		}
	}
}

/*
% javac Exercice2.java
% java Exercice2 0    
n = 0, X = [], S = 0
Un sous-ensemble de X, de somme S : 

% java Exercice2 2 4 6   7
n = 3, X = [2, 4, 6], S = 7
 Il n'y a pas de sous-ensemble de X de somme S
% java Exercice2 2 4 6   10
n = 3, X = [2, 4, 6], S = 10
Un sous-ensemble de X, de somme S : 
X[1]=4 X[2]=6 
% java Exercice2 2 4 6   12 
n = 3, X = [2, 4, 6], S = 12
Un sous-ensemble de X, de somme S : 
X[0]=2 X[1]=4 X[2]=6 
% java Exercice2 2 4 6   4 
n = 3, X = [2, 4, 6], S = 4
Un sous-ensemble de X, de somme S : 
X[1]=4 
% 
*/
