#include "ordenacao.h"

/* Implementacao do Insertion Sort */
void insertionSort(int vetor[], int n) {
	int i, j;
	int aux;

	for (i = 1; i < n; i++) {
		aux = vetor[i];
		j = i - 1;
		// Percorre a sub-lista deslocando o vetor.
		while ((j >= 0) && (vetor[j] > aux)) {
			vetor[j + 1] = vetor[j];
			j--;
		}
		// Encaixa v[i] na posicao certa.
		vetor[j + 1] = aux;
	}
}
