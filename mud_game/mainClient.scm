(use tcp)

;; divide a 'string' em substrings separadas pelo 'char'
;; retorna uma lista contendo estas substrings
(define string-cut
  (lambda (string char)
    (let loop ((i (- (string-length string) 1))
               (j (string-length string))
               (list '()))
      (if (< i 0)
          (cons (substring string 0 j) list)
          (if (char=? (string-ref string i) char)
              (loop (- i 1) i (cons (substring string (+ i 1) j) list))
              (loop (- i 1) j list))))))
          
;; tenta uma conexao em 'host' e 'porta'
;; e inicia o processo de comunicacao
;; le comandos do usuario e envia ao servidor
;; traduz os comandos do servidor para o usuario
(define connect
  (lambda (host port)
    (let-values (((i o) (tcp-connect host port)))
      (print "> Voce se encontra em um novo ambiente")
      (letrec ((loop (lambda ()
                       (display "#>")
                       (write-line (read-line) o)
                       (display ">")
                       (let ((info (read-line i)))
                         (cond
                          ((eof-object? info) (error "Servidor Desconectado"))
                          ((string=? info "OK") (print "Voce se movimentou"))
                          ((string=? info "PAREDE") (print "Existe uma parede a sua frente"))
                          ((char-numeric? (string-ref info 0)) (begin
                                                                   (print (format "Voce entrou na passagem ~a" (string-ref info 0)))
                                                                   (close-output-port o)
                                                                   (connect (cadr (string-cut info #\ )) (string->number (caddr (string-cut info #\ ))))))
                          
                          (else (print info))))
                       (loop))))
        (loop)))))

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