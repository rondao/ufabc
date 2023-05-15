struct NoDeLista {
    int freq;
    int tipo;
    void* valor;
    struct NoDeLista* prox;
    struct NoDeLista* ant;
};
typedef struct NoDeLista LNo;

#define TIPO_CHAR 1
#define TIPO_AH 2

/** DEFINIÇÃO: ListaOrd_Criar()
 *  Retorna um ponteiro NULL */
LNo* ListaOrd_Criar();

/** DEFINIÇÃO: ListaOrd_InserirChar()
 *  Insere um nó do tipo LNo na Lista, e seu valor será um char*
 *  Lista - Ponteiro por referência da Lista a ser inserida
 *  valor - Ponteiro para o caractere a ser alterada */
void ListaOrd_InserirChar(LNo** Lista, char* valor);

/** DEFINIÇÃO: ListaOrd_InserirAH()
 *  Insere um nó do tipo LNo na Lista, e seu valor será um AH*
 *  Lista - Ponteiro por referência da Lista a ser alterada
 *  valor - Ponteiro para a estrutura AH a ser inserida
 *  Freq - A frequência que este nó terá */
void ListaOrd_InserirAH(LNo** Lista, AH* valor, int Freq);

/** DEFINIÇÃO: ListaOrd_Buscar()
 *  Busca um determinado caractere na Lista
 *  Lista - Ponteiro para a Lista a ser buscada
 *  valor - Caractere a se buscado na Lista
 *  Retorno - Ponteiro para o nó com o valor, ou NULL se não for encontrado */
LNo* ListaOrd_Buscar(LNo* Lista, char* valor);

/** DEFINIÇÃO: ListaOrd_Ordenar()
 *  Ordena a Lista por frequência em ordem crescente, apartir do nó passado
 *  No - Nó que está fora de ordem
 *  Retorno - Ponteiro para um nó. Se o início da lista foi alterado,
 *            o retorno será o primeiro nó, caso contrário NULL será retornado */
LNo* ListaOrd_Ordenar(LNo* No);

/** DEFINIÇÃO: ListaOrd_RemoverMenorFreq()
 *  Remove o nó de menor frequência da Lista (primeiro)
 *  Lista - Ponteiro por referência da Lista a ser alterada
 *  Ret - Ponteiro por referência para uma estrutura AH.
 *        Ela conterá o valor do nó de menor frequência
 *  Freq - Referência para a frequência deste nó */
void ListaOrd_RemoverMenorFreq(LNo** Lista, AH** Ret, int* Freq);

/** DEFINIÇÃO: ListaOrd_Imprimir()
 *  Imprime o valor dos nós e suas frequências
 *  Lista - Ponteiro para a Lista a ser impressa */
void ListaOrd_Imprimir(LNo* Lista);

#include "TAD_ListaOrd.c"
