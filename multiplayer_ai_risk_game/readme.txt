Programação Orientada a Objetos em Jogos Multijogadores
 
Rafael Tavares Rondão
E-mail: rafael.rondao@ufabc.edu.br

Vinicius Aldeia Zanquini
E-mail: vinicius.zanquini@ufabc.edu.br


Universidade Federal do ABC
Centro de Matemática, Computação e Cognição

-------------------------------------------

Este projeto se divide em dois executáveis, o servidor e o cliente.

O servidor é o arquivo 'warproj - server.jar', que está disponível no pacote 'warproj.zip' na parte Downloads.
O servidor fica a espera por conexões na porta 11000. O número de conexões é determinada por uma caixa de dialogo.
Quando todos os jogadores se conectarem o servidor ficará gerenciando o jogo.

O cliente é executado num navegador de internet, sendo que este deve estar habilitado para rodar JavaScript.
O arquivo do cliente é o 'index.html', que se encontra no diretório 'HTML' do pacote 'warproj.zip'.
O cliente precisa do IP do host e a porta a se conectar. Quando o cliente encontrar o servidor, este ficará a espera do início do jogo.
Para o cliente rodar, é preciso permitir que o Applet do jogo tenha acesso as conexões do computador.
Quando todos os jogadores se conectarem o jogo inicia seguindo as regras do jogo original do War.