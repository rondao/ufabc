#include <math.h>

#include "ordenacao.h"

/* Implementacao do Heap Sort */
void heapify(int *vetor, int pai, int heapsize) {
	int fl, fr, imaior;

	// esquerdo = pai*2;
	fl = (pai << 1) + 1;
	// direito = pai*2 + 1;
	fr = fl + 1;

	while (1) {
		// Verifica quem e maior, filho esquedo ou pai.
		if ((fl < heapsize) && (vetor[fl] > vetor[pai])) imaior = fl;
		else imaior = pai;

		// Verifica quem e maior, filho direito ou o anterior.
		if ((fr < heapsize) && (vetor[fr] > vetor[imaior])) imaior = fr;

		// Se pai e o maior, heap pronto.
		if (imaior != pai) {
			int aux = vetor[pai];

			// Corrige o pai para o maior
			vetor[pai] = vetor[imaior];
			vetor[imaior] = aux;

			// Repete o heapify para o filho.
			pai = imaior;

			fl = (pai << 1) + 1;
			fr = fl + 1;
		} else break;
	}
}

void heapSort(int *vetor, int n) {
	int i;
	// Comeca a partir do primeiro no pai,
	// chama heapify para construir um heap
	// em todos os nos. (n >> 1)-1 divisão por 2.
	for (i = (n >> 1) - 1; i >= 0; i--) heapify(vetor, i, n);

	for (i = n - 1; i > 0; i--) {
		int aux = vetor[0];

		// Pega o maior e posiciona.
		vetor[0] = vetor[i];
		vetor[i] = aux;

		// Restaura o heap.
		heapify(vetor, 0, i);
	}
}
