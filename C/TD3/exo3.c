#include <stdio.h>
#include <stdlib.h>
#define N 3
#define M 5
int main(){
    int i;
    char** A=NULL;
    if((A=malloc(N))!=NULL){
        for(i=0;i<N;i++){
            A[i]=(char*)malloc(M);
            printf("Entrer le mot %d: ");
            scanf("%s",A[i]);
        }
    }else{
        printf("malloc failed");
        exit(EXIT_FAILLURE);
    }
    for(i=0;i<N;i++)
        printf("Mot %d = %s ",i,A[i]);
    free(A);
    return 0;
}
