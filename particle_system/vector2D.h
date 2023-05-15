// Um vetor do R2.
struct vector2D {
       GLfloat x, y;
};
typedef struct vector2D vector2;

// Cria um novo vetor2, a partir das coordenadas 'x' e 'y'.
// O novo vetor2 eh retornado.
vector2 vector2_New(GLfloat x, GLfloat y);

// Multiplica o vetor2 'v' por um escalar 'e'.
// O vetor2 resultante eh retornado.
vector2 vector2_MultE(vector2 v, GLfloat e);

// Soma o vetor2 'v1' com o vetor2 'v2'.
// O vetor2 resultante eh retornado.
vector2 vector2_Sum(vector2 v1, vector2 v2);

// Normaliza o vetor2 'v'.
void vector2_Unity(vector2* v);

// Retorna a Norma do vetor2 'v'.
GLfloat vector2_Norm(vector2 v);

#include "vector2D.c"
