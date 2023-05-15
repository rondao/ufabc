dfl deflector_New(vector2 p1, vector2 p2) {
     dfl df;
     
     df.p1 = p1;
     df.p2 = p2;
     
     // 'dx', 'dy' e 'c' para calcular a funcao implicita.
     df.dx = p2.x - p1.x;
     df.dy = p2.y - p1.y;
     df.c = p2.x*p1.y - p1.x*p2.y;
     
     // Vetor normal a reta do Defletor.
     vector2 normal;
     normal.x = df.dy;
     normal.y = -df.dx;
     
     // A normal sera normalizada.
     vector2_Unity(&normal);
     df.normal = normal;
     
     return df;
}

void deflector_Test(dfl df, ptc* p) {
      
      // Calcula a funcao implicita.                            
      GLfloat d = p->position.x*df.dy - p->position.y*df.dx + df.c;
      
      if (d < 0) {
          // Calcula a velocidade da particula e a diminui por "0.4".
          GLfloat velocity = vector2_Norm(p->velocity) * 0.6;
          // A nova velocidade sera na direcao da normal e tera a nova velocidade.
          p->velocity = vector2_MultE(df.normal, velocity);
      }

}
