import java.util.Random;
import java.util.Arrays;
public class TD8 {

	public static void main(String[] Args){
	
		{ 	System.out.println("Intersection de deux ensembles");
			int[] 	A = {-1, 2, 4, 6, 8},
					B = {-3, -1, 0, 2, 5, 7, 8, 9};
			System.out.printf("%s inter %s = %s\n", 
				Arrays.toString(A), Arrays.toString(B), 
				Arrays.toString(intersection(A,B))
			); 
		}
		
		{  
			System.out.println("\nPremier plus long sous-tableau constant");
			int m = 15, inf = 0, sup = 4;
         	int[] T = tableauAleatoire(m,inf,sup); 
   			System.out.println("T = " + Arrays.toString(T));
   			int[] df = pplstc(T);
   			System.out.printf("d = %d, f = %d\n", df[0], df[1]);
   		}

		{ 
			System.out.println("\nPremier sous-tableau de somme maximum");
			int[] T = {-1, 2, 1, -4, 3, 4, -6, 2, 3, 2, -3, 1};
			System.out.println("T = " + Arrays.toString(T));
			int[] dfs = pstsm(T); int d = dfs[0], f = dfs[1], s = dfs[2];
			int[] PSTSM = Arrays.copyOfRange(T, d, f);
			System.out.printf("T[%d:%d] de somme %d, maximum : %s\n", d, f, s, Arrays.toString(PSTSM));
			System.out.println();
		}
	}

	static int[] intersection(int[] A, int[] B){ int m = A.length, n = B.length;
		int[] C = new int[min(m,n)];
		Arrays.sort(A); Arrays.sort(B); // A et B doivent être strictement croissants
		if (!croissantStrict(A)){
		    System.out.println("A n'est pas un ensemble, il contient des répétitions");
		    return null; 
		}
		if (!croissantStrict(B)){
		    System.out.println("B n'est pas un ensemble, il contient des répétitions");
		    return null;
		}
		// I(k, p, q) : C[0:k] union A[p:m] inter B[q:n] = A inter B
		...
		return Arrays.copyOfRange(C,0,k);
   }

   static boolean croissantStrict(int[] T){ int n = T.length;
		int j = 1; 
		while (j < n && T[j-1] < T[j])
			j++;
		return j == n;
   }
   
   static int min(int x, int y){if (x <= y) return x; return y;}

   static int[] pplstc(int[] T){ int n = T.length; // Premier plus long ss-tab. constant
   // I(d,f,j,k) : T[d:f] est le pplstc du k-préfixe de T et T[j:k] le plus long suf. cst.
   		...
		return new int[] {d,f};
   }
   
   static int[] pstsm(int[] T){ int n = T.length; // premier ss-tab. de somme max.
   // I(d,f,s,j,k,sp) : T[d:f], de somme s, est le pstsm de T[0:k] et
   //    T[j:k], de somme s', est le suffixe de T[0:k] de somme maximum.
 		...
     	return new int[] {d,f,s};
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
% java TD8
Intersection de deux ensembles
[-1, 2, 4, 6, 8] inter [-3, -1, 0, 2, 5, 7, 8, 9] = [-1, 2, 8]

Premier plus long sous-tableau constant
T = [2, 1, 3, 3, 0, 3, 0, 0, 3, 3, 2, 2, 2, 3, 2]
d = 10, f = 13

Premier sous-tableau de somme maximum
T = [-1, 2, 1, -4, 3, 4, -6, 2, 3, 2, -3, 1]
T[4:10] de somme 8, maximum : [3, 4, -6, 2, 3, 2]

% 

*/