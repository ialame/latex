#include <stdio.h>
#include <stdlib.h>
typedef struct {
    char nom[20];
    int note;
} eleve;
int main(){
    eleve* classe[25], e;
    int i=-1,j;
    do{
        printf("\nNom : "); scanf("%s",e.nom);
        if(e.nom[0]!='#'){
            printf("\nNote : "); scanf("%d",&e.note);
            i++;
            classe[i] = (eleve*) malloc (sizeof(eleve));
            *(classe[i])=e;
        }else
            break;
    } while(i<500);
    for(int j=0;j<=i;j++) // affichage
        printf("%s : %d\n", classe[j]->nom,classe[j]->note);
}