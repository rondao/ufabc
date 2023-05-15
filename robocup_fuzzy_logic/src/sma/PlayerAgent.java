package sma;

import java.io.IOException;
import java.io.Serializable;

import org.lwjgl.opengl.Display;

import robocup.Ponto;
import robocup.RoboCup;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class PlayerAgent extends Agent {
	public Ponto posicao;

	protected void setup() {
		posicao = new Ponto(500, 300);

		addBehaviour(new MarcacaoBehavior(this, 20));
	}

	public double getDistancia(Ponto p) {
		return Math.sqrt(Math.pow((posicao.x - p.x), 2)
				+ Math.pow((posicao.y - p.y), 2)) / 10;
	}
	
	public void moverPara(Ponto p) {
		float norma = (float) Math.sqrt(Math.pow(p.x - posicao.x, 2)
				+ Math.pow(p.y - posicao.y, 2));
		if (norma == 0)
			return;

		posicao.x += ((p.x - posicao.x) / norma) * 2; // Velocidade Cte
		posicao.y += ((p.y - posicao.y) / norma) * 2; // Velocidade Cte
		
		// Informa a nova posicao ao RC.
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID("RC", AID.ISLOCALNAME));
		msg.setOntology("NOVA_POSICAO");
		msg.setLanguage(getLocalName());
		try {
			msg.setContentObject(posicao);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		send(msg);
	}
}
