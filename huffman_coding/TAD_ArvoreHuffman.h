struct ArvoreHuffman {
    char* valor;                // Caractere do Nó. NULL para não folhas
    char* codigo;               // Código do Nó
    unsigned char tam;                    // Tamanho da String 'codigo'
    struct ArvoreHuffman* esq;  // Ponteiro para o filho da esquerda
    struct ArvoreHuffman* dir;  // Ponteiro para o filho da direita
};
typedef struct ArvoreHuffman AH;

/** DEFINIÇÃO: AH_CriarNo()
 * Cria um nó do tipo AH
 * Esq - Ponteiro para uma estrutura AH. O novo nó->esq apontará para ela
 * Dir - Ponteiro para uma estrutura AH. O novo nó->dir apontará para ela
 * Retorno - Ponteiro para a estrutura AH criada */
AH* AH_CriarNo(AH* Esq, AH* Dir);

/** DEFINIÇÃO: AH_Altura()
 * Calcula a altura de uma árvore
 * raiz - Nó inicial para calcular a altura
 * Retorno - Inteiro com o valor da altura */
int AH_Altura(AH* raiz);

/** DEFINIÇÃO: AH_Codificar()
 * Identifica os códigos referentes as folhas e os atribui a estrutura
 * raiz - Nó inicial para codificar
 * Cod - String acumulando o código, até o momento formado
 * UltIndex - O índice após o último escrito, onde escrever
 * tam - O tamanho que a String Cod atingiu */
void AH_Codificar(AH* raiz, char Cod[], int UltIndex, int tam);

/** DEFINIÇÃO: AH_BuscarChar()
 * Busca o caractere na árvore, e retorna o código do mesmo
 * raiz - Nó inicial para buscar
 * valor - Caractere a ser buscado
 * Retorno - String com o código do caractere */
char* AH_BuscarChar(AH* raiz, char valor);

/** DEFINIÇÃO: AH_GravarFicheiro()
 * Grava todas as estruturas da árvore e sub-árvores num ficheiro
 * raiz - Nó inicial para gravar
 * F - Ponteiro para o ficheiro a ser gravado */
void AH_GravarFicheiro(AH* raiz, FILE* F);

/** DEFINIÇÃO: AH_LerFicheiro()
 * Lê um ficheiro e reconstroi todas as estruturas da árvore e sub-árvores
 * F - Ponteiro para o ficheiro a ser lido
 * Retorno - Ponteiro para a raiz da Árvore de Huffman */
AH* AH_LerFicheiro(FILE* F);

/** DEFINIÇÃO: AH_Imprimir()
 * Imprime o código de cada caractere em pós-ordem
 * raiz - Nó inicial para imprimir */
void AH_Imprimir(AH* raiz);

#include "TAD_ArvoreHuffman.c"
