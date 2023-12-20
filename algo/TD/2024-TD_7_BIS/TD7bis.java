import java.util.Random;
import java.util.Arrays;
public class TD7bis{ // Exemple d'exécution en fin de fichier, comme d'hab.

	public static void main(String[] Args){
 	  	{ 	System.out.println("\n1) Nombres d'occurrences des valeurs de T");
  	 		int n = 10, inf = 0, sup = 6; // valeurs de T aléatoires dans [0:6] 
  	 		int[] T = tableauAléatoire(n,inf,sup); // T[0:n] à valeurs au hasard dans [inf:sup]
  	 		System.out.printf("T : %s, max(T) = %d\n", Arrays.toString(T), max(T));
  	 		int[] N = nombreOccurrences(T);
  	 		int[] IE = intervalleEntiers(inf, sup);
  	 		System.out.printf("[%d:%d] :\t\t\t%s\n", inf, sup, Arrays.toString(IE));
  	 		System.out.printf("nb occurrences :\t%s\n", Arrays.toString(N));
  	 	}
  		{ 	System.out.println("\n2) Renverser T");
  			int m = 10, inf = 0, sup = 6; 
  	 		int[] T = tableauAléatoire(m,inf,sup); // T[0:m] à valeurs au hasard dans [inf:sup]
  	 		System.out.println("T :\t\t" + Arrays.toString(T));
   		 	renverser(T);
   			System.out.println("renversé(T) :\t" + Arrays.toString(T));
   		}
  		{ 	System.out.println("\n3) Ensemble des valeurs de T");
  			int m = 10, inf = 0, sup = 6; 
   			int[] T = tableauAléatoire(m,inf,sup); // T[0:m] à valeurs au hasard dans [inf:sup]
   			Arrays.sort(T); 
   			System.out.println("T :\t\t" + Arrays.toString(T));
			int[] set = bagToSet(T); // set = T[0:k] = ens(T[0:n])
	 		System.out.printf("ens(T) = \t%s\n", Arrays.toString(set));  	 	
  	 	}
  	 }
	static int[] intervalleEntiers(int inf, int sup){ int n = sup-inf;
	// retourne [inf, inf+1, ..., sup-1] c'est-à-dire [inf:sup].
		int[] T = new int[n]; 
		for (int i = 0; i < n; i++)
			T[i] = i;
		return T;
	}	 
  	static int[] tableauAléatoire(int n, int inf, int sup){ int [] T = new int[n];
  	// retourne T[0:n] à valeurs dans [inf:sup] 
  		Random r = new Random();
  		for (int i = 0; i < n; i++)
  			T[i] = inf + r.nextInt(sup-inf);
  		return T;
  	} 
 	static int[] nombreOccurrences(int[] T){ int n = T.length ; int m = max(T);
 	/* Les valeurs de T[0:n] sont dans l'intervalle [0:max+1]
 	I(k) : N est le tableau des nombres d'occurrences des valeurs dans le k-préfixe de T
 	Init : k = 0, N = [..., 0, ...]
 	Arrêt : ...
 	Progression : on augmente de 1 le nombre d'occurrences de la valeur T[k]. 
 		Autrement dit : I(k) et k != n et N[T[k]]++ ==> I(k+1)
 		Autrement dit : I(k) et k != n et N[T[k]] = n_{t_{k}} + 1 ==> I(k+1) 
 	*/
 		int[] N = new int[m+1];
 		for (int i = 0; i < m; i++) N[i] = 0; // inutile en Java
 		int k = 0;  
 		// I(k)
 		while (k != n){ // I(k) et k!=n
 			N[T[k]] = N[T[k]]+1; // I(k+1)
 			k++; // I(k)
 		} // I(n) donc N contient les nombres d'occurrences des valeurs de T.
 		return N;
 	}
 	static void renverser(int[] T){ int n = T.length;
 	/* I(k) : le k-préfixe de T est le renversé du (n-k) suffixe de T
 	Remarque : la tranche de tableau restant à traiter est T[k:n-k].
 	[t_{n}...t_{n-k}|xxxxxxxxx|t_{k}...t_{0}}]
 	                 k         n-k
 	                  à traiter
 	Initialisation : k = 0
 	Arrêt : (n-k-k) ≤ 1 La tranche de tableau restant à traiter est vide.
 	Progression : I(k) et n-2k ≥ 2 et t_k permutée avec t_{n-k-1} ==> I(k+1) */
 		int k = 0; // T[0:0]=[] est le renversé de T[n:n]=[]
 		while (n-2*k >= 1){ permuter(T,k,n-k-1); // I(k+1)
 			k = k+1; // I(k)
 		}
 		// I(k) et n-2*k ≤ 1, donc la sortie S (T contient ses valeurs dans l'ordre inverse)	
 	}
  	static void permuter(int[] T, int i, int j){int ti=T[i]; T[i]=T[j]; T[j]=ti;}
 	static int max(int[] T){int n = T.length;
 	/* I(m,k) : m = max(T[0:k]); Initialisation : m = -∞, k = 0; Arrêt k = n;
 	Progression : I(m,k) et k≠n et ...
 		• t_k <= m ==> I(m,k+1)
 		• t_k > m ==> I(t_k, k+1) */
 		int k = 0, m = Integer.MIN_VALUE;
 		while (k != n) // I(m,k) et k≠n
 			if (T[k] <= m) // I(m,k+1)
 				k = k+1; // I(m,k) 
 			else {// I(t_k, k+1)
 				m = T[k]; // I(m,k+1)
 				k = k+1; // I(m,k)
 			}
 		// I(m,n) c'est-à-dire m = max(T[0:n])
 		return m;
 	}
 	static int[] bagToSet(int[] T){ int n = T.length;
 	// T est croissant. Exemple : 2 2 3 4 4 4 6 7
 	/* I(k,j) : le k-préfixe de T contient e(j) (ensemble des valeurs du j-suffixe de T)
 	Il est sous-entendu que T contient une permutation des valeurs de T.
 	Initialisation : j=1, k=1  ( T[0:1] = ens(T[0:1]) car il n'y a qu'une valeur dans T[0:1] )
 	Arrêt : j = n. Nous avons T[0:k] = ens(T[0:n])
 	Progression : I(k,j) et j!=n et ...
 		• t_j == t_{k-1} ==> I(k,j+1)
 		• t_j ≠ t_{k-1} et t_j permutée avec t_{k} ==> I(k+1,j+1)
 	*/
 		int j=1, k=1; // I(k,j)
 		while (j != n)
 			if (T[j]==T[k-1]) // I(k,j+1)
 				j++; // I(k,j)
 			else{ permuter(T,j,k); // I(k+1;j+1)
 				k++; 
 				j++;// I(k,j)
 			}
 		// I(k,n) c'est à dire la sortie souhaitée
 		return Arrays.copyOfRange(T,0,k);
 	}
 }
 /* 
% javac TD7bis.java
% java TD7bis      

1) Nombres d'occurrences des valeurs de T
T : [0, 2, 2, 0, 5, 4, 2, 4, 4, 1], max(T) = 5
[0:6] :			[0, 1, 2, 3, 4, 5]
nb occurrences :	[2, 1, 3, 0, 3, 1]

2) Renverser T
T :		[-1, 0, 0, 0, 0, 1, 1, 0, -1, 0]
renversé(T) :	[0, -1, 0, 1, 1, 0, 0, 0, 0, -1]

3) Ensemble des valeurs de T
T :		[-3, -2, -2, -1, -1, -1, 0, 0, 1, 1]
ens(T) = 	[-3, -2, -1, 0, 1]
% javac TD7bis.java
% java TD7bis      

1) Nombres d'occurrences des valeurs de T
T : [2, 0, 2, 5, 0, 5, 1, 5, 2, 4], max(T) = 5
[0:6] :			[0, 1, 2, 3, 4, 5]
nb occurrences :	[2, 1, 3, 0, 1, 3]

2) Renverser T
T :		[4, 4, 2, 5, 1, 3, 5, 2, 2, 5]
renversé(T) :	[5, 2, 2, 5, 3, 1, 5, 2, 4, 4]

3) Ensemble des valeurs de T
T :		[0, 1, 1, 1, 2, 2, 2, 3, 3, 5]
ens(T) = 	[0, 1, 2, 3, 5]
% 
 
 */