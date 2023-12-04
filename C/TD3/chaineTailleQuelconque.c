#include <stdio.h> 
#include <stdlib.h>
char* saisir() { 
    char c; 
    int i=0; 
    char* s = (char*) malloc ((i+1));
    while( (c=getchar())!='?'){
        s[i++]=c; 
        s = (char*) realloc(s,i+1);
    }     
    s[i]='\0';
    return s;
    
}
void main() { 
    printf("Entrer les caracteres avec ? a la fin.\n"); 
    char* s= saisir(); 
    printf("s=%s\n",s);
    free(s); 
}