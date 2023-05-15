/** Inserir um Bit no início de um Byte */
void Bin_EscreverByte(unsigned char* Byte, unsigned char Bit, unsigned char* Cont) {

    // Desloca todos os Bits uma casa para a esquerda
    (*Byte) <<= 1;
    // Insere o Bit no início do Byte
    // [xxxxxxx{X}] OU [0000000{1/0}] = [xxxxxxx{1/0}]
    (*Byte) |= Bit;

    // Incrementa o número de Bits no Byte;
    (*Cont)++;
}

/** Le um Bit específico de um Byte */
char Bin_LerByte(unsigned char Byte, unsigned char Num) {

    // Desloca todos os Bits um a menos do número deste
    // Assim o Bit desejado ficará na primeira posição
    // Num = 6: [xxxxx{X}xx] <<= 5 = [{X}xx00000]
    Byte <<= Num-1;

    // Desloca todos os Bits sete casas para a direita
    // Assim o Bit desejado ficará na última posição
    // [{X}xx00000] >>= 7 = [0000000{X}]
    Byte >>= 7;

    return Byte;
}
