#include <time.h>

#include "ordenacao.h"

/* Implementacao do Quick Sort */
void qSort(int* vet, int l, int r) {
	int i, j;
	if (r <= l) return; // Tamanho 1, ja ordenado.

	i = l;
	int pivo = vet[i]; // Pivo sendo o primeiro elemento.

	vet[i] = vet[l];
	vet[l] = pivo;

	i = l;
	j = r;

	// Repitido ate i e j cruzarem.
	while (1) {
		// Encontra alguem do lado errado do pivo.
		while ((j > i) && (vet[j] >= pivo)) j--;

		// Se i e j nao cruzaram ainda, troque-os.
		if (i == j) break;
		vet[i] = vet[j];
		i++;

		// Encontra alguem do lado errado do pivo.
		while ((i < j) && (vet[i] <= pivo)) i++;

		// Se i e j nao cruzaram ainda, troque-os.
		if (i == j) break;
		vet[j] = vet[i];
		j--;
	}

	vet[i] = pivo;

	qSort(vet, l, i - 1);
	qSort(vet, i + 1, r);
}

void quickSort(int* vet, int l, int r) {
	qSort(vet, l, r);
}

