/** Cria uma nova lista com NULL */
LNo* ListaOrd_Criar() {
    return NULL;
}

/** Insere um nó com valor de um ponteiro do tipo char */
void ListaOrd_InserirChar(LNo** Lista, char* valor) {

    // Busca pelo elemento na lista
    LNo* novo = ListaOrd_Buscar((*Lista), valor);

    // Se não for encontrado ou a lista estiver vazia
    if (novo == NULL) {

        // Criando o novo nó de lista
        novo = (LNo*) malloc(sizeof (LNo));

        // Alocando memória para um char
        novo->valor = (char*) malloc(sizeof (char));
        *(char*) novo->valor = *valor;

        // Por ser novo, possui apenas 1 de frequência
        novo->freq = 1;

        // Seu tipo é (char*)
        novo->tipo = TIPO_CHAR;

        // Este nó será o primeiro da lista
        novo->prox = (*Lista);
        novo->ant = NULL;

        // Se este não for o único nó da lista
        if (novo->prox != NULL) {
            // O primeiro da lista deve apontar para o novo primeiro
            (*Lista)->ant = novo;
        }

        // Agora o primeiro elemento da lista é o novo
        (*Lista) = novo;

        return;
    }
    // Neste passo é caso o elemento já exista

    // Se este for encontrado, incrementar sua frequencia
    novo->freq++;

    // Ao incrementar a frequência, pode ser preciso reordenar
    LNo* ret = ListaOrd_Ordenar(novo);

    // Se ret for diferente de NULL o início da lista foi alterado
    // e o novo primeiro elemento é o ret
    if (ret != NULL) {
        (*Lista) = ret;
    }
}

/** Insere um nó com valor de um ponteiro do tipo AH */
void ListaOrd_InserirAH(LNo** Lista, AH* valor, int Freq) {

    // Cria um novo nó de lista
    LNo* novo = (LNo*) malloc(sizeof (LNo));

    // Passando o conteúdo deste AH por ponteiro
    novo->valor = valor;

    // Sua frequência depende da estrutura da árvore
    // Este valor é passado pelo parâmetro
    novo->freq = Freq;

    // Seu tipo é (AH*)
    novo->tipo = TIPO_AH;

    // Este nó será o primeiro da lista
    novo->prox = (*Lista);
    novo->ant = NULL;

    // Se este não for o único nó da lista
    if (novo->prox != NULL) {
        // O primeiro da lista deve apontar para o novo primeiro
        (*Lista)->ant = novo;
    }

    // Atualizando o novo início
    (*Lista) = novo;

    // Como sua frequência pode ser qualquer, pode precisar ser reordenado
    LNo* ret = ListaOrd_Ordenar(novo);

    // Se ret for diferente de NULL o início da lista foi alterado
    // e o novo primeiro elemento é o ret
    if (ret != NULL) {
        (*Lista) = ret;
    }
}

/** Busca um elemento na lista e o retorna. Retorna NULL se nada for achado */
LNo* ListaOrd_Buscar(LNo* Lista, char* valor) {

    LNo* aux = Lista;

    // Avança o aux até encontrar o valor ou este ser NULL
    while (aux != NULL) {

        // Se este nó for do tipo AH, ignorar
        if (aux->tipo == TIPO_AH) {
            aux = aux->prox;
            continue;
        }

        // Se o valor da chave for encontrado, sair do laço
        if (*(char*) aux->valor == *valor) {
            break;
        }

        aux = aux->prox;
    }

    return aux;
}

/** Reordena a lista deste ponteiro para frente */
LNo* ListaOrd_Ordenar(LNo* No) {

    LNo* ret = NULL;

    // Se este for o último nó da lista, não precisa reordenar
    if (No->prox == NULL) {
        return ret;
    }

    LNo* aux;

    // Enquanto a frequência deste for maior que o do próximo
    while (No->freq > No->prox->freq) {

        /* Trocar Posições */
        aux = No->prox;

        No->prox = aux->prox;

        aux->prox = No;
        aux->ant = No->ant;

        No->ant = aux;

        // Se este não for o primeiro elemento, ajustar o seu anterior
        // para apontar para este nó
        if (aux->ant != NULL) {
            aux->ant->prox = aux;
        } else {
            // Se este era o primeiro, então deve retornar o novo primeiro
            // para atualizar o início da lista
            ret = aux;
        }
        /* Fim Trocar Posições */

        // Se tiver algum elemento na sua frente, atualizar o seu anterior
        // Se não, este se tornou o último da lista, logo já está ordenado
        if (No->prox != NULL) {
            No->prox->ant = No;
        } else {
            break;
        }
    }

    return ret;
}

/** Remove o elemento de menor frequência da lista */
void ListaOrd_RemoverMenorFreq(LNo** Lista, AH** Ret, int* Freq) {

    LNo* rem = (*Lista);

    if ((*Lista) == NULL) {
        return;
    }

    // Avançando o início da lista
    (*Lista) = (*Lista)->prox;

    // Se a lista não conter apenas um elemento,
    // O que faria com que (*Lista) apontasse para NULL
    if ((*Lista) != NULL) {
        (*Lista)->ant = NULL;
    }

    // Se for do tipo (char*), este será uma folha
    if (rem->tipo == TIPO_CHAR) {
        // Alocando espaço na memória para esta nova folha
        (*Ret) = (AH*) malloc(sizeof (AH));

        // Logo não possui filhos
        (*Ret)->esq = NULL;
        (*Ret)->dir = NULL;

        // Alocando a memória de valor, este será um ponteiro para um char
        (*Ret)->valor = (char*) malloc(sizeof (char));

        // rem->valor guarda um char, então basta pegá-lo
        *((*Ret)->valor) = *(char*) rem->valor;
    } else {
        // Se for do tipo (AH*), basta pegar o ponteiro do nó->valor
        (*Ret) = (AH*) rem->valor;
    }

    // Passa a frequência que este nó tem
    (*Freq) = rem->freq;

    free(rem);
}

/** Imprime todos os elementos da lista */
void ListaOrd_Imprimir(LNo* Lista) {

    LNo* aux = Lista;

    printf("\nIniciando Impressao da Lista!\n");

    while (aux != NULL) {
        if (aux->tipo == TIPO_CHAR) {
            printf("Valor: %c - Freq: %d", *(char*) aux->valor, aux->freq);

            printf("\n");
        } else {
            printf("Valor: AH - Freq: %d\n", aux->freq);
        }

        aux = aux->prox;
    }
}
