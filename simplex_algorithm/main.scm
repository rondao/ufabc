(use vector-lib)
(use srfi-1)

;; Ajuda
(define help
  (lambda ()
    (print "#### Erro ao ler o problema ####")
    (print "O programa le um arquivo de entrada com o problema.")
    (print "O arquivo deve se chamar \"entrada.txt\" e estar no mesmo diretorio do programa")
    (print "O formato do arquivo deve ser como o abaixo.")
    (newline)
    (print "n m")
    (print "[max/min] c1 c2 c3 ... cn")
    (print "c11 c12 c13 ... c1n [<=/>=] b1")
    (print "...")
    (print "cm1 cm2 cm3 ... cmn [<=/>=] bm")
    (newline)
    (print "Onde 'n' eh o numero de variaveis e 'm' eh o numero de restricoes.")
    (print "Na segunda linha eh a funcao objetivo, e ci eh o coeficiente da variavel xi")
    (print "Nas linhas subsequentes sao as restricoes, e cij eh o coeficiente da variavel xj na restricao i")
    (newline)
    (exit)))

;; Guarda se algum Bi eh negativo
(define bi-negative #f)
(define bi-negative2 #f)

;; Cria o vetor 'A' com 'n' variaveis e 'm' restricoes
;; que contera a funcao objetivo e as restricoes.
(define make-A
  (lambda (in)
    (let* ((n (read in))
           (m (read in))
           (A (make-vector (+ m n 2) #f)))
      (values A m n))))

;; Le a funcao objetivo e retorna um vetor contendo as informacoes.
;; Se o problema for de minimizacao, este eh convertido para maximizacao multiplicando por -1.
(define read-z
  (lambda (in m n)
    (let ((otm (read in))
          (Z (make-vector (+ m n 2) 0))) ;; Numero de variaveis + Variaveis de folga + Constante + X0
      (let loop ((i 1))
        (if (<= i n)
            (begin
              (vector-set! Z i (read in))
              (loop (+ i 1)))))
      (if (eqv? otm 'min)
          (vector-map (lambda (i x) (- x)) Z)
          Z))))

;; Le uma restricao e retorna um vetor contendo as informacoes.
;; O vetor ja esta com a variavel de folga em evidencia.
(define read-constrain
  (lambda (in m n)
    (let ((Ax (make-vector (+ m n 2) 0)))
      (let loop ((i 1))
        (if (<= i n)
            (begin
              (vector-set! Ax i (read in))
              (loop (+ i 1)))))
      (let ((op (read in))
            (bx (read in)))
        (cond
         ((eqv? op '<=) (vector-map! (lambda (i x) (- x)) Ax))
         ((eqv? op '>=) (set! bx (- bx)))
         (else (help)))
        (vector-set! Ax (- (vector-length Ax) 1) bx)
        (if (negative? bx)
            (begin
              (set! bi-negative #t)
              (set! bi-negative2 #t))))
      Ax)))

;; Constroi o problema de programacao linear na matriz A.
;; A primeira linha de A contem a funcao objetivo,
;; e nas demais i linhas contem #f se a variavel i nao esta na base
;; e contem um vetor com seu valor caso pertenca a base.
(define read-linprog
  (lambda (in)
    (let-values (((A m n) (make-A in)))
      (vector-set! A (- (vector-length A) 1) (read-z in m n))
      (let loop-rs ((i 1))
        (if (<= i m)
            (let ((Ax (read-constrain in m n)))
              (vector-set! A (+ n i) Ax)
              (loop-rs (+ i 1)))))
      A)))

;; Retorna uma matriz contendo o problema de programacao linear
;; lido do arquivo 'filename'.
(define load-linear-program
  (lambda (filename)
    (let* ((in (open-input-file filename))
           (A (read-linprog in)))
      (close-output-port in)
      A)))

;; Imprime a funcao objetivo e as retricoes de um problema de programacao linear
(define print-lpp
  (lambda (A)
    (let ((n+m (vector-length A)))
      (let loop ((i 0))
        (if (< i n+m)
            (let ((Ax (vector-ref A i)))
              (if (not (eqv? Ax #f))
                  (begin
                    (newline)
                    (if (= i (- n+m 1))
                        (display "Z  = ")
                        (display (format "X~a = " i)))
                  (let loop ((j 0))
                    (if (< j n+m)
                        (begin
                          (display (vector-ref Ax j))
                          (if (< j (- n+m 1))
                              (display (format ".X~a " j)))
                          (loop (+ j 1)))))))
              (loop (+ i 1))))))))

;; Encontra qual variavel deve entrar na base.
(define find-in-var
  (lambda (A m+n)
    (let ((Z (vector-ref A m+n)))
      (let loop ((i 0))
        (if (>= i m+n)
            #f
            (if (positive? (vector-ref Z i))
                i
                (loop (+ i 1))))))))

;; Retorna um vetor contendo os valores de restricao para a variavel que ira entrar na base.
;; A variavel basica de menor valor associado sera a variavel a sair da base.
(define cons-out-vars
  (lambda (A i m+n)
    (let ((vars (make-vector m+n #f)))
      (let loop ((j 0))
        (if (< j m+n)
            (let ((vb (vector-ref A j)))
              (if (and (not (eqv? vb #f)) (negative? (vector-ref vb i)))
                  (vector-set! vars j (/ (- (vector-ref vb m+n)) (vector-ref vb i))));;;;;;;;;;;;;;;;;;;;;;
              (loop (+ j 1)))))
      vars)))

;; Encontra qual variavel deve sair da base.
(define find-out-var
  (lambda (A i m+n)
    (let ((vars (cons-out-vars A i m+n))
          (min #f)
          (xi #f))
      (let loop ((i 0))
        (if (< i m+n)
            (let ((val (vector-ref vars i)))
              (if (and (not (eqv? val #f)) (not (negative? val)))
                  (if (or (eqv? min #f) (< val min))
                      (begin
                        (set! min val)
                        (set! xi i))))
              (loop (+ i 1)))
            xi)))))

;; Coloca a variavel 'i' na base, substituindo a variavel 'o' que estava na base.
(define switch!
  (lambda (A i o)
    (let* ((xo (vector-ref A o))
           (cxi (- (vector-ref xo i))))
      (vector-set! xo i 0)
      (vector-set! xo o -1)
      (vector-set! A i (vector-map (lambda (i x) (/ x cxi)) xo))
      (vector-set! A o #f))))

;; Realiza o pivotamento da nova variavel basica 'i'.
;; Ela eh substituida em todas as suas ocorrencias.
(define pivoting!
  (lambda (A i m+n)
    (let ((xi (vector-ref A i)))
      (let loop ((j 0))
        (if (<= j m+n)
            (let ((xb (vector-ref A j)))
              (if (and (not (eqv? xb #f)) (not (= i j)))
                  (let* ((ci (vector-ref xb i))
                         (cxi (vector-map (lambda (i x) (* x ci)) xi)))
                    (vector-set! A j (vector-map (lambda (i x) (+ x (vector-ref cxi i))) xb))
                    (vector-set! (vector-ref A j) i 0)))
              (loop (+ j 1))))))))

;; Encontra o maior coeficiente negativo para a primeira substituicao
;; do problema auxiliar.
(define find-aux-out-var
  (lambda (lpp)
    (let ((n+m (- (vector-length lpp) 1))
          (min #f)
          (xi #f))
      (let loop ((i 0))
        (if (< i n+m)
            (let ((Ax (vector-ref lpp i)))
              (if (not (eqv? Ax #f))
                  (let ((val (vector-ref (vector-ref lpp i) n+m)))
                    (vector-set! Ax 0 1)
                    (if (or (eqv? min #f) (< val min))
                        (begin
                          (set! min val)
                          (set! xi i)))))
                    (loop (+ i 1))))
            xi))))

;; Soluciona o problema de programacao linear auxiliar
;; para se encontrar uma solucao inicial.
(define solve-aux!
  (lambda (lpp m+n)
    (set! bi-negative #f)
    (print (format "~%#### Inicio do Problema Auxiliar ####~%"))
    (let* ((Z (vector->list (vector-ref lpp m+n)))
           (newZ (make-vector (length Z) 0)))
      (vector-set! newZ 0 -1)
      (vector-set! lpp m+n newZ)
      (let ((xo (find-aux-out-var lpp)))
        (if (eqv? xo #f)
            (error "ERRO INICIO PROBLEMA AUXILIAR"))
        (switch! lpp 0 xo)
        (pivoting! lpp 0 m+n))
      (solve! lpp)
      (print (format "~%#### Fim do Problema Auxiliar ####~%"))
      (vector-set! lpp m+n (list->vector Z)))))

;; Soluciona um problema de programacao linear.
(define solve!
  (lambda (lpp)
    (let ((m+n (- (vector-length lpp) 1)))
      (if (eqv? bi-negative #t)
          (begin
            (solve-aux! lpp m+n)
            (let loop ((i 0))
              (if (< i m+n)
                  (let ((Ax (vector-ref lpp i)))
                    (if (not (eqv? Ax #f))
                        (begin
                          (vector-set! Ax 0 0)
                          (pivoting! lpp i m+n)))
                    (loop (+ i 1)))))))
      (let loop ((i 1))
        (print (format "~%Iteracao ~a~%" i))
        (print-lpp lpp)
        (newline)
        (let ((xi (find-in-var lpp m+n)))
          (if (not (eqv? xi #f))
              (let ((xo (find-out-var lpp xi m+n)))
                (if (eqv? xo #f)
                    (begin
                      (print-lpp lpp)
                      (print (format "~%### PROBLEMA ILIMITADO ###~%"))
                      (exit)))
                (switch! lpp xi xo)
                (pivoting! lpp xi m+n)
                (loop (+ i 1)))))))))

(define linearProgram (load-linear-program "entrada.txt"))
(solve! linearProgram)

(if (eqv? bi-negative2 #t)
    (let* ((m+n (- (vector-length linearProgram) 1))
           (Z (vector-ref linearProgram m+n)))
      (vector-set! Z m+n (- (vector-ref Z m+n)))))

(newline)
(print "#### Fim do problema ####")