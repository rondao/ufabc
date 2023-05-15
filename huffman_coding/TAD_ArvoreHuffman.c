/** Cria um nó de AH, recebendo seus valores para a Esq e Dir */
AH* AH_CriarNo(AH* Esq, AH* Dir) {

    AH* pai = (AH*) malloc(sizeof (AH));

    // Este nó não é uma folha, logo não precisa de valor
    pai->valor = NULL;

    pai->esq = Esq;
    pai->dir = Dir;

    return pai;
}

/** Retorna a altura da árvore */
int AH_Altura(AH* raiz) {

    // Condição de parada
    if (raiz == NULL) {
        return 0;
    }

    // Variáveis para as alturas dos filhos
    int h_esq;
    int h_dir;

    // Chama recursivamente para os filhos
    h_esq = AH_Altura(raiz->esq);
    h_dir = AH_Altura(raiz->dir);

    return h_esq > h_dir ? h_esq + 1 : h_dir + 1;
}

/** Varre toda a árvore para identificar os códigos das folhas */
// char Cod[] guardará a posição do nó - Esq = 0, Dir = 1
// tam armazena o tamanho da string a ser alocado na memória

void AH_Codificar(AH* raiz, char Cod[], int UltIndex, int tam) {

    // Se o valor deste nó for diferente de NULL,
    // Este nó é uma folha, e deve ser codificado
    if (raiz->valor != NULL) {
        // Adicinando o caractere de fim de string
        Cod[UltIndex] = '\0';

        // Alocando memória para a string do código
        raiz->codigo = (char*) malloc(tam * sizeof (char));
        strcpy(raiz->codigo, Cod);

        // Armazena o tamanho desta string
        raiz->tam = tam;

        return;
    }

    // Chamando recursivamente para os filhos
    // A posição será 0, pois está chamando para o filho da esquerda
    Cod[UltIndex] = '0';
    AH_Codificar(raiz->esq, Cod, UltIndex + 1, tam + 1);


    // A posição será 1, pois está chamando para o filho da direita
    Cod[UltIndex] = '1';
    AH_Codificar(raiz->dir, Cod, UltIndex + 1, tam + 1);
}

/** Busca um caractere específico na árvore e retorna o seu código */
char* AH_BuscarChar(AH* raiz, char valor) {

    // Se o valor deste nó for diferente de NULL,
    // Este nó é uma folha, e é preciso checar o valor
    if (raiz->valor != NULL) {
        if (*(raiz->valor) == valor) {
            return raiz->codigo;
        }

        return NULL;
    }

    // Guarda o resultado da busca pela esquerda
    char* aux;
    aux = AH_BuscarChar(raiz->esq, valor);

    // Se este for NULL, não foi encontrado na esquerda
    if (aux == NULL) {
        aux = AH_BuscarChar(raiz->dir, valor);
    }

    return aux;
}

/** Grava a árvore e suas sub-árvores em um ficheiro */
void AH_GravarFicheiro(AH* raiz, FILE* F) {

    // Caractere de sinalização para a descompactação
    char ch;

    // Se esta for uma folha
    if (raiz->valor != NULL) {
        // Gravando um sinal (subir para o pai);
        ch = -1;
        fwrite(&ch, sizeof (char), 1, F);

        // Gravando o seu valor
        fwrite(raiz->valor, sizeof (char), 1, F);

        return;
    }

    // Chamando recursivamente para os possíveis filhos
    if (raiz->esq != NULL) {
        // Gravando um sinal (filho da esquerda [0])
        ch = 0;
        fwrite(&ch, sizeof (char), 1, F);

        // Chamando para gravar o filho
        AH_GravarFicheiro(raiz->esq, F);
    }
    if (raiz->dir != NULL) {
        // Gravando um sinal (filho da esquerda [1])
        ch = 1;
        fwrite(&ch, sizeof (char), 1, F);

        // Chamando para gravar o filho
        AH_GravarFicheiro(raiz->dir, F);
    } else {
        // Gravando um sinal (subir para o pai);
        ch = -1;
        fwrite(&ch, sizeof (char), 1, F);
    }
}

/** Lê um ficheiro e re constroi a árvore e suas sub-árvores */
AH* AH_LerFicheiro(FILE* F) {

    // Caractere para ler os sinais no ficheiro
    char ch;

    // Alocando a memória
    AH* pai = (AH*) malloc(sizeof (AH));

    // Campos agora desnecessários
    pai->codigo = NULL;
    pai->tam = 0;

    // Campo a ser alterado, se for uma folha
    pai->valor = NULL;

    // Lendo o caractere de sinalização
    fread(&ch, sizeof (char), 1, F);

    // Se seu valor for -1. Este é um nó folha
    if (ch == -1) {
        // Não possui filhos
        pai->esq = NULL;
        pai->dir = NULL;

        // Alocando memória para o seu valor
        pai->valor = (char*) malloc(sizeof (char));
        // Lendo o valor deste nó
        fread(pai->valor, sizeof (char), 1, F);

        // Voltar na recursão, retornando o novo nó
        return pai;
    }

    // Se seu valor for 0. Este nó possui filho na esquerda
    if (ch == 0) {
        // Chamando recursivamente para este novo filho
        pai->esq = AH_LerFicheiro(F);
    }

    // Lendo mais um sinal para ver se tem filho na direita
    fread(&ch, sizeof (char), 1, F);

    // Se seu valor for 1. Este nó possui filho na direita
    if (ch == 1) {
        // Chamando recursivamente para este novo filho
        pai->dir = AH_LerFicheiro(F);
    }

    // Seu valor aqui podia ser -1
    // O que significaria voltar na recursão. O que já vai ocorrer

    // Retornando o novo nó
    return pai;
}

/** Imprime as folhas desta árvore e sub-árvores, em pós-ordem */
void AH_Imprimir(AH* raiz) {

    // Se o valor deste nó for diferente de NULL,
    // Este nó é uma folha, e apenas precisa ser impresso
    if (raiz->valor != NULL) {
        // Se o código não estiver definido
        if (raiz->codigo == NULL) {
            printf("Valor: %c\n", *(raiz->valor));
        } else {
            printf("Codigo: %s - Valor: %c\n", raiz->codigo, *(raiz->valor));
        }
        return;
    }

    // Chamando recursivamente para os filhos
    AH_Imprimir(raiz->esq);
    AH_Imprimir(raiz->dir);
}

