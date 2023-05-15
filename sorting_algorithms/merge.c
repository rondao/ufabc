#include <stdio.h>
#include <stdlib.h>

#include "ordenacao.h"

/* Implementacao do Merge Sort */
void mSort(int *vet, int *aux, int esq, int dir) {
	if (dir <= esq) return; // Tamanho 1, ja esta ordenado.
	int meio = (dir + esq) / 2;

	mSort(vet, aux, esq, meio); // Primeira chamada
	mSort(vet, aux, meio + 1, dir); // Segunda chamada

	merge(vet, aux, esq, meio, dir); // Combina as metades

}

void merge(int* vet, int* aux, int esq, int meio, int dir) {
	int i, j, k;

	i = k = esq;
	j = meio + 1;

	// Avanca o ponteiro simultaneamente pelas duas listas,
	// e intercala o menor deles.
	while ((i <= meio) && (j <= dir)) {
		if (vet[i] < vet[j]) aux[k++] = vet[i++];
		else aux[k++] = vet[j++];
	}

	// Se uma das listas ja foi intercalada,
	// basta encaixar o resto da outra.
	while (i <= meio) aux[k++] = vet[i++];
	while (j <= dir) aux[k++] = vet[j++];

	while (esq <= dir) vet[esq] = aux[esq++];
}

void mergeSort(int *vet, int n) {
	int *aux = (int*) malloc(n*sizeof(int));
	mSort(vet, aux, 0, n - 1);
	free(aux);
}
