#include <stdio.h>
#include <stdlib.h>

int main()
{
    float *p,sum;
    int i,n;
    printf("Entrer le nombre des étudiants: ");
    scanf("%d",&n);
    p=(float*)malloc(n*sizeof(float));
    if(p==NULL){
        printf("Erreur d'allocation memoire");
        exit(1);
    }
    for(i=0;i<n;i++){
        printf("Entrer la note de l'élève %i: ",i);
        scanf("%f",p+i);
    }
    // Calculer la somme
    for(i=0;i<n;i++) sum+=*(p+i);
    printf("\nMoyenne=%.2f\n",sum/n);

    return 0;
}
