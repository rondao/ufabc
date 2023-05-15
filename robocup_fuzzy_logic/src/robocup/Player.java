package robocup;

import java.io.Serializable;

import rules.RuleChuteGol;
import rules.RulePassar;

public class Player implements Serializable {

	private Ponto posicao;
	private Ponto posInicial;
	private boolean posse;
	transient Time meuTime;
	private long tempoTirarBola;

	public Player(float x, float y, float xi, float yi, Time time) {
		posicao = new Ponto(x, y);
		posInicial = new Ponto(xi, yi);
		posse = false;
		meuTime = time;
		tempoTirarBola = 0;
	}

	public void setPosicao(float x, float y) {
		posicao.x = x;
		posicao.y = y;
		if (posse)
			RoboCup.ball.setPosicao(posicao);
	}

	public void setPosicao(Ponto p) {
		this.posicao.x = p.x;
		this.posicao.y = p.y;

		if (posse)
			RoboCup.ball.setPosicao(posicao);
	}

	public Ponto getPosicao() {
		return posicao;
	}

	public void setPosInicial(float x, float y) {
		posInicial.x = x;
		posInicial.y = y;
	}

	public Ponto getPosInicial() {
		return posInicial;
	}

	public void moverPara(Ponto p) {
		float norma = (float) Math.sqrt(Math.pow(p.x - posicao.x, 2)
				+ Math.pow(p.y - posicao.y, 2));
		if (norma == 0)
			return;

		posicao.x += ((p.x - posicao.x) / norma) * 2; // Velocidade Cte
		posicao.y += ((p.y - posicao.y) / norma) * 2; // Velocidade Cte
		if (posse)
			RoboCup.ball.setPosicao(posicao);
	}

	public void chute(Ponto direcao) {
		posse = false;
		meuTime.setPosse(false);

		Ponto tmp = new Ponto(direcao.x - posicao.x, direcao.y - posicao.y);
		RoboCup.ball.setVelocidade(tmp.Normalizar(), Math.min(tmp.Norma() / 10,
				20));
	}

	public void setPosse(boolean b) {
		this.posse = b;
		if (posse)
			RoboCup.ball.setPosicao(posicao);
		if (posse)
			this.meuTime.setPosse(b);
	}

	public boolean getPosseDeBola() {
		return posse;
	}

	public double getDistancia(Ponto p) {
		return Math.sqrt(Math.pow((posicao.x - p.x), 2)
				+ Math.pow((posicao.y - p.y), 2)) / 10;
	}

	public boolean tiraBola(float chance) {
		// tempo que tentou tirar bola nao passou
		if (System.currentTimeMillis() < tempoTirarBola)
			return false;

		if (Math.random() < chance) {
			setPosse(true);
			tempoTirarBola = System.currentTimeMillis() + 1000;
			return true;
		}
		tempoTirarBola = System.currentTimeMillis() + 1000;
		return false;
	}

	public boolean tentarPasse(RulePassar rPassar, Time tAdv) {
		if (!posse)
			return false;
		Player aPassar = null;
		float possMaior = 10 - getPressao(tAdv); // depdende da minha pressao

		double[] inputsCrisp = new double[2];

		for (Player p : meuTime.getPlayers()) {
			inputsCrisp[0] = getDistancia(p.getPosicao());
			inputsCrisp[1] = p.getPressao(tAdv);

			try {
				rPassar.setInput(inputsCrisp);
				float possibilidade = (float) rPassar.inference();

				if (possibilidade > possMaior) {
					if (meuTime.getTimeNumero() == 1) {
						if (p.getPosicao().x + 150 < posicao.x)
							continue;
					} else {
						if (p.getPosicao().x - 150 > posicao.x)
							continue;
					}
					
					possMaior = possibilidade;
					aPassar = p;
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		if (aPassar == null)
			return false;
		chute(aPassar.getPosicao());
		return true;
	}

	public boolean tentarChutarGol(RuleChuteGol rCG, Player pg) {
		if (meuTime.getTimeNumero() == 1) {
			if (posicao.x < 850) {
				return false;
			}
		} else {
			if (posicao.x > 150) {
				return false;
			}
		}
		if (posicao.y > 425 || posicao.y < 125) {
			return false;
		}

		double[] inputsCrispY = new double[2];
		// colocando no universo de discurso.
		inputsCrispY[0] = pg.posicao.y - RoboCup.height/2;
		inputsCrispY[1] = posicao.y - RoboCup.height/2;

		float newY = -10.0f;
		try {
			rCG.setInput(inputsCrispY);
			newY = (float) rCG.inference();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		// se nenhuma regra foi ativada
		if (newY == -10.0) {
			return false;
		}
		
		float newX = (meuTime.getTimeNumero() == 1) ? RoboCup.width - 10.0f : 10.0f;
		
		chute(new Ponto(newX, newY+300));
		return true;
	}

	public int getPressao(Time tAdv) {
		int cont = 0;

		for (Player pAdv : tAdv.getPlayers()) {
			if (getDistancia(pAdv.getPosicao()) <= 14) {
				cont++;
			}
		}

		return cont;
	}
}
