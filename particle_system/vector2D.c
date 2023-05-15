vector2 vector2_New(GLfloat x, GLfloat y) {
        vector2 v;
        
        v.x = x;
        v.y = y;
        
        return v;
}

vector2 vector2_MultE(vector2 v, GLfloat e) {
        vector2 res;
        
        res.x = v.x * e;
        res.y = v.y * e;
        
        return res;
}

vector2 vector2_Sum(vector2 v1, vector2 v2) {
        vector2 res;
        
        res.x = v1.x + v2.x;
        res.y = v1.y + v2.y;
        
        return res;
}

void vector2_Unity(vector2* v) {
        GLfloat norm = vector2_Norm(*v);
        
        v->x = v->x / norm;
        v->y = v->y / norm;
}

GLfloat vector2_Norm(vector2 v) {
        return sqrt(v.x*v.x + v.y*v.y);
}
