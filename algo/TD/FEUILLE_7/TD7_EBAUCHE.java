import java.util.Random;
import java.util.Arrays;
public class TD7{

	public static void main(String[] Args){
   		{ 	System.out.println("np1");
   			int m = 10, inf = -1, sup = 2; 
   			int[] T = tableauAleatoire(m,inf,sup); // T[0:m] à valeurs au hasard dans [inf:sup]
   			System.out.println("Avant : " + Arrays.toString(T));
   			int p = np1(T);
   			System.out.println("Après : " + Arrays.toString(T));
   			System.out.printf("p = %d\n",p);
   			System.out.println();
   		}
  		{ 	System.out.println("np2");
  			int m = 10, inf = -1, sup = 2; 
   			int[] T = tableauAleatoire(m,inf,sup); // T[0:m] à valeurs au hasard dans [inf:sup]
   			System.out.println("Avant : " + Arrays.toString(T));
    		int p = np2(T);
   			System.out.println("Après : " + Arrays.toString(T));
   			System.out.printf("p = %d\n",p);
   			System.out.println();
   		}
  		{ 	System.out.println("nnp1");
  			int m = 10, inf = -2, sup = 3; 
   			int[] T = tableauAleatoire(m,inf,sup); // T[0:m] à valeurs au hasard dans [inf:sup]
   			System.out.println("Avant : " + Arrays.toString(T));
   			int[] pq = nnp1(T);
   			System.out.println("Après : " + Arrays.toString(T));
   			System.out.printf("p = %d, q = %d\n", pq[0],pq[1]);   	
   			System.out.println();
   		}
  		{ 	System.out.println("nnp2");
  			int m = 10, inf = -2, sup = 3; 
   			int[] T = tableauAleatoire(m,inf,sup); // T[0:m] à valeurs au hasard dans [inf:sup]
   			System.out.println("Avant : " + Arrays.toString(T));
   			int[] pq = nnp2(T);
   			System.out.println("Après : " + Arrays.toString(T));
   			System.out.printf("p = %d, q = %d\n", pq[0],pq[1]);  
   			System.out.println();
   		} 		

		{  
      		System.out.println("Tri rapide avec segmentation en 3 parties");
 		   	Random r = new Random();
        	int n = (int)Math.pow(2,5), inf = 0, sup = 5;
        	int[] T = tableauAleatoire(n,inf,sup);
         	System.out.println("Avant : " + Arrays.toString(T)); 
        	qs(T,0,n);
         	System.out.println("Après : " + Arrays.toString(T));
   			System.out.println();
		}
		
		{ 
			System.out.print("Temps de calcul avec n = 10^20 et 10 valeurs différentes dans T. ");
        	Random r = new Random();
        	int n = (int)Math.pow(2,20), inf = 0, sup = 10;
      	  	int[] T = tableauAleatoire(n,inf,sup);
			long avant = System.currentTimeMillis();
			qs(T,0,n);
			long apres = System.currentTimeMillis();
			System.out.printf("Temps d'exécution : %d ms\n", (apres-avant));
			System.out.println();
		}
		
	}


	static int np1(int[] T){ int m = T.length;
	// I(p,q) : T[0:p] < 0 et T[p:q] >=0
		...
		return p;
	}

	static int np2(int[] T){ int m = T.length;
	// I(p,q) : T[0:p] < 0 et T[q:m] >=0. 
		...
		return p;
	}
	
	static int[] nnp1(int[] T){ int m = T.length;
	// I(p,q,r) : T[0:p] < 0 et T[p:q] = 0 et T[q:r] > 0 
	// Le sous-tableau T[r:m] reste à traiter.
		...
		return new int[] {p,q}; 
	}
	
	static int[] nnp2(int[] T){ int m = T.length;
	// I(p,q,r) : T[0:p] < 0 et T[p:q] = 0 et T[r:m] > 0 
	// Le sous-tableau T[q:r] reste à traiter.
		...
		return new int[] {p,q}; 
	}
	
	static void permuter(int[] T, int i, int j){int x = T[i];
		T[i] = T[j]; 
		T[j] = x;
	}
   
	static void qs(int[] T, int i, int j){
		...
	}

	static int[] ts(int[] T, int i, int j){
   		// I(p,q,r) : T[i:p] < T[p:q] < T[r:j]
   		// Initialisation : T[i:p] vide, T[p:q] contient T[i], T[r:j] vide
  		...
   		return new int[] {p,q};
   }
	
   static int[] tableauAleatoire(int n, int inf, int sup){ 
   	// retour T[0:n] à valeurs dans [0:sup]
   		Random r = new Random();
   		int[] T = new int[n];
   		for (int i = 0; i < n; i++) T[i] = inf + r.nextInt(sup - inf);
   		return T;
   	}
}
/*
% javac TD7.java
% java TD7      
np1
Avant : [1, 1, -1, -1, 1, 0, 0, 1, -1, -1]
Après : [-1, -1, -1, -1, 1, 0, 0, 1, 1, 1]
p = 4

np2
Avant : [-1, 1, 1, -1, 1, -1, -1, -1, -1, 0]
Après : [-1, -1, -1, -1, -1, -1, 1, 1, 0, 1]
p = 6

nnp1
Avant : [1, -1, -2, -1, 1, 0, 0, -1, 2, -2]
Après : [-1, -2, -1, -1, -2, 0, 0, 1, 2, 1]
p = 5, q = 7

nnp2
Avant : [-2, 1, -2, 1, -1, 2, 0, 1, 2, -1]
Après : [-2, -1, -2, -1, 0, 2, 1, 2, 1, 1]
p = 4, q = 5

Tri rapide avec segmentation en 3 parties
Avant : [4, 0, 4, 1, 3, 1, 4, 3, 2, 1, 3, 4, 0, 0, 3, 4, 3, 2, 4, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 1, 0]
Après : [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4]

Temps de calcul avec n = 10^20 et 10 valeurs différentes dans T. Temps d'exécution : 28 ms

% 

*/