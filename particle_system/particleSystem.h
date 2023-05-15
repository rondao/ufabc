// Um sistema de particulas.
// Armazena as particulas criadas por este.
// Armazena os Defletores que afetaram as particulas.
struct particleSystem {
       vector2 position;    // Vetor2 posicao.
       vector2 direction;   // Vetor2 direcao para atirar as particulas. ( (0,0) atirara para qualquer lado).
       vector2 gravity;     // Vetor2 aceleracao que afetara as particulas.
       
       GLfloat velocity;    // Velocidade inicial das particulas.
       GLfloat velocityVar; // Variacao que a velocidade inicial pode ter. Valor em porcentagem.
       
       GLfloat angle;       // "Angulo" que ira variar a direcao a atirar as particulas. valor: [-1.0,1.0]
       
       GLfloat delay;       // Tempo entre um tiro e outro. Valor em milisegundos.
       GLfloat time;        // Tempo em que a ultima particula foi atirada.
       
       GLint ptcNum;        // Numero total de particulas que ja foram atiradas.
       GLint ptcTotal;      // Numero total de particulas a atirar.
       GLfloat ptcLife;     // Tempo de vida das particulas.
       GLfloat ptcLifeVar;  // Variacao do tempo de vida das particulas.
       
       ptc* particles;      // Ponteiro para as particulas controladas por este sistema.
       
       GLint dflNum;        // Numero de defletores criados.
       dfl* deflectors;     // Ponteiro para os defletores controlados por este sistema.
};
typedef struct particleSystem ptcSys;

// Cria um novo sistema de coordenadas.
// Um ponteiro para este eh retornado.
ptcSys* ptcSys_new(vector2 position,
                   vector2 direction,
                   vector2 gravity,
                   GLfloat velocity,
                   GLfloat velocityVar,
                   GLfloat angle,
                   GLfloat delay,
                   GLint ptcTotal,
                   GLfloat ptcLife,
                   GLfloat ptcLifeVar,
                   GLint deflectors);
       
// Cria uma particula e a atira.
// 'elapsedTime' eh usado para guardar quando esta particula foi atirada.
void ptcSys_Fire(ptcSys* PS, GLfloat elapsedTime);

// Atualiza todas as particulas do sistema.
// 'elapsedTime' eh usado para verificar quais particulas ja morreram.
void ptcSys_Update(ptcSys* PS, GLfloat elapsedTime);

// Desenha o sistema, todas as suas particulas e os defletores.
void ptcSys_Display(ptcSys* PS);

// Adiciona o defletor 'df' na lista de defletores do sistema 'PS'.
void ptcSys_AddDfl(ptcSys* PS, dfl df);

#include "particleSystem.c"
