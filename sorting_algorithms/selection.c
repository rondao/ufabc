#include "ordenacao.h"

/* Implementacao do Selection Sort */
void selectionSort(int vetor[], int n) {
	int temp;
	int imenor, i, j;

	for (i = 0; i < n - 1; i++) {
		imenor = i;

		// Percorre o resto da lista desordenada,
		// procurando o menor elemento.
		for (j = i + 1; j < n; j++)
			if (vetor[j] < vetor[imenor])
				imenor = j;

		// Posiciona o menor no lugar certo.
		temp = vetor[i];
		vetor[i] = vetor[imenor];
		vetor[imenor] = temp;
	}
}
