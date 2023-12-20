// TD10, puissance entière, exercice 1.
// 13.10.2023 rene.natowicz@esiee.fr 

public class PuissanceEntiere{ 
	public static void main(String[] args){ int n = args.length;
		if (n!=2){ System.out.println("Usage : PuissanceEntière a b");
			return;		
		}
		int a = Integer.parseInt(args[0]), b = Integer.parseInt(args[1]);
		System.out.printf("Puissance séquentielle itérative : %d ** %d = %d\n", 
			a, b, pesi(a,b));
		System.out.printf("Puissance séquentielle récursive : %d ** %d = %d\n", 
			a, b, pesr(a,b));
		System.out.printf("Puissance dichotomique itérative : %d ** %d = %d\n", 
			a, b, pedi(a,b));
		System.out.printf("Puissance dichotomique récursive : %d ** %d = %d\n", 
			a, b, pedr(a,b));
	}
	static int pesi(int a, int b){
	// I(p,b') : p * a**b' = a**b
		int p = 1, bp = b; // I(p,b') 
		while (bp!=0){ // I(p,b') et b'!=0 ==> I(p*a, b'-1)
			p = p*a; // I(p, b'-1)
			bp=bp-1; // I(p, b')
		} // I(p,0) c'est-à-dire p * a**0 = p * 1 = a**b.
		return p;
	}
	static int pesr(int a, int b){
		if (b == 0) return 1; 
		return a * pesr(a, b-1);
	}
	static int pedi(int a, int b){
	// I(p,a'b') : p * a'**b' = a**b
		int p = 1, ap = a, bp = b; // I(p,a',b') 
		while (bp!=0){ // I(p,a'b') et b'!=0 et ...
			if (bp%2 == 0) {// b' pair ==> I(p,a'*a',b'/2) 
				ap = ap*ap; // I(p,a',b'/2)
				bp = bp/2; // // I(p,a',b')
			} // I(p,a',b')
			else{ // I(p*a',a'*a',b'/2) 
				p = p*ap; // I(p,a'*a',b'/2)
				ap = ap*ap; // I(p,a',b'/2)
				bp = bp/2; // I(p,a',b') 
			} // I(p,a',b')
		} // I(p,0) c'est-à-dire p * a**0 = p * 1 = a**b.
		return p;
	}
	static int pedr(int a, int b){
		if (b == 0) return 1; 
		if (b%2 == 0)return pesr(a*a, b/2);
		return a*pesr(a*a, b/2);
	}
}