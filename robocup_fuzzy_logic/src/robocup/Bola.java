package robocup;

public class Bola {

	private Ponto velocidade;
	private Ponto posicao;
	private float aceleracao;
	
	public Bola(){
		posicao = new Ponto(RoboCup.width/2, RoboCup.height/2);
		velocidade = new Ponto(0,0);
		aceleracao = -0.05f;
	}
	
	public void moverBola(){
		posicao.x += velocidade.x;
		posicao.y += velocidade.y;
		
		// checando gol
		if (posicao.x < 5) {
			if (posicao.y >= 268-5 && posicao.y <= 332+5) {
				RoboCup.setPlacar(2);
			}
		} else if (posicao.x > 995) {
			if (posicao.y >= 268-5 && posicao.y <= 332+5) {
				RoboCup.setPlacar(1);
			}
		}
		
		
		// bouncing
		if(posicao.x < 5 || posicao.x > 995){
			velocidade.x *= -1;
		}
		if(posicao.y < 5 || posicao.y > 595){
			velocidade.y *= -1;
		}
		
		if(velocidade.x > 0) {
			velocidade.x += aceleracao*velocidade.x;
			if(velocidade.x < 0.1)
				velocidade.x = 0;
		}
		if(velocidade.x < 0) {
			velocidade.x += aceleracao*velocidade.x;
			if(velocidade.x > -0.1)
				velocidade.x = 0;
		}
		
		if(velocidade.y > 0) {
			velocidade.y += aceleracao*velocidade.y;
			if(velocidade.y < 0.1)
				velocidade.y = 0;
		}
		if(velocidade.y < 0) {
			velocidade.y += aceleracao*velocidade.y;
			if(velocidade.y > -0.1)
				velocidade.y = 0;
		}
	
		
	}
	
	public void setVelocidade(Ponto direcao, float velocidade){
		this.velocidade.x = direcao.x * velocidade;
		this.velocidade.y = direcao.y * velocidade;
	}
	
	public void setPosicao(Ponto posicao){
		this.posicao.x = posicao.x;
		this.posicao.y = posicao.y;
	}
	
	public Ponto getPosicao(){
		return posicao;
	}
	
	public Ponto getVelocidade() {
		return velocidade;
	}
}
