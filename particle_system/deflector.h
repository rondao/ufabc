// Um defletor.
// Reflete as particulas que os tocarem.
struct deflector{
       vector2 p1;
       vector2 p2;
       vector2 normal;
       
       GLfloat dx;
       GLfloat dy;
       GLfloat c;
};
typedef struct deflector dfl;

// Cria um novo Defletor que cobrira a reta que passa pelos pontos 'p1' e 'p2'.
// O novo Defletor eh retornado.
dfl deflector_New(vector2 p1, vector2 p2);

// Verifica se a particula 'p' sera refletida pelo defletor 'df'.
// Além de verificar, também realiza a deflexao.
void deflector_Test(dfl df, ptc* p);

#include "deflector.c"
