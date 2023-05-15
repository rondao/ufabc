#include "ordenacao.h"

/*Implementacao do Bubble Sort */
void bubbleSort(int v[], int n) {
	int i, j, aux;

	for (i = n; i >= 1; i--) {
		// Olhamos ate a posicao i,
		// pois o resto ja esta ordenado.
		for (j = 1; j < i; j++) {
			if (v[j - 1] > v[j]) {
				aux = v[j];
				v[j] = v[j - 1];
				v[j - 1] = aux;
			}
		}
	}
}
