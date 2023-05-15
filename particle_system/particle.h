// Uma particula.
struct particle {
       vector2 position;  // vetor posicao atual.
       vector2 velocity;  // vetor velocidade atual.
       
       GLfloat timeBorn;  // tempo atual do programa quando foi criada.
       GLfloat lifeTotal; // tempo de vida.
       
       GLint isAlive;     // se a particula ainda esta viva.
};
typedef struct particle ptc;

// Cria uma nova particula de posicao 'position', velocidade 'velocity',
// tempo do nascimento 'timeBorn' e tempo de vida 'lifeTotal'.
// A particula criada eh retornada.
ptc ptc_New(vector2 position,
            vector2 velocity,
            GLfloat timeBorn,
            GLfloat lifeTotal);

// Atualiza a particula 'p', movimentando-a conforme a aceleracao 'gravity'.
// 'elapsedTime' eh usado para atualizar o tempo de vida da particula.
// Retorna 1 caso a particula tenha morrido, e 0 caso contrario.            
GLint ptc_Update(ptc* p, GLfloat elapsedTime, vector2 gravity);

#include "particle.c"
