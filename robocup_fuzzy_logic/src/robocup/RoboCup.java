package robocup;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import org.lwjgl.input.*;

import rules.*;

public class RoboCup extends Agent {

	final static int width = 1000;
	final static int height = 600;

	private static int golsTime1;
	private static int golsTime2;
	private static String titulo;

	public static Bola ball;

	private int texCampFut;

	private Time Team1;
	private HashMap<String, Player> adversarios;

	private Time Team2;
	private HashMap<String, Player> agentes;

	private RulePosicaoSemBolaSemPosse rPSBSPx;
	private RulePosicaoSemBolaSemPosse rPSBSPy;

	private RulePosicaoSemBolaComPosseX rPSBCPx;
	private RulePosicaoSemBolaComPosseY rPSBCPy;

	private RuleChuteGol rCG;

	private RulePassar rPassar;

	protected void setup() {
		ball = new Bola();

		golsTime1 = 0;
		golsTime2 = 0;
		titulo = "RoboCup Simulation";

		createTeams();

		try {
			rPSBSPx = new RulePosicaoSemBolaSemPosse(0.0, 25.0, 50.0, 75.0,
					100.0);
			rPSBSPy = new RulePosicaoSemBolaSemPosse(0.0, 15.0, 30.0, 45.0,
					60.0);
			rPSBCPx = new RulePosicaoSemBolaComPosseX(0.0, 25.0, 50.0, 75.0,
					100.0);
			rPSBCPy = new RulePosicaoSemBolaComPosseY(0.0, 15.0, 30.0, 45.0,
					60.0);
			rPassar = new RulePassar();
			rCG = new RuleChuteGol();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		try {
			init();
		} catch (LWJGLException le) {
			le.printStackTrace();
			System.err.println("Failed to initialize.");
			return;
		}

		addBehaviour(new TickerBehaviour(this, 25) {
			public void onTick() {
				try { // isClose lanca excecao quando fecha.
					if (!Display.isCloseRequested()) {
						iterate();
					} else {
						destroy();
						stop();
					}
				} catch (Exception e) {
					destroy();
					stop();
				}
			}
		});
	}

	private void createTeams() {
		adversarios = new HashMap<String, Player>();
		Team1 = new Time(1);

		Player[] jogs = Team1.getPlayers();
		for (int i = 0; i < jogs.length; i++) {
			adversarios.put("P" + i, jogs[i]);
		}

		agentes = new HashMap<String, Player>();
		Team2 = new Time(2);
		
		jogs = Team2.getPlayers();
		for (int i = 0; i < jogs.length; i++) {
			agentes.put("A" + i, jogs[i]);
		}
	}

	private void init() throws LWJGLException {
		// Informa a nova posicao ao RC.
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		for (int i = 0; i < 10; i++) {
			msg.addReceiver(new AID("A" + i, AID.ISLOCALNAME));
		}
		msg.setOntology("MARCAR");
		send(msg);

		Display.setLocation(0, 0);
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setTitle(titulo);
		Display.create();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GLU.gluOrtho2D(0, width, 0, height);

		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glColor3f(0.0f, 0.0f, 1.0f);

		try {
			texCampFut = BMPLoader.loadBMP("res/Campo-Fut2.bmp", 512, 512,
					false);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texCampFut);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void iterate() {
		verificarNovasPosicoes();
		verificarRequisicoesPorPosicao();

		inferenceEngine(Team1);
		// inferenceEngine(Team2);

		desenhar();

		ball.moverBola();

		mouseFunc();

		Display.update();
	}

	private void verificarNovasPosicoes() {
		// Recebe mensagem de nova posicao.
		MessageTemplate mt = MessageTemplate.MatchOntology("NOVA_POSICAO");
		for (ACLMessage msg = receive(mt); msg != null; msg = receive(mt)) {
			Player p = agentes.get(msg.getLanguage());
			
			if (p == null) {
				continue;
			}
			
			try {
				Ponto pos = (Ponto) msg.getContentObject();
				p.setPosicao(pos);
			} catch (UnreadableException e) {
				e.printStackTrace();
				return;
			}
		}
	}
	
	private void verificarRequisicoesPorPosicao() {
		// Verifica se alguem precisa das posicoes.
		MessageTemplate mt = MessageTemplate.MatchOntology("POSICAO");
		for (ACLMessage msg = receive(mt); msg != null; msg = receive(mt)) {
			ACLMessage replyMsg = msg.createReply();

			try {
				if (msg.getContent().equals("ALL")) {
					replyMsg.setLanguage("ALL");
					replyMsg.setContentObject(adversarios);
				} else if (msg.getContent().equals("BOLA")) {
					replyMsg.setLanguage("BOLA");
					replyMsg.setContentObject(ball.getPosicao());
				} else {
					replyMsg.setContentObject(adversarios.get(msg.getContent()).getPosicao());
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			send(replyMsg);
		}
	}
	
	private void desenhar() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glPointSize(10);

		// fundo
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0.0, 0.0);
		GL11.glVertex2d(0.0, 0.0);
		GL11.glTexCoord2d(1.0, 0.0);
		GL11.glVertex2d(width, 0.0);
		GL11.glTexCoord2d(1.0, 1.0);
		GL11.glVertex2d(width, height);
		GL11.glTexCoord2d(0.0, 1.0);
		GL11.glVertex2d(0.0, height);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		// jogadores
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glColor3f(1.0f, 0.0f, 0.0f);
		GL11.glVertex2f(Team1.getGoleiro().getPosicao().x, Team1.getGoleiro()
				.getPosicao().y);
		for (Player p : Team1.getPlayers()) {
			GL11.glVertex2f(p.getPosicao().x, p.getPosicao().y);
		}

		GL11.glColor3f(0.0f, 0.0f, 1.0f);
		GL11.glVertex2f(Team2.getGoleiro().getPosicao().x, Team2.getGoleiro()
				.getPosicao().y);
		for (Player p : Team2.getPlayers()) {
			GL11.glVertex2f(p.getPosicao().x, p.getPosicao().y);
		}
		GL11.glEnd();

		// bola
		GL11.glPointSize(6);
		GL11.glBegin(GL11.GL_POINTS);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glVertex2f(ball.getPosicao().x, ball.getPosicao().y);
		GL11.glEnd();
	}
	
	private void inferenceEngine(Time t) {
		if (!t.temPosse()) { // Time sem a posse de bola
			for (Player p : t.getPlayers()) {
				semPosse(p, t);
			}
		} else { // Time com a posse de bola
			for (Player p : t.getPlayers()) {
				if (p.getPosseDeBola()) {
					comBola(p, t);
					continue;
				}

				comPosse(p, t);
			}
		}

		goleiro(t.getGoleiro(), t);
	}

	private void semPosse(Player p, Time t) {
		double[] inputsCrispX = new double[2];
		inputsCrispX[0] = p.getPosInicial().x / 10;
		inputsCrispX[1] = ball.getPosicao().x / 10;
		double[] inputsCrispY = new double[2];
		inputsCrispY[0] = p.getPosInicial().y / 10;
		inputsCrispY[1] = ball.getPosicao().y / 10;

		try {
			rPSBSPx.setInput(inputsCrispX);
			float newX = (float) rPSBSPx.inference();
			newX = (newX == -1.0) ? p.getPosicao().x : newX * 10;

			rPSBSPy.setInput(inputsCrispY);
			float newY = (float) rPSBSPy.inference();
			newY = (newY == -1.0) ? p.getPosicao().y : newY * 10;

			p.moverPara(new Ponto(newX, newY));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		float proxX = Math.abs(p.getPosicao().x - ball.getPosicao().x);
		float proxY = Math.abs(p.getPosicao().y - ball.getPosicao().y);
		if (proxX <= 10 && proxY <= 10) {
			if (getOpositeTeam(t).temPosse()) {
				if (p.tiraBola(Math.max((11 - proxX) / 10, (11 - proxY) / 10))) {
					getOpositeTeam(t).setPosse(false);
				}
			} else {
				p.tiraBola(0.9f);
			}
		}
	}

	private void comPosse(Player p, Time t) {
		double[] inputsCrispX = new double[2];
		inputsCrispX[0] = p.getPosInicial().x / 10;
		inputsCrispX[1] = ball.getPosicao().x / 10;
		double[] inputsCrispY = new double[2];
		inputsCrispY[0] = p.getPosInicial().y / 10;
		inputsCrispY[1] = ball.getPosicao().y / 10;

		if (t.equals(Team2)) { // Se time2, inverte ataque e defesa
			inputsCrispX[0] = (width - p.getPosInicial().x) / 10;
			inputsCrispX[1] = (width - ball.getPosicao().x) / 10;
		}

		try {
			rPSBCPx.setInput(inputsCrispX);
			float newX = (float) rPSBCPx.inference();
			newX = (newX == -1.0) ? p.getPosicao().x : newX * 10;

			rPSBCPy.setInput(inputsCrispY);
			float newY = (float) rPSBCPy.inference();
			newY = (newY == -1.0) ? p.getPosicao().y : newY * 10;

			if (t.equals(Team2)) { // Se time2, inverte ataque e defesa
				newX = width - newX;
			}

			p.moverPara(new Ponto(newX, newY));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		p.tentarPasse(rPassar, getOpositeTeam(t));
	}

	private void comBola(Player p, Time t) {
		double[] inputsCrispY = new double[2];
		inputsCrispY[0] = p.getPosInicial().y / 10;
		inputsCrispY[1] = ball.getPosicao().y / 10;

		try {
			float newX = 900.0f;
			if (t.equals(Team2)) {
				newX = width - newX;
			}

			rPSBCPy.setInput(inputsCrispY);
			float newY = (float) rPSBCPy.inference();
			newY = (newY == -1.0) ? p.getPosicao().y : newY * 10;

			p.moverPara(new Ponto(newX, newY));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		if (p.tentarChutarGol(rCG, getOpositeTeam(t).getGoleiro())) {
			return;
		}

		p.tentarPasse(rPassar, getOpositeTeam(t));
	}

	private void goleiro(Player p, Time t) {
		float newX;
		if (t.equals(Team2)) {
			newX = width - 10 - (width - ball.getPosicao().x) / 6;
		} else {
			newX = 10 + ball.getPosicao().x / 6;
		}

		float newY = height / 2 - (height / 2 - ball.getPosicao().y) / 6;

		if (newY < 268) {
			newY = 268;
		} else if (newY > 332) {
			newY = 332;
		}

		p.moverPara(new Ponto(newX, newY));

		float proxX = Math.abs(p.getPosicao().x - ball.getPosicao().x);
		float proxY = Math.abs(p.getPosicao().y - ball.getPosicao().y);
		if (proxX <= 5 && proxY <= 5) {
			p.tiraBola(1);
			getOpositeTeam(t).setPosse(false);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(0);
			}

			if (!p.tentarPasse(rPassar, getOpositeTeam(t))) {
				p.chute(new Ponto(300, 500));
			}
		}
	}

	private void mouseFunc() {
		if (Mouse.getEventButton() != -1.0) {
			if (Mouse.isButtonDown(0)) {
				Team1.getPlayers()[9].setPosicao(Mouse.getX(), Mouse.getY());
			}
			if (Mouse.isButtonDown(1)) {
				if (ball.getPosicao().x == width / 2
						&& ball.getPosicao().y == height / 2) {
					return;
				}
				ball.setVelocidade(new Ponto(width / 2 - ball.getPosicao().x,
						height / 2 - ball.getPosicao().y).Normalizar(), 5);
			}
		}
	}

	private void destroy() {
		Display.destroy();
	}

	private Time getOpositeTeam(Time t) {
		return (Team1.equals(t)) ? Team2 : Team1;
	}

	public static void setPlacar(int time) {
		if (time == 1) {
			golsTime1++;
		} else if (time == 2) {
			golsTime2++;
		}

		ball.setPosicao(new Ponto(RoboCup.width / 2, RoboCup.height / 2));
		ball.setVelocidade(new Ponto(0, 0), 0);

		titulo = "RoboCup Simulation - " + golsTime1 + " X " + golsTime2;
		System.out.println(titulo);
	}
}
