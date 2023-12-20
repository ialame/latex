import java.util.Arrays;
import java.util.Random;
public class QuickSelect{

	public static void main(String[] args){
			System.out.println("Selection rapide (quickSelect). Exemple : ");
			int n = 15;
			int[] T = randomArray(n); // n entiers au hasard dans [0:15]		
			System.out.printf("T[0:%d] :  %s\n", n, Arrays.toString(T)); 
			System.out.println("Nous ne trions pas le tableau T.");
			for (int p = 1; p < n+1; p++)
				System.out.printf("%d-ème valeur : %d\n", p, sapiensQuickSelect(T,p));
			Arrays.sort(T);
			System.out.printf("Tableau T trié, pour vérifier ces résultats : %s\n", 
				Arrays.toString(T)); 
	}

	static Random rand = new Random();
	// Les Sapiens font appel à la fonction sapiensQuickSelect ci-dessous.
	// Ils l'appellent avec s dans l'intervalle [1:n+1]
	public static int sapiensQuickSelect(int[] T, int s){int n = T.length;
		return qsel(T,0,n,s-1); // s-1 in [0:n]
	}
	
	static int qsel(int[] T, int i, int j, int p){// p est dans [0:j-i].
			...
	}
	
	static int segmenter(int[] T, int i, int j){ // copié-collé depuis QuickSort.java
	/*	Calcule dans T[i:j] une permutation de T[i:j] vérifiant T[i:k] ≤ T[k:k+1] < T[k+1:j]
		I(k,j') : T[i:k] ≤ T[k:k+1] < T[k+1:j']
		Arrêt : j' = j
		Initialisation : i = k et j'=k+1
		Progression : I(k,j') et j'≠j et ...
			... t_k < t_{j'} ==> I(k,j'+1)
			... t_{j'} ≤ t_k et T[k]  = t_{j'} et T[k+1] = t_k et T[j'] = t_{k+1} ==> I(k+1,j'+1)  
	*/
		// choisir au hasard un indice h dans [i:j] et permuter T[i] avec T[h]
		int h = i+rand.nextInt(j-i);
		permuter(T,i,h);
		int k = i, jp = k+1;
		while (jp!=j)
			if (T[jp] > T[k]) // I(k,jp+1)
				jp++ ; // I(k,jp)
			else { permuter(T,jp,k+1); permuter(T,k+1,k); // I(k+1;jp+1)
				k++; jp++; // I(k;jp)
			}
		return k;
	}
	
	static void permuter(int[] T, int i, int j){ // copié-collé depuis QuickSort.java
		int ti = T[i];
		T[i] = T[j];
		T[j] = ti;
	}
	
	static int[] randomArray(int n){ // // copié-collé depuis QuickSort.java
	// Retourne T[0:n] à valeurs aléatoires dans [0:n]
		Random r = new Random();
		int[] T = new int[n];
		for (int i = 0; i < n; i++)
			T[i] = r.nextInt(n);
		return T;
	}		
}

/*
% javac QuickSelect.java
% java QuickSelect      
Selection rapide (quickSelect). Exemple : 
T[0:15] :  [7, 10, 10, 11, 0, 3, 13, 2, 10, 12, 5, 4, 2, 2, 1]
Nous ne trions pas le tableau T.
1-ème valeur : 0
2-ème valeur : 1
3-ème valeur : 2
4-ème valeur : 2
5-ème valeur : 2
6-ème valeur : 3
7-ème valeur : 4
8-ème valeur : 5
9-ème valeur : 7
10-ème valeur : 10
11-ème valeur : 10
12-ème valeur : 10
13-ème valeur : 11
14-ème valeur : 12
15-ème valeur : 13
Tableau T trié, pour vérifier ces résultats : [0, 1, 2, 2, 2, 3, 4, 5, 7, 10, 10, 10, 11, 12, 13]
%
*/