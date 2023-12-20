import java.util.Arrays;
import java.util.HashMap;
class Segmentation{
	static int[] calculerA(String C, HashMap<String,Boolean> D){ int n = C.length();
	/* calcule S[0:n+1] de terme général S[j] = s(j) et
	A[0:n+1] de terme général A[j] = arg s(j), et retourne A. */
		boolean[] S = new boolean[n+1];
		int[] A = new int[n+1];
		// base de la récurrence j = 0
		[...]
		// cas général : 1 ≤ j < n+1
		// s(j) = "il existe i, 0 ≤ i < j tel que S[i:j] est un mot du dictionnaire D et
		// S[0:i] est segmentable."
		[...]
		return A;
	}
	
	static String segmentation(String C, int[] A, int j){
	/* retourne une segmentation de C.substring(0,j) (le j-préfixe de la chaîne C) */
		[...]
	}
	static HashMap<String,Boolean> dictionnaire(){
		HashMap<String, Boolean> D = new HashMap<String, Boolean>();
		D.put("la",true); D.put("ci",true); D.put("cigale",true); D.put("et",true); 
		D.put("fourmi",true); D.put("le",true); D.put("ayant",true);
		D.put("chant",true); D.put("chanté",true); D.put("tout",true);
		D.put("l",true); D.put("été",true);
		return D;      
	}
	public static void main(String[] Args){
		if (Args.length != 1){
			System.out.println("Usage : java Segmentation chaîne");
			return;
		}
		
		HashMap<String,Boolean> D = dictionnaire();

		String C = Args[0]; 
		int n = C.length();		
		int[] A = calculerA(C,D);
		if (A[n] == -1)
			System.out.println(C + " n'est pas segmentable");
		else 
			System.out.println("\nSegmentation de " + C + " : " + segmentation(C,A,n));

// Les deux lignes ci-dessous affichent le tableau A et le dictionnaire D. 
// Les mettre en en commentaire si l'on ne veut pas les voir.
		System.out.println("\nA = " + Arrays.toString(A)); 			
		System.out.println("D = " + D.toString()); 			
	}
}
/*
% javac Segmentation.java      
% java Segmentation lacigaleayantchanté

Segmentation de lacigaleayantchanté :  la cigale ayant chanté

A = [0, 0, 0, -1, 2, -1, -1, -1, 2, -1, -1, -1, -1, 8, -1, -1, -1, -1, 13, 13]
D = {ayant=true, chanté=true, chant=true, fourmi=true, la=true, ci=true, le=true, tout=true, l=true, été=true, cigale=true, et=true}
% java Segmentation lacigaleayantdansé 
lacigaleayantdansé n'est pas segmentable

A = [0, 0, 0, -1, 2, -1, -1, -1, 2, -1, -1, -1, -1, 8, -1, -1, -1, -1, -1]
D = {ayant=true, chanté=true, chant=true, fourmi=true, la=true, ci=true, le=true, tout=true, l=true, été=true, cigale=true, et=true}
% 
*/

