GLfloat rnd_Float() {
        return (float) rand()/ (float) RAND_MAX;
}

GLfloat rnd_FloatMinMax(GLfloat min, GLfloat max) {
        return (float) rnd_Float()*(max-min) + min;
}
