#include <time.h>

#include "ordenacao.h"

/* Implementacao do Quick Sort */
void qSortR(int* vet, int l, int r) {
	int i, j;
	if (r <= l) return; // Tamanho 1, ja ordenado.

	// Setando uma seed em relacao ao horario,
	// para garantir ordem aleatoria diferente.
	srand(time(NULL));

	// Pivo aleatorio entre as extremidades.
	i = rand() % (r - l) + l + 1;
	int pivo = vet[i];

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

	qSortR(vet, l, i - 1);
	qSortR(vet, i + 1, r);
}

void quickSortRandom(int* vet, int l, int r) {
	qSortR(vet, l, r);
}

