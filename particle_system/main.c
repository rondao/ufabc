#include <GL/glut.h>
#include <stdlib.h>
#include <stdio.h>

#include "rnd.c"

#include "vector2D.h"
#include "particle.h"
#include "deflector.h"
#include "particleSystem.h"

void display(void);
void idle(void);
void init(void);
void update(void);
void reshape(GLint newWidth, GLint newHeight);
void keyboard(unsigned char key, int x, int y);
void mouse(int button, int state, int x, int y);

/** VARIAVEIS DE CONTROLE -- VALORES PADRAO **/
    GLfloat px = 400.0;
    GLfloat py = 300.0;
    GLfloat dx = 0.0;
    GLfloat dy = 0.0;
    GLfloat gx = 0.0;
    GLfloat gy = -0.05;
    GLfloat v = 2.0;
    GLfloat vVar = 0.5;
    GLfloat ang = 0.2;
    GLfloat del = 50;
    GLint nPtc = 1000;
    GLfloat lPtc = 10000.0;
    GLfloat lVarPtc = 0.8;
    GLfloat backColR = 1.0;
    GLfloat backColG = 1.0;
    GLfloat backColB = 1.0;
    GLfloat colR = 0.0;
    GLfloat colG = 0.0;
    GLfloat colB = 0.0;
    GLfloat ptSize = 3.0;
/** FIM VARIAVEIS **/

ptcSys* PS;

int main(int argc, char** argv){
    
/** LENDO PARAMETROS **/
    int c = 0;
    while (++c != argc){
          if ( ! strcmp(argv[c],"-p")) {
              px = atof(argv[++c]);
              py = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-d")) {
              dx = atof(argv[++c]);
              dy = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-g")) {
              gx = atof(argv[++c]);
              gy = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-v")) {
              v = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-vVar")) {
              vVar = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-ang")) {
              ang = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-del")) {
              del = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-nPtc")) {
              nPtc = atoi(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-lPtc")) {
              lPtc = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-lVarPtc")) {
              lVarPtc = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-backCol")) {
              backColR = atof(argv[++c]);
              backColG = atof(argv[++c]);
              backColB = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-col")) {
              colR = atof(argv[++c]);
              colG = atof(argv[++c]);
              colB = atof(argv[++c]);
          }
          if ( ! strcmp(argv[c],"-ptSize")) {
              ptSize = atof(argv[++c]);
          }
    }
/** FIM LENDO PARAMETROS **/

	glutInit(&argc, argv);
	
	// Modo GLUT.
	glutInitDisplayMode (GLUT_DOUBLE | GLUT_RGB);
	printf("%d \n", GLUT_RGB);
	
	// Definicoes da janela.
	glutInitWindowSize (800, 600);
	glutInitWindowPosition (0, 0);
	glutCreateWindow ("Particle System - Rafael Rondão");
	
	init();
	
	// Funcoes Callback.
	glutDisplayFunc(display);
	glutReshapeFunc (reshape); 
	glutKeyboardFunc(keyboard);
	glutMouseFunc(mouse);
	glutIdleFunc(idle);
	
	// Criando um sistema de particulas.
	PS = ptcSys_new(vector2_New(px, py),//Position.
                    vector2_New(dx, dy),    //Direction.
                    vector2_New(gx, gy),  //Gravity.
                    v,                      //Velocity.
                    vVar,                      //Velocity Variation.
                    ang,                      //"Angle".
                    del,                       //Delay.
                    nPtc,                     //NumParticles.
                    lPtc,                    //LifeParticles.
                    lVarPtc,                      //LifeParticles Variation.
                    10);                      //Deflectors.
                   
	glutMainLoop();
	return 0;
}

void init(void){
     // Cores.
	glClearColor(backColR, backColG, backColB, 1.0);
	glColor3f(colR, colG, colB);
	glPointSize(ptSize);
	
	// Matrix.
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluOrtho2D (0, 800, 0, 600 );
} 

// Cria as particulas quando for a hora.
void idle(void) {
     // Se o 'delay' for 0, todas as particulas serao criadas ao mesmo tempo.
     if ( ! PS->delay) {
          int i;
          for (i = 0; i < PS->ptcTotal; i++)
              ptcSys_Fire(PS, glutGet(GLUT_ELAPSED_TIME));
              
          // Nenhuma particula sera criada agora.
          glutIdleFunc(update);
     }
     
     // Se o tempo entre a ultima particula criada for maior que 'delay'.
     if (glutGet(GLUT_ELAPSED_TIME) - PS->time >= PS->delay) {
        // Cria uma nova particula.
        ptcSys_Fire(PS, glutGet(GLUT_ELAPSED_TIME));
        
        // Se todas as particulas ja foram criadas, mudar o Callback.
        if (PS->ptcNum == PS->ptcTotal) {
           glutIdleFunc(update);
        }
     }

     update();
}

// Atualiza todo o sistema de particulas e redesenha.
void update(void) {
     ptcSys_Update(PS, glutGet(GLUT_ELAPSED_TIME));
     glutPostRedisplay(); 
}

// Desenha todas as particulas.
void display(void){
	glClear(GL_COLOR_BUFFER_BIT);
    ptcSys_Display(PS);
	glutSwapBuffers();
}

// Controla a redimensao da janela.
void reshape(GLint newWidth, GLint newHeight) {
    glViewport (0, 0, newWidth, newHeight); 
    
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity ( ); 
	gluOrtho2D (0.0, (GLfloat) newWidth, 0.0, (GLfloat) newHeight);
	
	glutSwapBuffers();
}

// funcao Callback do mouse.
// Usado para criar Defletores
void mouse(int button, int state, int x, int y){

     static GLint x0, y0;
     static GLint creating;
     
     if (button == GLUT_LEFT_BUTTON && state == GLUT_DOWN) {
        // Se for o primeiro click.        
        if ( ! creating) {
           x0 = x;
           y0 = glutGet(GLUT_WINDOW_HEIGHT) - y;
           
           creating = 1;
        } else {
           // Cria um novo defletor.
           dfl df = deflector_New(vector2_New(x0, y0),
                                   vector2_New(x, (glutGet(GLUT_WINDOW_HEIGHT) - y)));
           
           // Adiciona este defletor ao sistema de particulas.
           ptcSys_AddDfl(PS, df);
           creating = 0;
        }
     }
}

// funcao Callback do teclado.
void keyboard(unsigned char key, int x, int y){
	switch (key) {
        // ESC - Sai do programa.
		case 27:
            exit(0);
		break;
		// D - Direciona o sistema de particulas conforme o mouse.
		case 100:
             PS->direction.x = x - PS->position.x;
             PS->direction.y = glutGet(GLUT_WINDOW_HEIGHT) - y - PS->position.y;
                
             vector2_Unity(&(PS->direction));
        break;
        // G - Direciona a gravidade conforme o mouse (Com referencia o sistema de particulas).
		case 103:
             PS->gravity.x = (x - PS->position.x) / 1000;
             PS->gravity.y = (glutGet(GLUT_WINDOW_HEIGHT) - y - PS->position.y) / 1000;
        break;
        // V - Direciona a velocidade conforme o mouse (Com referencia o sistema de particulas).
        vector2 temp;
		case 118:
             temp = vector2_New( (x - PS->position.x) / 25, (glutGet(GLUT_WINDOW_HEIGHT) - y - PS->position.y) / 25);
             PS->velocity = vector2_Norm(temp);
        break;
		// P - Posiciona o sistema de particulas conforme o mouse.
		case 112:
             PS->position.x = x;
             PS->position.y = glutGet(GLUT_WINDOW_HEIGHT) - y;             
        break;
	}
}
