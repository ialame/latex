#include <stdio.h> 
void miroir() { 
    char s[20],c; 
    int i=0; 
    while( (c=getchar())!='?') 
        s[i++]=c; 
    s[i]='\0'; 
    while (--i>=0) 
        putchar (s[i]); 
}
void main() { 
    printf("Entrer les caracteres avec ? a la fin.\n"); 
    miroir(); 
    
}