// TD10, entiers bi-carrés								31.10.2023 rene.natowicz@esiee.fr 
public class Bicarrés{ 
	public static void main(String[] args){ int nargs = args.length;
		if (nargs == 0){ System.out.println("usage : java Bicarés n"); return ;}
		int N = Integer.parseInt(args[0]); // affiche les N premiers entiers bi-carrés
		System.out.printf("%d premiers entiers : bi-carrés et non bi-carrés...\n", N);
		for (int n = 0; n < N; n++){
			int [] xy = bicarré_0(n); 
			// xy = si n bicarré alors [x,y] tels que n = x^2 + y^2 sinon [-1,-1] 
			if (xy[0] == -1) 
				System.out.printf("%d n'est pas bi-carré\n", n);
			else{ int x = xy[0], y = xy[1];
				System.out.printf("%d = %d^2 + %d^2\n", n, x, y);
			}
		}
	}
	/* La fonction bicarré est une recherche arrière dans un tableau fictif
	F[0:n+1][0:n+1] de terme général F[i][j] = i^2 + j^2.
	Ses lignes et colonnes fictives sont strictement croissantes. 
	Il s'agit donc d'une application directe de la recherche arrière vue en cours.
	Retourne : 
		si n non bicarré alors {-1,-1} ; 
		si n bicarré alors {i,j} tel que n^2 = i^2 + j^2
	*/
	// Première version en Theta(n) multiplications : 
	static int[] bicarré_0(int n){
		int p = 0, q = n+1;  
		while (...)
			...
		if (...)
			return new int[] {p,q-1};
		else
			return new int[] {-1,-1}; 
	}
	/* 2e version : Il s'agit de la version précédente sans les multiplications 
	du corps de boucle. Elle sont remplacées par des multiplications par 2 : 
	p = 2*p ou q = 2*q, respectivement p<<1 et q<<1, c'est-à-dire des décalages à gauche, 
	opérations en temps constant.
	Nous définissons la variable entière s = p*p + (q-1) * (q-1).
	• Si n > spq : p devient p+1 et q ne change pas. 
	  Donc spq devient ... 
	• Dans le cas où n < spq : q devient q-1 et p ne change pas.
	  Donc spq devient ...
	*/
	static int[] bicarré(int n){
		int p = 0, q = n+1, s = (q-1)*(q-1); // s = p*p + (q-1)*(q-1), 1 multiplication.
		while (...)
			...
		}
		if (...) 
			return new int[] {p,q-1};
		else
			return new int[] {-1,-1}; 
	}
}
/* Compilation et exemple d'exécution dans une fenêtre Terminal Unix
% javac Bicarrés.java
% java Bicarrés 21   
21 premiers entiers : bi-carrés et non bi-carrés...
0 = 0^2 + 0^2
1 = 0^2 + 1^2
2 = 1^2 + 1^2
3 n'est pas bi-carré
4 = 0^2 + 2^2
5 = 1^2 + 2^2
6 n'est pas bi-carré
7 n'est pas bi-carré
8 = 2^2 + 2^2
9 = 0^2 + 3^2
10 = 1^2 + 3^2
11 n'est pas bi-carré
12 n'est pas bi-carré
13 = 2^2 + 3^2
14 n'est pas bi-carré
15 n'est pas bi-carré
16 = 0^2 + 4^2
17 = 1^2 + 4^2
18 = 3^2 + 3^2
19 n'est pas bi-carré
20 = 2^2 + 4^2
% 
*/
