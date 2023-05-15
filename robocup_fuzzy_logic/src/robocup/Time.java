package robocup;

public class Time {

	private Player goleiro;
	private Player jogadores[];
	private boolean posse;
	private int time;

	int w = RoboCup.width;
	int h = RoboCup.height;

	public Time(int time) {
		goleiro = new Player(45, 300, 45, 300, this);
		jogadores = new Player[10];
		posicionaJogadores();
		this.time = time;

		if (time == 2) { // Inverte as posicoes
			goleiro.setPosicao(w - goleiro.getPosicao().x, h
					- goleiro.getPosicao().y);
			goleiro.setPosInicial(w - goleiro.getPosicao().x, h
					- goleiro.getPosicao().y);
			for (int i = 0; i < 10; i++) {
				jogadores[i].setPosicao(w - jogadores[i].getPosicao().x,
						jogadores[i].getPosicao().y);
				jogadores[i].setPosInicial(w - jogadores[i].getPosInicial().x,
						jogadores[i].getPosInicial().y);
			}
		}
	}

	private void posicionaJogadores() {
		jogadores[0] = new Player(200, 112, 320, 80, this);
		jogadores[1] = new Player(200, 237, 80, 240, this);
		jogadores[2] = new Player(200, 362, 80, 360, this);
		jogadores[3] = new Player(200, 487, 320, 600, this);
		jogadores[4] = new Player(300, 200, 650, 50, this);
		jogadores[5] = new Player(300, 300, 500, 80, this);
		jogadores[6] = new Player(300, 400, 500, 420, this);
		jogadores[7] = new Player(400, 200, 650, 480, this);
		jogadores[8] = new Player(400, 300, 920, 200, this);
		jogadores[9] = new Player(400, 400, 920, 400, this);
	}

	public boolean temPosse() {
		return posse;
	}

	public void setPosse(boolean posse) {
		this.posse = posse;

		if (!posse) {
			for (Player p : jogadores) {
				p.setPosse(posse);
			}
		}
	}

	public Player[] getPlayers() {
		return this.jogadores;
	}

	public Player getGoleiro() {
		return goleiro;
	}

	public int getTimeNumero() {
		return time;
	}
}
