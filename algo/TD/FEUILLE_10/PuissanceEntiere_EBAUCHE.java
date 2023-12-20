// TD10, puissance entière, exercice 1.					13.10.2023 rene.natowicz@esiee.fr 

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
		...		
		return p;
	}
	static int pesr(int a, int b){
		if (b==0) return ... ;
		// b > 0
		return ... ;
	}
	static int pedi(int a, int b){
		...
		return p;
	}
	static int pedr(int a, int b){
		if (b==0) return ...;
		if (b%2==0) // b pair
			return ...;
		return ...;
	}
}