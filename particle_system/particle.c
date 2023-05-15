ptc ptc_New(vector2 position, vector2 velocity, GLfloat timeBorn, GLfloat lifeTotal) {
    ptc p;
    
    p.position = position;
    p.velocity = velocity;
    p.timeBorn = timeBorn;
    p.lifeTotal = lifeTotal;
    
    p.isAlive = 1;
    
    return p;
}

GLint ptc_Update(ptc* p, GLfloat elapsedTime, vector2 gravity) {
     // Sua nova posicao eh a soma desta com a velocidade.
     p->position = vector2_Sum(p->position, p->velocity);
     // Sua nova velocidade eh a soma desta com a aceleracao.
     p->velocity = vector2_Sum(p->velocity, gravity);
     
     // Verifica se a particula morreu.
     if (p->lifeTotal < elapsedTime - p->timeBorn)
        return 1;
     return 0;
}
