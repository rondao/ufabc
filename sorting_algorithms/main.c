#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "ordenacao.h"

int* criaVetorEntradaCrescente(int size);
int* criaVetorEntradaDecrescente(int size);
int* criaVetorAleatorio(int size);

void imprimeVetor(int v[], int size);

void testar(int* entrada, int tam);

int main(int argc, char** argv) {
	int* vetorEntrada;

	int tam;

	if (argc < 3) {
		printf("ERRO: \n");
		printf("Usage: programa tamanho (-c|-d|-r) [vezes] ");
		return -1;
	}

	tam = atoi(argv[1]);
	char* ord = argv[2];

	printf("\nComecei!\n");
	printf("Tamanho: %d\n", tam);
	printf("Forma de ordenação %s\n", ord);

	// Entrada aleatória (-r)
	if (strcmp(ord, "-r") == 0) {
		printf("\nÉ entrada aleatória\n");
		if (argc != 4) {
			printf("ERRO");
			printf("Usage: programa tamanho (-c|-d|-r) [vezes] ");
			return -1;
		}

		int i;
		int vezes = atoi(argv[3]);

		printf("\nTamanho %d\n", tam);
		printf("Vai rodar %d vezes\n", vezes);

		for (i = 0; i < vezes; i++) {
			vetorEntrada = criaVetorAleatorio(tam);
			testar(vetorEntrada, tam);
		}
	} else
	// Entrada Crescente (-c)
	if (strcmp(ord, "-c") == 0) {
		vetorEntrada = criaVetorEntradaCrescente(tam);
		testar(vetorEntrada, tam);
	} else
	// Entrada Decrescente (-d)
	if (strcmp(ord, "-d") == 0) {
		vetorEntrada = criaVetorEntradaDecrescente(tam);
		testar(vetorEntrada, tam);
	} else
		printf("ERRO: formato de ordenação não especificado");

	free(vetorEntrada);

	printf("SUCESSO\n");
	return 1;
}

void testar(int* entrada, int tam) {
	// Vetor para ordenar.
	int* temp = (int*) malloc(tam * sizeof(int));

	memcpy(temp, entrada, tam * sizeof(int)); // Resetando o temp.
	insertionSort(temp, tam);

	memcpy(temp, entrada, tam * sizeof(int));
	selectionSort(temp, tam);

	memcpy(temp, entrada, tam * sizeof(int));
	bubbleSort(temp, tam);

	memcpy(temp, entrada, tam * sizeof(int));
	mergeSort(temp, tam);

	memcpy(temp, entrada, tam * sizeof(int));
	quickSort(temp, 0, tam);

	memcpy(temp, entrada, tam * sizeof(int));
	quickSortRandom(temp, 0, tam);

	memcpy(temp, entrada, tam * sizeof(int));
	heapSort(temp, tam);

	free(temp);
}

int* criaVetorEntradaCrescente(int size) {
	int* v;
	v = (int*) malloc(size * sizeof(int));
	int i;
	for (i = 0; i < size; i++) {
		v[i] = 2 * i;
	}
	return v;
}

int* criaVetorEntradaDecrescente(int size) {
	int* v;
	v = (int*) malloc(size * sizeof(int));
	int i;
	for (i = 0; i < size; i++) {
		v[i] = size - 2 * i;
	}
	return v;
}

int* criaVetorAleatorio(int size) {
	srand(time(NULL));
	int* v = (int*) malloc(size * sizeof(int));
	int i;
	for (i = 0; i < size; i++) {
		v[i] = rand() % size;
	}
	return v;
}

void imprimeVetor(int v[], int size) {
	int i;
	for (i = 0; i < size; i++) {
		printf("%d. %d\n", i, v[i]);
	}
}
