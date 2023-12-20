// TD10, puissance entière, exercice 2.				13.10.2023 rene.natowicz@esiee.fr 
import java.util.Arrays;
public class Couples{ // couples de T à somme dans T 
	public static void main(String[] args){ int n = args.length;
		if (n == 0){ System.out.println("Usage : Couples t0 t1 t2 ..."); 
			return;
		}
		int[] T = toIntTab(args);		
		Arrays.sort(T); // T croissant
		if (!croissantStrict(T)){
			System.out.printf("Les données d'entrées ont des répétitions");
			return;		
		}
		if (!positifsStrict(T)){ // les valeur T doivent être strictement positives
			System.out.printf("Les données d'entrées doivent être strictement positives"); 
			return;		
		}
		System.out.printf("Tableau T : %s\n", Arrays.toString(T));
		System.out.printf("Nombre de Couples de T à somme dans T : %d\n", nctst(T));
	}
	static int[] toIntTab(String[] args){int n = args.length;
		int[] T = new int[n];
		for (int i = 0; i < n; i++) T[i] = Integer.parseInt(args[i]);
		return T;
	}
	static boolean croissantStrict(int[] T){ int n = T.length;
		int i = 1; 
		while (i < n && T[i-1] < T[i]) 
			i++; 
		return i == n;
	}
	static boolean positifsStrict(int[] T){ int n = T.length;
		int i = 0; 
		while (i < n && T[i] > 0) 
			i++; 
		return i == n;
	}
	static int nctst(int[] T){ int m = T.length;
		int nc = 0; // nombre de couples de T à somme dans T 
		for (int k = 0; k < m; k++)
			nc = nc + nctss(T, T[k]); // nb de couples (ti,tj), i ≤j, de somme ti+tj=tk
		return nc;
	}
	static int nctss(int[] T, int s){ // Nombre de couples de T de somme s.
	/* Calcul par recherche arrière de s dans tableau fictif F[O:n][0:n] de terme général
	F[i][j] = T[i] + T[j] 
	Le tableau T est strictement croissant. Donc les lignes et les colonnes de F sont
	strictement croissantes.
	Pour calculer le nombre d'occurrences de s dans F, voir la recherche arrière, RA,
	traitée en cours. 
	La RA retourne le nombre d'occurrences de la valeur x dans T[0:m][0:n].
	Elle compare x à T[p][q-1] et à chaque itération elle réduit la taille du sous-tableau 
	T[p:m][0:q] qui reste à explorer.
		Si x = T[p][q-1] p++ q--.
		Si x < T[p][q-1] q--.
		Si x > T[p][q-1] q++.
	Condition d'arrêt : le sous-tableau T[p:m][0:q] est vide. Donc p=m ou q=0.

	Dans le cas présent quelles sont les différences ? 
	1) Le tableau F est fictif. Dans la RA, remplacer F[p][q-1] par T[p]+T[q-1].
	2) Nous cherchons les couples (ti,tj), i <= j.
	La condition d'arrêt de la RA était : p=m ou q=0 car on recherchait tous les couples
	(t_p,t_{q-1}) tels que T[p][q-1] = s.
	À présent nous cherchons les couples (t_p,t_{q-1}), p <= q-1, tels que T[p][q-1] = s.
	On itère tant que p ≤ q-1. La condition d'arrêt est p > q-1 (autrement dit : p >= q.)
.
	*/
		int m = T.length, n = T.length; // m = n lignes et colonnes 
		int p = 0, q = n, k = 0; // k est le nombre d'occurrences de s dans F 
		while (!(p>q-1)){ int tpq = T[p]+T[q-1];
			if (tpq == s){
				p++; q--; k++;
			}
			else
			if (tpq > s)
				q--; 
			else 
				p++;
		}
		return k;
	}
}
/*
% javac Couples.java        
% java Couples              
Usage : Couples t0 t1 t2 ...
% java Couples 1
Tableau T : [1]
Nombre de Couples de T à somme dans T : 0
% java Couples 1 2
Tableau T : [1, 2]
Nombre de Couples de T à somme dans T : 1
% java Couples 1 2 3
Tableau T : [1, 2, 3]
Nombre de Couples de T à somme dans T : 2
% java Couples 1 2 3 4
Tableau T : [1, 2, 3, 4]
Nombre de Couples de T à somme dans T : 4
% java Couples 1 2 3 4 5
Tableau T : [1, 2, 3, 4, 5]
Nombre de Couples de T à somme dans T : 6
% java Couples 1 2 3 4 5 6
Tableau T : [1, 2, 3, 4, 5, 6]
Nombre de Couples de T à somme dans T : 9
% java Couples 1 2 3 4 5 6 7
Tableau T : [1, 2, 3, 4, 5, 6, 7]
Nombre de Couples de T à somme dans T : 12
% java Couples 1 2 3 4 5 6 7 8
Tableau T : [1, 2, 3, 4, 5, 6, 7, 8]
Nombre de Couples de T à somme dans T : 16
%
*/ 