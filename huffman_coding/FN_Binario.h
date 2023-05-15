/** DEFINIÇÃO: Bin_EscreverByte()
 * Atribui um Bit no início de um Byte
 * Byte - Byte a receber um Bit
 * Bit - Bit a ser inserido no Byte
 * Cont - Contador de quantos Bits tem no Byte
 *        Quando chegar em 8, o Byte está completo */
void Bin_EscreverByte(unsigned char* Byte, unsigned char Bit, unsigned char* Cont);

/** DEFINIÇÃO: Bin_LerByte()
 * Le um Bit específico de um Byte
 * Byte - Byte a ser lido
 * Num - Qual o Bit a ser lido no Byte
 * Retorno - O valor do Bit */
char Bin_LerByte(unsigned char Byte, unsigned char Num);

#include "FN_Binario.c"
