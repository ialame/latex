#include <stdio.h>
#include <math.h>
#define boolean int
typedef enum
{ False = 0, True = 1 }
Bool; 
/**
 * Classe représentant des nombres complexes.
 */
 typedef struct{
        float real; // Partie réelle
        float img; // Partie imaginaire
} Complex;

  // Constructeur d'un nombre complexe
   Complex new_Complex(float r, float i) {
   Complex z;
    z.real = r;
    z.img = i;
	return z;
  }

    float absolu(float x){
    if(x<0)
	  return -x;
	else
	  return x;
  }

/**
 * Classe de manipulation des nombres complexes.
 */
  // Valeur considérée comme nulle
  float EPSILON = (float)1e-12;
  /**
   * Méthode déterminant si deux nombres complexes
   * sont égaux.
   */
  boolean equals(Complex c1, Complex c2) {
    return (absolu(c1.real-c2.real) < EPSILON &&
        absolu(c1.img-c2.real)  < EPSILON);
  }
  /**
   * Méthode somme de deux nombres complexes.
   */
  Complex sum(Complex c1, Complex c2) {
    // La somme de deux nombres complexes est un nouvel
    // objet complexe dont les parties réelle et imaginaire
    // sont égales à la somme des parties 
    return new_Complex(c1.real+c2.real,c1.img+c2.img);
  }
  /**
   * Méthode produit de deux nombres complexes.
   */
  Complex product(Complex c1, Complex c2) {
    // Le produit de deux nombres complexes est un nouvel
    // objet complexe dont les parties réelle et imaginaire
    // sont calculées au préalable.
    float real = c1.real*c2.real-c1.img*c2.img;
    float img = c1.real*c2.img+c1.img*c2.real;
    return new_Complex(real,img);
  }
  /**
   * Méthode conjuguate d'nombres complexes.
   */
  Complex conjuguate(Complex c) {
    return new_Complex(c.real,-c.img);
  }
   /**
   * Méthode multReal : multiplication par un réel d'nombres complexes.
   */
  Complex multReal(float a,Complex c) {
    return new_Complex(a*c.real,a*c.img);
  }
  /**
   * Méthode div de deux nombres complexes.
   */
  Complex div(Complex c1, Complex c2) {
    // Le produit de deux nombres complexes est un nouvel
    // objet complexe dont les parties réelle et imaginaire
    // sont calculées au préalable.
    Complex z= product(c1,conjuguate(c2));
	float norme2 = product(c2,conjuguate(c2)).real;
	z=multReal(1/norme2,z);
    return z;
  }
  /**
   * Méthode d'affichage d'un nombre complexe.
   */
  void print(Complex c) {
    if (absolu(c.real) < EPSILON) {
      if (absolu(c.img) < EPSILON) {
    //printf("Si complexe est nul abs(%f)=%f",c.real,absolu(c.real));
    printf("0\n");
      } else {
    //printf("Si partie réelle nulles");
    printf("i\%f\n",c.img);
      }
    } else {
      if (absolu(c.img) < EPSILON) {
    //printf("Si partie imaginaire nulle");
    printf("%f\n",c.real);
      } else {
    //printf("Rien n'est nul");
	
    printf("%f+i%f\n",c.real,c.img);
      }
    }
  }
   /**
   * Méthode exp 
   */
  Complex Exp(Complex z) {
    return multReal(exp(z.real),new_Complex(cos(z.img),sin(z.img)));
  }
    /**
   * Méthode f.
   */
	Complex f(Complex z){
		return sum(product(z,product(z,z)),new_Complex(-1,0));
	}
  /**
   * Méthode df.
   */
	Complex df(Complex z){
	    Complex h = new_Complex(.0001,0);
	    Complex zprim = sum(z,h);
		Complex DeltaF=sum(f(zprim),multReal(-1,f(z)));
		return multReal(10000,DeltaF);
	}
	/**
   * Méthode newton de test.
   */
   Complex newton(){
	   Complex un=new_Complex(-10,-5);
	   Complex zero=new_Complex(0,0);
	   int i;
	   Complex z=un;
	   for(i=0;i<50;i++){//while(!equals(z,zero)){
		   z=sum(z,multReal(-1,div(f(z),df(z))));
		   print(z);
	   }
   }
  /**
   * Méthode principale de test.
   */
     
  void main() {
	  Complex z = new_Complex(1,2);
	  //print(Exp(z));
	  //print(f(z));
  newton();
  /*
    Complex c1 = new_Complex(1,2);
	printf("real=%f\n",c1.real);
    Complex c2 = new_Complex(3,4);
	Complex z = div(c1,c2);
    print(z);
	*/
	
	
	/*
    if(equals(c1,c1)){
		printf("oui\n");
	};
    if(equals(c1,c2)){
		printf("c1=c2\n");
	};
    Complex c3 = sum(c2,c2);
    print(c3);
    c3 = product(c2,c3);
    print(c3);
	*/
  }
