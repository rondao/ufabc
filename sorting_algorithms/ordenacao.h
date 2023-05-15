/*
 * ordenacao.h
 *
 *  Created on: 08/12/2010
 *      Author: rondao
 */

#ifndef ORDENACAO_H_
#define ORDENACAO_H_

void bubbleSort(int *vetor, int n);
void insertionSort(int *vetor, int n);
void selectionSort(int *vetor, int n);

void quickSort(int *vet, int l, int r);
void quickSortRandom(int* vet, int l, int r);
void heapSort(int *vetor, int n);
void mergeSort(int *vet, int n);

void heapify(int *vetor, int pai, int heapsize);
void mSort(int *vet, int *aux, int esq, int dir);
void merge(int *vet, int *aux, int esq, int meio, int dir);

#endif /* ORDENACAO_H_ */
