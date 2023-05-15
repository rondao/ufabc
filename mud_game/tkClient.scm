(use tcp)
(use srfi-18)
(require-extension tk)

;; inicia a gui tk.
;; recebe a saida para o servidor, onde enviara as mensagens.
;; retorna a caixa de texto para imprimir para o usuario.
(define start-gui
  (lambda (o)
    (start-tk)
    (let ((wText (tk 'create-widget 'text)))
      (tk/pack wText
               #:expand #t
               #:side 'top)
      (tk/pack
       (tk 'create-widget 'button
           #:text 'Cima
           #:command (lambda () (write-line "n" o)))
       (tk 'create-widget 'button
           #:text 'Baixo
           #:command (lambda () (write-line "s" o)))
       (tk 'create-widget 'button
           #:text 'Esquerda
           #:command (lambda () (write-line "o" o)))
       (tk 'create-widget 'button
           #:text 'Direita
           #:command (lambda () (write-line "l" o)))
       (tk 'create-widget 'button
           #:text 'Pegar
           #:command (lambda () (write-line "p" o)))
       (tk 'create-widget 'button
           #:text 'Deixar
           #:command (lambda () (write-line "d" o)))
       (tk 'create-widget 'button
           #:text 'Sair
           #:command (lambda () (end-tk) (exit)))
       #:expand #t
       #:side 'left)
    wText)))
    
;; verifica se uma string eh informacao de passagem
(define passagem?
  (lambda (str)
    (if (string-ci=? (car (string-split str)) "PASSAGEM")
      #t
      #f)))

;; tenta uma conexao em 'host' e 'porta'
;; e inicia o processo de comunicacao
;; le comandos do usuario e envia ao servidor
;; traduz os comandos do servidor para o usuario
(define connect
  (lambda (host port)
    (let-values (((i o) (tcp-connect host port)))
      (let ((wText (start-gui o)))
        (thread-start! (make-thread (lambda () (event-loop))))
        (wText 'insert '(1 . 0) (format "> Voce se encontra em um novo ambiente~%"))
        (letrec ((loop (lambda ()
                         (let ((info (read-line i)))
                           (cond
                            ((or (null? info) (eof-object? info)) (begin (end-tk) (error "Servidor Desconectado")))
                            ((string-ci=? info "OK") (wText 'insert '(1 . 0) (format "> Voce se movimentou~%")))
                            ((string-ci=? info "PAREDE") (wText 'insert '(1 . 0) (format "> Existe uma parede a sua frente~%")))
                            ((passagem? info) (let ((infos (string-split info)))
                                                (wText 'insert '(1 . 0) (format "> Voce entrou numa passagem~%"))
                                                (close-output-port o)
                                                (end-tk)
                                                (connect (cadr infos) (string->number (caddr infos)))))

                            (else (wText 'insert '(1 . 0) (format ">~a~%" info)))))
                         (loop))))
          (loop))))))

;; inicia o cliente
;; pergunta o host e porta desejados para se conectar
(define startClient
  (lambda ()
    (print "### SCHEME VIRTUAL WORLD ###")
    (print "> Digite o endereco e porta de conexao")
    (let ((host (begin
               (display "#>")
               (read-line)))
          (port (begin
                  (display "#>")
                  (string->number (read-line)))))
      (connect host port))))

;; inicia o cliente
(startClient)
