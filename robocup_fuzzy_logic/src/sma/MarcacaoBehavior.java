package sma;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import robocup.*;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class MarcacaoBehavior extends TickerBehaviour {

	PlayerAgent pa;
	String jogMarcar = null;

	public MarcacaoBehavior(Agent a, long period) {
		super(a, period);
		pa = (PlayerAgent) a;
	}

	@Override
	protected void onTick() {
		ACLMessage msg = myAgent.receive(MessageTemplate
				.MatchOntology("MARCAR"));

		if (msg != null) { // Iniciar marcacao
			definirMarcador();
		}

		// Se nao definiu o marcador ainda espere.
		if (jogMarcar == null) {
			return;
		}

		// Pede ao RoboCup a posicao de quem precisa marcar.
		ACLMessage askMsg = new ACLMessage(ACLMessage.REQUEST);
		askMsg.addReceiver(new AID("RC", AID.ISLOCALNAME));
		askMsg.setOntology("POSICAO");
		askMsg.setContent(jogMarcar);
		myAgent.send(askMsg);

		// Fica esperando a resposta.
		ACLMessage replyMsg = myAgent.blockingReceive(MessageTemplate
				.MatchOntology("POSICAO"));
		Ponto p;
		try {
			p = (Ponto) replyMsg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
			return;
		}
		
		// Jogador perseguindo a bola esta muito longe.
		// Ira redefinir os marcadores.
		// Se ninguem estiver perseguindo a bola nunca ira redefinir! Pode acontecer.
		if (jogMarcar.equals("BOLA")) {
			if (pa.getDistancia(p) > 20) {
				// Pede a todos para redefinirem a marcacao.
				ACLMessage msgMarcado = new ACLMessage(ACLMessage.INFORM);
				for (int i = 0; i < 11; i++) {
					msgMarcado.addReceiver(new AID("A" + i, AID.ISLOCALNAME));
				}
				msgMarcado.setOntology("MARCAR");
				myAgent.send(msgMarcado);
				System.out.println("REDEFINIR MARCACAO!");
			}
		}

		pa.moverPara(p);
	}

	@SuppressWarnings("unchecked")
	private void definirMarcador() {
		// Ignora mensagens de marcacoes anteriores.
		MessageTemplate mt = MessageTemplate.MatchOntology("MARCADO");
		for (ACLMessage msgM = myAgent.receive(mt); msgM != null; msgM = myAgent
				.receive(mt)) {
		}

		ACLMessage askMsg = new ACLMessage(ACLMessage.REQUEST);
		askMsg.addReceiver(new AID("RC", AID.ISLOCALNAME));
		askMsg.setOntology("POSICAO");
		askMsg.setContent("ALL");
		myAgent.send(askMsg);

		askMsg.setContent("BOLA");
		myAgent.send(askMsg);

		// Fica esperando a resposta da posicao dos adversarios.
		ACLMessage replyMsg = myAgent.blockingReceive(MessageTemplate.and(
				MessageTemplate.MatchOntology("POSICAO"),
				MessageTemplate.MatchLanguage("ALL")));

		HashMap<String, Player> adversarios;
		try {
			adversarios = (HashMap<String, Player>) replyMsg.getContentObject();
		} catch (UnreadableException e1) {
			e1.printStackTrace();
			return;
		}

		// Fica esperando a resposta da posicao da bola.
		replyMsg = myAgent.blockingReceive(MessageTemplate.and(
				MessageTemplate.MatchOntology("POSICAO"),
				MessageTemplate.MatchLanguage("BOLA")));

		Ponto bola;
		try {
			bola = (Ponto) replyMsg.getContentObject();
		} catch (UnreadableException e1) {
			e1.printStackTrace();
			return;
		}

		// Uma pausa aleatoria para evitar que os agentes marquem um mesmo adversario.
		try {
			Thread.sleep((long) (Math.random() * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		// Verifica quem os outros ja marcaram.
		mt = MessageTemplate.MatchOntology("MARCADO");
		for (ACLMessage msgM = myAgent.receive(mt); msgM != null; msgM = myAgent
				.receive(mt)) {
			String content = msgM.getContent();

			if (content.equals("BOLA")) {
				bola = null;
			} else {
				adversarios.put(content, null);
			}
		}

		double minDist = 9999; // Valor grande.
		if (bola != null) {
			minDist = pa.getDistancia(bola);
			jogMarcar = "BOLA";
		}

		// Percorre vendo quem esta mais perto.
		Iterator<Entry<String, Player>> it = adversarios.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Player> pair = it.next();

			Player p = pair.getValue();
			if (p == null) {
				continue;
			}

			double dist = pa.getDistancia(pair.getValue().getPosicao());

			if (dist < minDist) {
				minDist = dist;
				jogMarcar = pair.getKey();
			}
		}
		System.out.println(myAgent.getLocalName() + ": " + jogMarcar);

		// Avisa para todos quem vai marcar
		ACLMessage msgMarcado = new ACLMessage(ACLMessage.INFORM);
		for (int i = 0; i < 10; i++) {
			msgMarcado.addReceiver(new AID("A" + i, AID.ISLOCALNAME));
		}
		msgMarcado.setOntology("MARCADO");
		msgMarcado.setContent(jogMarcar);
		myAgent.send(msgMarcado);

	}
}
