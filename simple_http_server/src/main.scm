(load "12.scm")
(load "13.scm")
(load "date.scm")

(define *web-host* "127.0.0.1")
(define *web-port* 10000)
(define *termite-host* "127.0.0.1")
(define *termite-port* 50001)
(define *base* "/home/rondao/web")
(define *number-of-workers* 4)
(define *cache* (make-table))
(define *cache-num* 3)
(define *log-file* "/log.txt")
  
(define server (make-node *termite-host* *termite-port*))
(node-init server)

(define-macro (with-break body)
  (call/cc
   (lambda (break)
     body)))

(with-break
 (with-exception-handler
  (lambda (e)
    (print "fudeu: " e)
    (break #f))
  (lambda () (/ 2 0))))

(p (print 2))
;; copia todo o conteúdo de in para out.
(define copia
  (lambda (in out)
    (let loop ((linha (read-line in)))
      (cond ((not (eof-object? linha))
             (display linha out)
             (newline out)
             (loop (read-line in)))))))

;; verifica se a requisicao eh da forma GET / /HTTP.
(define check-http
  (lambda (lista)
    (cond ((not (= (length lista) 3))
           (print "Not a real HTTP request\n")
           #f)
          ((not (string-ci= (car lista) "get"))
           (print "Request not understood: \n"
                  (car lista))
           #f)
          ((not (= (string-prefix-length "HTTP" (caddr lista))
                   4))
           (print "Not a real HTTP request\n")
           #f)
          (else
           (print "Serving " (cadr lista) "\n")
           #t))))

;; envia o cabecalho HTTP padrao para a saida out.
(define send-headers
  (lambda (out type)
    (display "HTTP/1.0 200 OK\n" out)
    (display (string-append "Content-Type: " type "\n") out)))


;; O requerimento "/" se torna "/index.html".
;; Outras regras de reescrita podem ser incluídas aqui.
(define trata-caminho
  (lambda (str)
    (if (string=? str "/")
        "/index.html"
        str)))

;; comportamento do ator para fazer logs.
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
  
;; comportamento do ator para interpretar arquivos .scm.
(define evalling
  (lambda ()
    (let loop ()
      (recv ((file out)
             (let ((in (open-input-file file)))
               (let loop ((linha (eval (read in))))
                 (cond ((not (eof-object? linha))
                        (cond ((not (eqv? linha #!void))
                               (display (eval linha) out)
                               (newline out)))
                      (loop (read in)))))
               (close-output-port out))))
      (loop))))

(define evaluer
  (spawn evalling))

;; comportamento do ator para enviar requisicoes 200.
(define send200
  (lambda ()
    (let loop ()
      (recv ((file out)
             (if (= (string-suffix-length ".scm" file) 4)
                 (! evaluer (list file out))
                 (let ((cache (check-cache file)))
                   (if (= (string-suffix-length ".html" file) 5)
                       (send-headers out "text/html")
                       (send-headers out "multipart/form-data")) ;; se nao for html, mandara como dado.
                   (newline out)
                   (if (eqv? cache #f)
                       (copia (open-input-file file) out)
                       (display cache out))
                   (close-output-port out)))))
      (loop))))

;; comportamento do ator para enviar requisicoes 404.
(define send404
  (lambda ()
    (let loop ()
      (recv (out
             (send-headers out "text/html")
             (newline out)
             (copia (open-input-file (string-append *base* "/404.html")) out)
             (close-output-port out)))
      (loop))))

;; copia um arquivo para a cache.
(define load-cache
  (lambda (in)
    (let loop ((cache "")
               (linha (read-line in)))
      (if (eof-object? linha)
          cache
          (loop (string-append cache linha "\n") (read-line in))))))

;; verifica se um arquivo se encontra na cache.
;; se sim, sera retornado o arquivo diretamente.
;; se nao, sera contabilizado o numero de requisicoes.
(define check-cache
  (lambda (file)
    (let ((ret #f))
      (if (table-search (lambda (k v) (string=? k file)) *cache*)
          (let ((v (table-ref *cache* file)))
            (if (number? v)
                (cond ((= v *cache-num*)
                       (set! ret (load-cache (open-input-file file)))
                       (table-set! *cache* file ret))
                      ((< v *cache-num*)
                       (table-set! *cache* file (+ v 1))))
                (set! ret (table-ref *cache* file))))
          (table-set! *cache* file 1))
      ret)))

(define sender200
  (spawn send200))

(define sender404
  (spawn send404))

(define logger
  (spawn logging))

;; Uma função que interage com o usuário usando duas
;; portas (entrada/saída).
(define interage
  (lambda (in out)
    (let ((requisicao (string-tokenize (read-line in))))
      (! logger requisicao)
      (if (check-http requisicao)
          (let ((url (trata-caminho (cadr requisicao))))
            (let ((arquivo (string-append *base* url)))
              (if (file-exists? arquivo)
                    (! sender200 (list arquivo out))
                    (! sender404 out))))))))
    
;; ator para pegar as requisicoes.
(define worker
  (lambda ()
    (let loop ()
      (recv ((in out)
             (call/cc
              (lambda (k)
                (with-exception-handler
                 (lambda (e)
                   (print (thread-name (current-thread)) " got exception: " e "\n")
                   (close-output-port out)
                   (k #f))
                 (lambda () (interage in out)))))))
      (loop))))

;; monta uma lista de tamanho n com o resultado de init-proc.
(define make-list
  (lambda (n init-proc)
    (let loop ((i (- n 1))
               (l '()))
      (if (< i 0)
          l
          (loop (- i 1)
                (cons (init-proc) l))))))

;; lista de atores trabalhadores.
(define *workers*
  (make-list *number-of-workers*
             (lambda ()
               (spawn worker))))

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