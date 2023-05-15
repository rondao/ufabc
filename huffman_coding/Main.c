#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "TAD_ArvoreHuffman.h"
#include "TAD_ListaOrd.h"
#include "FN_Binario.h"

void CompactarFicheiro(char* Dir);
void DescompactarFicheiro(char* Dir);

unsigned char debug = 0;

int main(int argc, char** argv) {

    if (argc > 1) {
        if (strcmp(argv[1], "-debug") == 0) {
            printf("\n");
            printf("[]--------------------------------[]\n");
            printf("||           MODO DEBUG           ||\n");
            printf("[]--------------------------------[]\n");
            printf("\n");

            debug = 1;
        }
    }

    /* ------------------------------------------------------------------- */
    /* --------------------------- INICIO MENU --------------------------- */
    /* ------------------------------------------------------------------- */

    printf("\n");
    printf("[]--------------------------------[]\n");
    printf("||                                ||\n");
    printf("||      ALGORITMO DE HUFFMAN      ||\n");
    printf("||                                ||\n");
    printf("[]--------------------------------[]\n");
    printf("\n");
    printf("[]--------------------------------[]\n");
    printf("|| Escolha uma opcao:             ||\n");
    printf("|| 1 - Compactar ficheiro         ||\n");
    printf("|| 2 - Descompactar ficheiro      ||\n");
    printf("|| 0 - Sair                       ||\n");
    printf("[]--------------------------------[]\n");
    printf("\n");

    printf("Opcao: ");

    // Lendo um char de 'stdin' (Standard Input Stream) (Teclado)
    char Op = getc(stdin);

    // Limpando o Buffer de 'stdin' (Não funcionando!)
    //fflush(stdin);

    // Pegando o resto presente 'stdin', no caso o 'Enter'
    // fflush() deveria limpar este resto
    getc(stdin);

    if (Op != '1' && Op != '2') {
        return EXIT_SUCCESS;
    }

    printf("\n");
    printf("[]--------------------------------[]\n");
    printf("|| Digite o diretorio do ficheiro ||\n");
    printf("[]--------------------------------[]\n");
    printf("\n");

    printf("____________________________________\r");

    char* Dir = (char*) malloc(200 * sizeof (char));
    gets(Dir);

    printf("\n");

    if (Op == '1') {

        printf("[]---------------------------------[]\n");
        printf("|| Compactando o Arquivo...        ||\n");
        printf("[]---------------------------------[]\n\n");

        CompactarFicheiro(Dir);

        printf("[]---------------------------------[]\n");
        printf("|| Arquivo Compactado com Sucesso! ||\n");
        printf("[]---------------------------------[]\n\n");
    }

    if (Op == '2') {

        printf("[]------------------------------------[]\n");
        printf("|| Descompactando o Arquivo...        ||\n");
        printf("[]------------------------------------[]\n\n");

        DescompactarFicheiro(Dir);

        printf("[]------------------------------------[]\n");
        printf("|| Arquivo Descompactado com Sucesso! ||\n");
        printf("[]------------------------------------[]\n\n");
    }

    /* ------------------------------------------------------------------ */
    /* ---------------------------- FIM MENU ---------------------------- */
    /* ------------------------------------------------------------------ */

    return (EXIT_SUCCESS);
}

void CompactarFicheiro(char* Dir) {

    /* ------------------------------------------------------------------ */
    /* ---------------------- INICIO CRIANDO LISTA ---------------------- */
    /* ------------------------------------------------------------------ */

    LNo* Lista = ListaOrd_Criar();

    char ch;

    FILE *Fent;
    /* Abre um arquivo - r = read mode, t = text mode */
    Fent = fopen(Dir, "rt");

    // Deixando apenas o diretório do ficheiro
    char* Aux = Dir;

    // Avançando até chegar no fim da String
    while ((*Aux) != '\0')
        Aux++;

    // Retornando até chegar numa barra invertida (Fim do diretório)
    while ((*Aux) != '\\') {
        Aux--;
    }

    // Avançando uma posição para manter a barra
    Aux++;
    // Alterando o fim da String para esta posição
    (*Aux) = '\0';

    if (Fent == NULL) {
        printf("Nao foi possivel abrir arquivo especificado\n");
        exit(1);
    }

    /* Lê caractere por caractere para incluir na lista*/
    // Se este for diferente de EOF(End of File) o arquivo ainda não acabou
    while ((ch = fgetc(Fent)) != EOF) {
        ListaOrd_InserirChar(&Lista, &ch);

        // --------------------- DEBUG ----------------------
        if (debug) {
            printf("\nInserindo caractere: %c", ch);
            ListaOrd_Imprimir(Lista);
            getc(stdin);
        }
        // --------------------- DEBUG ----------------------
    }

    /* ----------------------------------------------------------------- */
    /* ----------------------- FIM CRIANDO LISTA ----------------------- */
    /* ----------------------------------------------------------------- */

    // --------------------- DEBUG ----------------------
    if (debug) {
        printf("\nLista Completa!\n");
        getc(stdin);
    }
    // --------------------- DEBUG ----------------------

    /* ------------------------------------------------------------------ */
    /* ---------------- INICIO CRIANDO ÁRVORE DE HUFFMAN ---------------- */
    /* ------------------------------------------------------------------ */
    // A raiz final da Árvore de Huffman
    AH* ArvHuffman;

    while (1) {

        // Criando os ponteiros para os nós de árvore
        AH* Esq;
        AH* Dir;

        // Criando variáveis para guardar a frequência dos nós da lista
        int EsqFreq;
        int DirFreq;

        // Pegando os elementos de menor frequência para criar um nó de árvore
        ListaOrd_RemoverMenorFreq(&Lista, &Dir, &DirFreq);
        ListaOrd_RemoverMenorFreq(&Lista, &Esq, &EsqFreq);

        // --------------------- DEBUG ----------------------
        if (debug) {
            printf("\nRemovendo os dois elementos de menor frequencia.\n");
            if (Dir->valor == NULL)
                printf("Menor frequencia: AH\n");
            else
                printf("Menor frequencia: %c\n", *(Dir->valor));
            if (Esq->valor == NULL)
                printf("Menor frequencia: AH\n");
            else
                printf("Menor frequencia: %c\n", *(Esq->valor));
            printf("Pai criado e reinserido na lista!\n");
            getc(stdin);
        }
        // --------------------- DEBUG ----------------------

        // Criando o novo nó de árvore que será o pai dos dois anteriores
        ArvHuffman = AH_CriarNo(Esq, Dir);

        // Se a Lista estiver vazia, a árvore está pronta
        if (Lista == NULL) {
            break;
        }

        // Este nó árvore é inserido na lista novamente
        ListaOrd_InserirAH(&Lista, ArvHuffman, EsqFreq + DirFreq);

        // --------------------- DEBUG ----------------------
        if (debug) {
            ListaOrd_Imprimir(Lista);
            getc(stdin);
        }
        // --------------------- DEBUG ----------------------
    }

    // Vendo a altura da árvore
    int h = AH_Altura(ArvHuffman);

    // Criando um vetor de char para armazenar os códigos dos elementos
    // Exemplo: 000 - A, 010 - B, 100 - C;
    char Cod[h + 1];

    // O tamanho começa em 2 para que se tiver apenas um elemento,
    // o primeiro caractere é o valor do nó, e o segundo é o fim de string
    AH_Codificar(ArvHuffman, Cod, 0, 2);

    // --------------------- DEBUG ----------------------
    if (debug) {
        printf("\nImprimindo Arvore de Huffman!\n");
        AH_Imprimir(ArvHuffman);
        getc(stdin);
    }
    // --------------------- DEBUG ----------------------

    /* ------------------------------------------------------------------- */
    /* ------------------ FIM CRIANDO ÁRVORE DE HUFFMAN ------------------ */
    /* ------------------------------------------------------------------- */

    // --------------------- DEBUG ----------------------
    if (debug) {
        printf("\nArvore de Huffman Criada!\n\n");
        getc(stdin);
    }
    // --------------------- DEBUG ----------------------

    /* ------------------------------------------------------------------- */
    /* ---------------- INICIO CRIANDO ARQUIVO COMPACTADO ---------------- */
    /* ------------------------------------------------------------------- */

    // Voltando o ponteiro Fent, para seu início
    // 0 Bytes a frente do início (SEEK_SET)
    fseek(Fent, 0, SEEK_SET);

    // Abrindo o arquivo de saida para escrever a compressão
    FILE *Fsai;
    /* Abre um arquivo - w = write mode*/
    // strcat() concatena o diretório com o nome do ficheiro
    Fsai = fopen(strcat(Dir, "ArqCompactado.rz"), "w");

    // Gravando a Árvore de Huffman no ficheiro
    AH_GravarFicheiro(ArvHuffman, Fsai);

    // Armazena os bytes a serem gravados
    // Iniciado como 1 para que << tenha efeito
    unsigned char Byte = 1;
    // Armazena o valor do código convertido em número, os bits
    unsigned char Bit = 0;
    // Conta quantos bits já foram inseridos em 'Byte'
    unsigned char Cont = 0;
    // Ponteiro para a string com o código do elemento
    char* pCod;
    // Recebe o caractere atual de pCod
    char chCod;

    /* Lê caractere por caractere a entrada*/
    // Se este for diferente de EOF(End of File) o arquivo ainda não acabou
    while ((ch = fgetc(Fent)) != EOF) {
        // Buscando o código deste caractere na árvore
        pCod = AH_BuscarChar(ArvHuffman, ch);

        // Se o caractere atual do Cod for diferente de NULL,
        // o código ainda não acabou
        while ((*pCod) != '\0') {

            chCod = (*pCod);

            // '0' = 48 e '1' = 49
            // Logo, subtraindo 48 deles, teremos o seu valor em decimal
            Bit = chCod - 48;

            // Escreve o Bit no início do Byte
            Bin_EscreverByte(&Byte, Bit, &Cont);

            // Se o Byte tiver 8 Bits. Está pronto para ser escrito
            if (Cont == 8) {
                // Escrevendo o Byte, com o tamanho de sizeof(char) (1 Byte),
                // com quantidade 1, no ficheiro Fsai
                fwrite(&Byte, sizeof (unsigned char), 1, Fsai);

                // --------------------- DEBUG ----------------------
                if (debug) {
                    printf("Byte gravado! Valor numerico: %d\n", Byte);
                    getc(stdin);
                }
                // --------------------- DEBUG ----------------------

                // Contador volta para 0
                Cont = 0;
                // Limpa o Byte. Para 1 assim << tem efeito
                Byte = 1;
            }

            // Avança o ponteiro 'pCod' pela sua String
            pCod++;
        }
    }

    fclose(Fent);

    // Aqui, todo o arquivo de entrada já foi lido
    // Porém, se o último Byte formado não tiver 8 Bits
    // ainda precisará ser gravado;
    // Isso acontecerá quando Cont for diferente de 0
    // pois após a gravação, Cont assume este valor
    if (Cont != 0) {
        fwrite(&Byte, sizeof (unsigned char), 1, Fsai);

        // --------------------- DEBUG ----------------------
        if (debug) {
            printf("Byte gravado! Valor numerico: %d\n", Byte);
            getc(stdin);
        }
        // --------------------- DEBUG ----------------------
    }

    // Porém, parte deste Byte é lixo, ou não. Logo, é gravado um outro Byte
    // com o número de Bits lixo gravados
    // Este valor é quanto falta para Cont completar 8
    // Agora se Cont for 0, nenhum Bit lixo foi gravado
    if (Cont == 0) {
        Byte = 0;
        fwrite(&Byte, sizeof (unsigned char), 1, Fsai);
    } else {
        Byte = 8 - Cont;
        fwrite(&Byte, sizeof (unsigned char), 1, Fsai);
    }

    // --------------------- DEBUG ----------------------
    if (debug) {
        printf("Byte final. Numero de Bits indesejados: %d\n", Byte);
        getc(stdin);
    }
    // --------------------- DEBUG ----------------------

    fclose(Fsai);

    /* ------------------------------------------------------------------ */
    /* ----------------- FIM CRIANDO ARQUIVO COMPACTADO ----------------- */
    /* ------------------------------------------------------------------ */
}

void DescompactarFicheiro(char* Dir) {

    /* ------------------------------------------------------------------- */
    /* ---------------------- INICIO DESCOMPACTANDO ---------------------- */
    /* ------------------------------------------------------------------- */

    // Ponteiro de ficheiro que terá o arquivo compactado
    FILE* Fent;
    // Ponteiro de ficheiro que criará o arquivo descompactado
    FILE* Fsai;

    /* Abre um arquivo - r = read mode - b = binary mode*/
    Fent = fopen(Dir, "rb");

    if (Fent == NULL) {
        printf("Nao foi possivel abrir arquivo especificado\n");
        exit(1);
    }

    // Deixando apenas o diretório do ficheiro
    char* Aux = Dir;

    // Avançando até chegar no fim da String
    while ((*Aux) != '\0')
        Aux++;

    // Retornando até chegar numa barra invertida (Fim do diretório)
    while ((*Aux) != '\\') {
        Aux--;
    }

    // Avançando uma posição para manter a barra
    Aux++;
    // Alterando o fim da String para esta posição
    (*Aux) = '\0';

    /* Abre um arquivo - w = write mode - t = text mode */
    Fsai = fopen(strcat(Dir, "ArqDescompactado.txt"), "wt");

    // Variável para ler o número de Bits lixo
    unsigned char BitsLixo = 0;

    // Primeiramente, iremos ler o último Byte do ficheiro
    // para ver o número de Bits lixo
    // Movendo o ponteiro 'Fent' sizeof (char) Bytes a partir
    // do fim do arquivo (SEEK_END)
    // E este movimento é para trás (para o início do arquivo)
    // Devido ao sinal de menos no Offset
    fseek(Fent, -sizeof (char), SEEK_END);

    // Lendo o último Byte
    fread(&BitsLixo, sizeof (unsigned char), 1, Fent);

    // --------------------- DEBUG ----------------------
    if (debug) {
        printf("Numero de Bits indesejados: %d\n", BitsLixo);
        getc(stdin);
    }
    // --------------------- DEBUG ----------------------

    // Voltando o ponteiro 'Fent' dois Bytes para trás
    // Assim apontará para o início do último Byte de arquivo
    fseek(Fent, -2 * sizeof (char), SEEK_END);

    // Guardemos esta posição
    // Variável para guardar uma posição dentro de um arquivo (long)
    fpos_t fposUltByte;
    // Lendo a posição atual de Fent em fposUltByte
    fgetpos(Fent, &fposUltByte);

    // Voltando o ponteiro 'Fent' para o início do arquivo
    // 0 de Offset, do início (SEEK_SET)
    fseek(Fent, 0, SEEK_SET);

    // Remontando a Árvore de Huffman, a partir do ficheiro
    AH* ArvHuffman = AH_LerFicheiro(Fent);

    // Neste ponto 'Fent' está apontando para o início
    // do primeiro Byte do arquivo compactado

    // Variável que receberá os Bytes lidos do ficheiro
    unsigned char Byte;
    // Variável que contará o número de Bits lidos do Byte
    unsigned char Num = 1;
    // Variável que terá o valor do Bit lido
    unsigned char Bit;
    // Ponteiro que percorrerá a Árvore de Huffman
    AH* NoAtual = ArvHuffman;

    // Variável para guardar a posição atual de Fent
    // enquanto percorre o ficheiro
    fpos_t fposAtual;
    // Lendo a posição de 'Fent' na variável 'fposAtual'
    fgetpos(Fent, &fposAtual);

    // Dentro deste laço, 'Fent' irá lendo o ficheiro
    // até sua posição coincidir com 'fposUltByte', neste
    // ponto chegamos no último Byte do arquivo compactado
    // e o trataremos de maneira diferente
    while (fposAtual != fposUltByte) {

        // Lendo um Byte
        fread(&Byte, sizeof (unsigned char), 1, Fent);

        // Lendo todo o Byte. Laço repetido 8 vezes
        while (Num < 9) {
            // Lendo um Bit, de posição Num
            Bit = Bin_LerByte(Byte, Num);

            // Aumentando o Número de Bits no Byte
            Num++;

            // Se seu valor for 0, devemos percorrer a árvore pela esquerda
            if (Bit == 0)
                NoAtual = NoAtual->esq;

            // Se seu valor for 1, devemos percorrer a árvore pela esquerda
            if (Bit == 1)
                NoAtual = NoAtual->dir;

            // --------------------- DEBUG ----------------------
            if (debug) {
                printf("Valor do Bit: %d\n", Bit);
                getc(stdin);
            }
            // --------------------- DEBUG ----------------------

            // Se o 'NoAtual' for uma folha, chegamos em um caractere
            // e devemos descompactá-lo
            if (NoAtual->valor != NULL) {
                // Escrevendo o arquivo descompactado
                fputc(*(NoAtual->valor), Fsai);

                // --------------------- DEBUG ----------------------
                if (debug) {
                    printf("Caractere a ser gravado: %c\n\n", *(NoAtual->valor));
                    getc(stdin);
                }
                // --------------------- DEBUG ----------------------

                // Ao encontrar uma folha, devemos voltar ao início
                NoAtual = ArvHuffman;
            }
        }
        // Aqui, um Byte inteiro foi lido. Voltando o contador para o início
        Num = 1;

        // Fent se deslocou pelo ficheiro
        // Atualizando sua posição em 'fposAtual'
        fgetpos(Fent, &fposAtual);
    }
    // Agora falta apenas ler o Byte final e desprezar os Bits Lixo

    // Lendo o último Byte
    fread(&Byte, sizeof (unsigned char), 1, Fent);

    // BitsLixo será incrementado, para apenas ler os Bits apartir deste valor
    // Ex.: BitsLixo = 5 [LLLLLxxx] <<= 6 = [xxx00000]
    while (BitsLixo != 8) {

        // Avançando no número do Bits a ser lido
        BitsLixo++;

        // Lendo um Bit, de posição BitsLixo
        Bit = Bin_LerByte(Byte, BitsLixo);

        // Se seu valor for 0, devemos percorrer a árvore pela esquerda
        if (Bit == 0)
            NoAtual = NoAtual->esq;

        // Se seu valor for 1, devemos percorrer a árvore pela esquerda
        if (Bit == 1)
            NoAtual = NoAtual->dir;

        // --------------------- DEBUG ----------------------
        if (debug) {
            printf("Valor do Bit: %d\n", Bit);
            getc(stdin);
        }
        // --------------------- DEBUG ----------------------

        // Se o 'NoAtual' for uma folha, chegamos em um caractere
        // e devemos descompactá-lo
        if (NoAtual->valor != NULL) {
            // Escrevendo o arquivo descompactado
            fputc(*(NoAtual->valor), Fsai);

            // --------------------- DEBUG ----------------------
            if (debug) {
                printf("Caractere a ser gravado: %c\n\n", *(NoAtual->valor));
                getc(stdin);
            }
            // --------------------- DEBUG ----------------------

            // Ao encontrar uma folha, devemos voltar ao início
            NoAtual = ArvHuffman;
        }
    }

    fclose(Fent);

    /* -------------------------------------------------------------------- */
    /* ------------------------ FIM DESCOMPACTANDO ------------------------ */
    /* -------------------------------------------------------------------- */
}
