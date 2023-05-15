;; Copia todo o conteúdo de in para out.
(define copia
  (lambda (in out)
    (let loop ((linha (read-line in)))
      (cond ((not (eof-object? linha))
             (display linha out)
             (newline out)
             (loop (read-line in)))))))

;; Verifica se a requisicao eh da forma GET / /HTTP.
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

;; O requerimento "/" se torna "/index.html".
;; Outras regras de reescrita podem ser incluidas aqui.
(define trata-caminho
  (lambda (str)
    (if (string=? str "/")
        "/index.html"
        str)))

;; Checa se o requerimento acessa algum diretorio proibido.
;; Os diretorios sao passados numa lista.
;; Utiliza continuacoes como um return.
;; Seleciona o inicio de cada diretorio (e.g. "foo/..."),
;;  e compara com cada um da lista de proibidos.
(define caminho-proibido
  (lambda (cam proib-list)
    (call/cc
     (lambda (return)
       (let loop ((dir cam)
                  (index (string-index cam #\/)))
         (if (eqv? index #f)
             #f
             (let ((sub-dir (substring dir (+ index 1) (string-length dir))))
               (if (= (string-length sub-dir) 0)
                   #f
                   (begin
                     (for-each (lambda (proib)
                                 (if (= (string-prefix-length proib sub-dir) (string-length proib))
                                     (return #t))) proib-list)
                     (loop sub-dir (string-index sub-dir #\/)))))))))))

;; Envia o cabecalho HTTP padrao para a saida out.
;; type contem o Content-Type desejado.
(define send-headers
  (lambda (out type)
    (display "HTTP/1.0 200 OK\n" out)
    (display (string-append "Content-Type: " type "\n") out)))