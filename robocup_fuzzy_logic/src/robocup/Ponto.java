package robocup;

import java.io.Serializable;

public class Ponto implements Serializable {

	public float x, y;

	public Ponto(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float Norma() {
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	public Ponto Normalizar() {
		float norma = Norma();
		return new Ponto(x/norma, y/norma);
	}
}
