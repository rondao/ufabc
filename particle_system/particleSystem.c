ptcSys* ptcSys_new(vector2 position, vector2 direction, vector2 gravity, GLfloat velocity, GLfloat velocityVar, GLfloat angle, GLfloat delay, GLint ptcTotal, GLfloat ptcLife, GLfloat ptcLifeVar, GLint deflectors) {
     ptcSys* PS = (ptcSys*) malloc(sizeof(ptcSys));
        
     PS->position = position;
     PS->direction = direction;
     PS->gravity = gravity;
     
     PS->velocity = velocity;
     PS->velocityVar = velocityVar;
     
     PS->angle = angle;
        
     PS->delay = delay;
     // Tempo do primeiro tiro eh 0.
     PS->time = 0;
        
     // Nenhuma particula foi disparada ateh o momento.   
     PS->ptcNum = 0;
     PS->ptcTotal = ptcTotal;
     PS->ptcLife = ptcLife;
     PS->ptcLifeVar = ptcLifeVar;
     
     PS->particles = (ptc*) malloc(ptcTotal * sizeof(ptc));
     
     // Nenhum defletor criado ateh o momento.
     PS->dflNum = 0;
     PS->deflectors = (dfl*) malloc(deflectors * sizeof(dfl));
     
     // Se o vetor direcao nao for nulo, este sera normalizado.
     // Caso este seja nulo. Ao sofrer a variacao do "angulo", ira
     // Disparar para todas as direcoes.
     if (PS->direction.x != 0.0 || PS->direction.y != 0.0)
        vector2_Unity(&(PS->direction));
     
     return PS;
}

void ptcSys_Fire(ptcSys* PS, GLfloat elapsedTime) {
     // Direcao a ser disparada a particula.
     vector2 direction = PS->direction;
     // A direcao sofre um desvio, dado pelo "angulo".
     direction.x += rnd_FloatMinMax(-PS->angle, PS->angle);
     direction.y += rnd_FloatMinMax(-PS->angle, PS->angle);
     // A direcao eh, entao, normalizada.
     vector2_Unity(&direction);

     // A velocidade eh a direcao multiplicado pelo escalar 'velocity',
     // e o resultado multiplicado pela variacao.
     vector2 velocity = vector2_MultE(vector2_MultE(direction, PS->velocity),
                   rnd_FloatMinMax(1.0 - PS->velocityVar, 1.0 + PS->velocityVar));
                   
     // Criando uma nova particula.
     PS->particles[PS->ptcNum] = ptc_New(PS->position, //Position
                                         velocity, //Velocity
                                         elapsedTime, //TimeBorn
                                         PS->ptcLife * rnd_FloatMinMax(1.0 - PS->ptcLifeVar, 1.0 + PS->ptcLifeVar)); //LifeTime
     PS->ptcNum++;
     
     // Tempo do disparo, eh o tempo decorrido ateh o momento.
     PS->time = elapsedTime;
}

void ptcSys_Update(ptcSys* PS, GLfloat elapsedTime) {
     int i, j;
     // Percorre todas as particulas.
     for (i = 0; i < PS->ptcNum; i++) {
         // Se a particula estiver morta, ignore.
         if ( ! PS->particles[i].isAlive)
            continue;
         
         // Percorre todos os defletores para esta particula.
         // E testa se esta colidiu com um defletor.
         // Se colidiu, sua nova velocidade  jah sera calculada.
         for (j = 0; j < PS->dflNum; j++) {
             deflector_Test(PS->deflectors[j], &(PS->particles[i]));
         }
         // Movimenta a particula.
         // Se o retorno for 1, a particula morreu.
         if (ptc_Update(&(PS->particles[i]), elapsedTime, PS->gravity)) {
            //free(PS->particles[i]);
            PS->particles[i].isAlive = 0;
         }
     }
}

void ptcSys_Display(ptcSys* PS) {
     int i;
     
     // Desenha o sistema de particulas.
     //glColor3f(0.0,0.0,0.0);
     glBegin(GL_QUADS);
     glVertex2f(PS->position.x + 3.0, PS->position.y + 3.0);
     glVertex2f(PS->position.x + 3.0, PS->position.y - 3.0);
     glVertex2f(PS->position.x - 3.0, PS->position.y - 3.0);
     glVertex2f(PS->position.x - 3.0, PS->position.y + 3.0);
     glEnd();
     
     // Desenha todas as particulas vivas.
     //glColor3f(0.0,0.0,1.0);
     glBegin(GL_POINTS);
     for (i = 0; i < PS->ptcNum; i++) {
         if ( ! PS->particles[i].isAlive)
            continue;
            
         glVertex2f(PS->particles[i].position.x, PS->particles[i].position.y);
     }
     glEnd();
     
     // Desenha todos os defletores.
     //glColor3f(1.0,0.0,0.0);
     glBegin(GL_LINES);
     for (i = 0; i < PS->dflNum; i++) {
         glVertex2f(PS->deflectors[i].p1.x, PS->deflectors[i].p1.y);
         glVertex2f(PS->deflectors[i].p2.x, PS->deflectors[i].p2.y);
     }
     glEnd();
}

void ptcSys_AddDfl(ptcSys* PS, dfl df) {
     // Adiciona o defletor a lista de defletores do sistema de particulas.
     PS->deflectors[PS->dflNum] = df;
     PS->dflNum++;
}
