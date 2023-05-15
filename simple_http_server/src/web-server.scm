(load "12.scm")
(load "13.scm")

(load "date.scm")
(load "pool-actor.scm")
(load "caching.scm")
(load "http.scm")

;; Endereco do servidor web.
(define *web-host* "127.0.0.1")
(define *web-port* 10000)

;; Endereco do node do termite.
(define *termite-host* "127.0.0.1")
(define *termite-port* 50001)

;; Diretorio raiz do acesso web.
(define *base* "/home/rondao/web")

;; Diretorios de acesso proibido.
(define *forbidden* (list "../" "private/"))

;; Arquivo para gravar log.
(define *log-file* "/log.txt")

;; Numero de trabalhadores para pegar requisicoes.
(define *number-of-workers* 4)

;; Cria e inicia o node do terminte.
(define server (make-node *termite-host* *termite-port*))
(node-init server)

;; Comportamento do ator para fazer logs.
;; Cada mensagem recebida, adiciona no arquivo de log:
;;  data e conteudo da mensagem.
(define logging
    (lambda ()
      (let loop ()
        (recv (log
               (let ((out (open-output-file (list path: (string-append *base* *log-file*)
                                                  append: #t))))
                 (display (current-date) out)
                 (display log out)
                 (newline out)
                 (close-output-port out))))
        (loop))))

;; Comportamento do ator para interpretar arquivos .scm.
;; Le cada entrada como simbolo e avalia.
;; Se o retorno obtido for void, nao envia nada.
(define evalling
  (lambda ()
    (let loop ()
      (recv ((in out)
             (send-headers out "text/html")
             (newline out)
             (let loop ((linha (eval (read in))))
               (cond ((not (eof-object? linha))
                      (cond ((not (eqv? linha #!void))
                             (display (eval linha) out)
                             (newline out)))
                      (loop (read in)))))
             (close-output-port out)))
      (loop))))

;; Comportamento do ator para enviar respostas 403.
;; Enviara uma pagina de negacao padrao.
(define send403
  (lambda ()
    (let loop ()
      (recv (out
             (send-headers out "text/html")
             (newline out)
             (copia (open-input-file (string-append *base* "/private/418.html")) out)
             (close-output-port out)))
      (loop))))
             
;; Comportamento do ator para enviar respostas 404.
;; Enviara uma pagina de erro padrao.
(define send404
  (lambda ()
    (let loop ()
      (recv (out
             (send-headers out "text/html")
             (newline out)
             (copia (open-input-file (string-append *base* "/private/404.html")) out)
             (close-output-port out)))
      (loop))))

;; Comportamento do ator para enviar respostas 200.
;; Se receber uma requisicao .html, enviará o header do tipo "text/html",
;;  caso contrario sera "multipart/form-data", para realizar download.
;; Verifica se esta no cache, se estiver envia diretamente.
(define send200
  (lambda ()
    (let loop ()
      (recv ((in out file)
             (let ((cache (check-cache file)))
               (if (= (string-suffix-length ".html" file) 5)
                   (send-headers out "text/html")
                   (send-headers out "multipart/form-data"))
               (newline out)
               (if (eqv? cache #f)
                   (copia in out)
                   (display cache out))
               (close-output-port out))))
      (loop))))

;; Comportamento do ator que filtra as requisicoes.
;; Ele tambem envia a requisicao pura para o logger.
(define filtering
  (lambda ()
    (let loop ()
      (recv ((in out worker)
             (let ((requisicao (string-tokenize (read-line in))))
               (! logger requisicao)
               (if (check-http requisicao)
                   (let ((url (trata-caminho (cadr requisicao))))
                     (let ((file (string-append *base* url)))
                       (if (caminho-proibido file *forbidden*)
                           (! sender403 out)
                           (! worker (list file in out)))))))))
      (loop))))

;; Tenta processar a requisicao.
(define worker-proccess
  (lambda (file in out s200)
    (if (file-exists? file)
        (let ((in (open-input-file file)))
          (if (= (string-suffix-length ".scm" file) 4)
              (! evaluer (list in out))
              (! s200 (list in out file))))
        (! sender404 out))))

;; Ator para pegar as requisicoes.
;; Recebe as portas da requisicao e envia para o filtro.
;; Recebe a requisicao filtrada e processa.
;; Cada trabalhador possui o seu sender200,
;;  assim evita acumular trabalho para apenas um sender200.
(define worker
  (lambda ()
    (let loop ((sender200 (spawn send200)))
      (recv ((file in out) ;; Requisicao filtrada.
             (with-exception-catcher
              (lambda (e)
                (print (thread-name (current-thread)) " got exception: " e "\n")
                (close-output-port out))
              (lambda () (worker-proccess file in out sender200))))
            ((in out) ;; Requisicao nova.
             (! filter (list in out (self)))))
      (loop sender200))))

;; Ator para enviar arquivos.scm interpretados.
(define evaluer (spawn evalling))

;; Ator para realizar logs.
(define logger (spawn logging))

;; Ator de filtragem.
(define filter (spawn filtering))

;; Ator para enviar pagina de erro 404.
(define sender404 (spawn send404))

;; Ator para enviar pagina de erro 403.
(define sender403 (spawn send403))

;; Lista de atores trabalhadores.
(define *workers* (make-pool *number-of-workers* worker))

;; Aceita conexão TCP e chama um worker.
(define trata
  (lambda (s first current)
    (let ((port (read s))
          (ptr (if (null? current)
                   first
                   current)))
      (! (car ptr) (list port port))
      (trata s first (cdr ptr)))))

;; Pega uma porta, começa a ouvir, e chama o tratador
;; de socket.
(define inicia-servidor
  (lambda ()
    (let ((socket (open-tcp-server (list server-address: *web-host*
                                         port-number: *web-port*))))
      (print "Listening on " *web-port* "...\n")
      (trata socket *workers*  *workers*))))

(inicia-servidor)