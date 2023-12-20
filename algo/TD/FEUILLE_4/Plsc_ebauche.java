// TD4 
public class Plsc{ /* Plus longue sous-séquence commune à deux séquences T et U.
Equation de récurrence des valeurs l(p,q)
*/

	static int[][] calculerL(String T, String U){ int m = T.length(), n = U.length();
	// retourne L[0:m+1][0:n+1] de terme général L[p][q] = l(p,q)
		int[][] L = new int[m+1][n+1];
		// base
		...
		// cas général
		for (int p = 1; p < m+1; p++)
			for (int q = 1; q < n+1; q++)
				...
		return L; // calcul en Theta(m x n).
	}
	static String plsc(String T, String U, int[][] L, int p, int q){
	// retourne une plsc aux p et q préfixes de X et Y.
		if (p == 0 || q == 0) return "";
		if (T.charAt(p-1) == U.charAt(q-1)) 
			return ...
		if (L[p-1][q] >= L[p][q-1]) return ...
		return ... 
	}
	public static void main(String[] args){ 
		if (args.length < 2){
			System.out.println("Usage : Plsc T U");
			return;
		}
		String T = args[0], U = args[1];
		int m = T.length(), n = U.length();
		int[][] L = calculerL(T,U);
		System.out.printf( "plsc(%s,%s) = %s\n", T, U, plsc(T,U,L,m,n) );
	}
}
/* Compilation, exécutions : 
% java Plsc aligatorithme algorithmique 
plsc(aligatorithme,algorithmique) = algorithme
% java Plsc uvuavvzzlwxyyvgzvowx tsbakghlpgmolibbdène
plsc(uvuavvzzlwxyyvgzvowx,tsbakghlpgmolibbdène) = algo
% 


*/