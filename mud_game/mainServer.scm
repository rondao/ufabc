;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Operacoes em Matrizes
;; 

;; retorna uma matriz 'n' por 'm'
;; todos os valores iniciais sao 'i'
(define make-matrix
  (lambda (n m i)
    (let ((mat (make-vector (+ 1 (* n m)) i)))
      (vector-set! mat (* n m) m)
      mat)))

;; retorna o tamanho de uma matriz 'mat'
(define matrix-size
  (lambda (mat)
    (- (vector-length mat) 1)))

;; retorna o numero de colunas de uma matriz 'mat'
(define matrix-colums
  (lambda (mat)
    (vector-ref mat (matrix-size mat))))

;; retorna o numero de linhas de uma matriz 'mat'
(define matrix-rows
  (lambda (mat)
    (/ (matrix-size mat) (matrix-colums mat))))

;; retorna a posicao 'i' 'j' no vetor-matriz
(define matrix-pos
  (lambda (mat i j)
    (+ j (* i (matrix-colums mat)))))

;; retorna o valor da posicao 'i' 'j' numa matriz 'mat'
(define matrix-ref
  (lambda (mat i j)
    (vector-ref mat (matrix-pos mat i j))))

;; define o valor da posicao 'i' 'j' numa matriz 'mat' para 'x'
(define matrix-set!
  (lambda (mat i j x)
    (vector-set! mat (matrix-pos mat i j) x)))

;; imprime uma matriz 'mat'
(define matrix-plot
  (lambda (mat)
    (let loop ((i 0))
      (cond ((< i (matrix-size mat))
             (display (vector-ref mat i))
             (if (= (modulo (+ i 1) (matrix-colums mat)) 0)
                 (newline))
             (loop (+ 1 i)))))))

;; imprime uma matriz 'mat' na saida 'out'
(define matrix-plot-out
  (lambda (mat out)
    (let loop ((i 0))
      (cond ((< i (matrix-size mat))
             (display (vector-ref mat i) out)
             (if (= (modulo (+ i 1) (matrix-colums mat)) 0)
                 (newline out))
             (loop (+ 1 i)))))))

;;;;;;;;;;;;;;;;;;;;
;; Leitor de Salas
;;

(use srfi-69)

;; retorna a altura e largura da sala
;; 'in' eh o canal de leitura
;; a leitura eh feita ate encontrar "" ou #eof
(define room-size
  (lambda (in)
    (let reading ((roomHeight 0)
                  (roomWidth 0))
      (let ((l (read-line in)))
        (if (or (eof-object? l) (string=? l ""))
            (values roomHeight roomWidth)
            (reading (+ roomHeight 1) (max roomWidth (string-length l))))))))

;; retorna o numero de objetos da sala
;; 'in' eh o canal de leitura
;; a leitura eh feita ate encontrar "" ou #eof
(define objects-size
  (lambda (in)
    (let reading ((numObjects 0))
      (let ((l (read-line in)))
        (if (or (eof-object? l) (string=? l ""))
            numObjects
            (reading (+ numObjects 1)))))))

;; retorna o numero de portas da sala
;; 'in' eh o canal de leitura
;; a leitura eh feita ate encontrar "" ou #eof
(define doors-size
  (lambda (in)
    (let reading ((numDoors 0))
      (let ((l (read-line in)))
        (if (or (eof-object? l) (string=? l ""))
            numDoors
            (reading (+ numDoors 1)))))))

;; constroi uma matriz '2+n' por '2+m' representando a sala preenchida por "#"
;; o acrescimo de duas unidades eh a borda da sala, garantindo ser fechada
;; o canal 'in' eh lido para se construir a sala
;; a leitura eh feita ate encontrar "" ou #eof
;; a matriz da sala eh retornada
(define construct-room
  (lambda (n m in)
    (let ((room (make-matrix (+ 2 n) (+ 2 m) "#")))
      (let reading ((i 1))
        (let ((l (read-line in)))
          (cond ((or (eof-object? l) (string=? l ""))
                 room)
                (else (let loop ((j 1))
                        (cond ((<= j (string-length l))
                               (matrix-set! room i j (string-ref l (- j 1)))
                               (loop (+ j 1)))))
                      (reading (+ i 1)))))))))

;; constroi uma matriz 'nObj' por '2' representando os objetos da sala
;; o primeiro valor eh o simbolo, e o segundo valor eh a descricao
;; o canal 'in' eh lido para se construir os objetos
;; a leitura eh feita ate encontrar "" ou #eof
;; a matriz dos objetos eh retornada
(define construct-objects
  (lambda (nObj in)
    (let ((objects (make-hash-table)))
      (let reading ((i 0))
        (let ((ch (read-char in)))
          (cond ((or (eof-object? ch) (char=? ch #\newline))
                 objects)
                (else (hash-table-set! objects ch (read-line in))
                      (reading (+ i 1)))))))))

;; constroi uma matriz 'nDoors' por '3' representando as passagens da sala
;; o primeiro valor eh o numero da passagem, e o segundo e terceiro valor sao a posicao (x,y)
;; o canal 'in' eh lido para se construir os objetos
;; a leitura eh feita ate encontrar "" ou #eof
;; a matriz das passagens eh retornada
(define construct-doors
  (lambda (nDoors in)
    (let ((doors (make-matrix nDoors 5 'nada)))
      (let reading ((i 0))
        (let ((ch (peek-char in)))
          (cond ((or (eof-object? ch) (char=? ch #\newline))
                 doors)
                (else
                 (matrix-set! doors i 0 (read in))
                 (matrix-set! doors i 1 (read in))
                 (matrix-set! doors i 2 (read in))
                 (matrix-set! doors i 3 (read in))
                 (matrix-set! doors i 4 (read in))
                 (read-line in)
                 (reading (+ 1 i)))))))))

;; constroi a sala presente no arquivo 'filename'
;; a matriz da sala e a matriz dos objetos eh retornada
(define make-room
  (lambda (filename)
    (let ((in (open-input-file filename)))
      (let-values (((n m) (room-size in)))
        (let ((numObj (objects-size in))
              (numDoors (doors-size in)))
          (close-input-port in)
          (let* ((in2 (open-input-file filename))
                 (room (construct-room n m in2))
                 (objs (construct-objects numObj in2))
                 (doors (construct-doors numDoors in2)))
            (close-input-port in2)
            (values room objs doors)))))))

;; constroi a sala presente no arquivo 'filename'
;; e retorna um fecho com acesso a sala e os objetos
(define make-room-closure
  (lambda (filename)
    (let-values (((room objects doors) (make-room filename)))
      (lambda (msg)
        (case msg
          ((room-get) (lambda () room))
          ((room-ref) (lambda (i j) (matrix-ref room i j)))
          ((room-set!) (lambda (i j x) (matrix-set! room i j x)))
          ((doors-ref) (lambda (i j) (matrix-ref doors i j)))
          ((is-object) (lambda (x) (hash-table-exists? objects x)))
          ((objects-ref) (lambda (x) (if (hash-table-exists? objects x)
                                         (hash-table-ref objects x)
                                         "Nao eh um objeto"))))))))

;;;;;;;;;;;;;;;;;
;; Servidor TCP
;;

(use tcp)
(use srfi-18)

;; cria um fecho contendo as informacoes do cliente
(define make-client-closure
  (lambda (position)
    (let* ((pos position) ;; posicao atual do cliente
           (next pos) ;; proxima posicao desejada
           (last-info #\ ) ;; informacao no mapa da posicao passada
           (item #\ )) ;; item atual
      (lambda (msg)
        (case msg
          ((pos-get) (lambda () pos))
          ((pos-set!) (lambda (x) (set! pos x) pos))
          ((next-get) (lambda () next))
          ((next-set!) (lambda (x) (set! next x) next))
          ((last-get) (lambda () last-info))
          ((last-set!) (lambda (x) (set! last-info x) last-info))
          ((item-get) (lambda () item))
          ((item-set!) (lambda (x) (set! item x) item)))))))


;; recebe dois pares e retorna outro que eh a soma dos primeiros
(define pair-add
  (lambda (pos add)
    (cons (+ (car pos) (car add)) (+ (cdr pos) (cdr add)))))

;; tenta mover para a posicao 'i' 'j' na sala 'room'
;; retorna #t se foi possivel e o valor da posicao se nao foi possivel
(define check-move
  (lambda (roomC pos)
    (let ((info ((roomC 'room-ref) (car pos) (cdr pos))))
      (cond ((char=? info #\ ) "OK")
            ((char=? info #\#) "PAREDE")
            ((char=? info #\X) "Um usuario esta neste local")
            ((char-numeric? info)
             (let ((door (- (char->integer info) 48))) ;; CHAR->INT TOSCO
               (format "PASSAGEM ~a ~a" ((roomC 'doors-ref) door 3) ((roomC 'doors-ref) door 4))))
            (else ((roomC 'objects-ref) info))))))

;; atualiza na sala a movimentacao do cliente
(define move!
  (lambda (roomC clientC)
    (let ((pos ((clientC 'pos-get)))
          (next ((clientC 'next-get))))
      ((roomC 'room-set!) (car pos) (cdr pos) ((clientC 'last-get)))
      ((clientC 'last-set!) ((roomC 'room-ref) (car next) (cdr next)))
      ((clientC 'pos-set!) next)
      ((roomC 'room-set!) (car next) (cdr next) #\X))))

;; tenta soltar o objeto na sala.
;; se conseguir, atualiza na sala.
(define drop-item!
  (lambda (roomC clientC)
    (let ((item ((clientC 'item-get))))
      (if (char=? item #\ )
          "Nao esta segurando item"
          (let ((pos ((clientC 'pos-get))))
            ((clientC 'last-set!) item)
            ((clientC 'item-set!) #\ )
            "Deixou o item")))))

;; tenta pegar um objeto da sala.
;; se conseguir, atualiza na sala.
(define take-item!
  (lambda (roomC clientC)
    (let ((item ((clientC 'item-get)))
          (next ((clientC 'next-get))))
    (if (not (char=? item #\ ))
        "Ja esta segurando um objeto"
        (let ((theItem ((roomC 'room-ref) (car next) (cdr next))))
          (if ((roomC 'is-object) theItem)
              (begin
                ((roomC 'room-set!) (car next) (cdr next) #\ )
                ((clientC 'item-set!) theItem)
                "Pegou o item")
              "Nao ha objeto para pegar"))))))

;; verifica se 'cmd' eh um comando valido e executa o procedimento associado.
;; retorna uma string com a informacao sobre o resultado de 'cmd'.
(define cmd-eval!
  (lambda (cmd roomC clientC)
    (let ((pos ((clientC 'pos-get))))
      (case (string->symbol cmd)
      ((l) (check-move roomC ((clientC 'next-set!) (pair-add pos (cons 0 1)))))
      ((o) (check-move roomC ((clientC 'next-set!) (pair-add pos (cons 0 -1)))))
      ((s) (check-move roomC ((clientC 'next-set!) (pair-add pos (cons 1 0)))))
      ((n) (check-move roomC ((clientC 'next-set!) (pair-add pos (cons -1 0)))))
      ((p) (take-item! roomC clientC))
      ((d) (drop-item! roomC clientC))
      (else "Comando Invalido")))))

;; cria um fecho com uma funcao para interacao com o cliente
;; recebe o fecho da sala 'roomClosure'
;; o fecho contem as informacoes do cliente
(define start-comunication
  (lambda (roomC in out)
    (let* ((posInicial (cons (+ 1 ((roomC 'doors-ref) 0 1)) (+ 1 ((roomC 'doors-ref) 0 2))))
           (clientC (make-client-closure posInicial)))
      (let client-com-loop ((cmd (read-line in)))
        (if (eof-object? cmd)
            (let ((pos ((clientC 'pos-get))))
              ((roomC 'room-set!) (car pos) (cdr pos) ((clientC 'item-get))))
            (let ((info (cmd-eval! cmd roomC clientC)))
              (write-line info out)
              (flush-output out)
              (if (string=? info "OK")
                  (move! roomC clientC))
              (matrix-plot ((roomC 'room-get))) ;; IMPRIME PARA O SERVER
              (client-com-loop (read-line in))))))))

;; cria uma thread para uma conexao 'socket' numa sala de fecho 'roomClosure'
;; esta thread ficara interagindo com o cliente
(define create-client-thread!
  (lambda (in out roomClosure)
    (thread-start! (make-thread
                    (lambda ()
                      (start-comunication roomClosure in out)
                      (close-output-port out))))))

;; cria uma thread para receber uma conexao na porta 'port' por telnet
;; imprime a sala de 'roomClosure' na saida para o telnet
(define map-room-thread!
  (lambda (port roomClosure)
    (thread-start! (make-thread (lambda()
                                  (let ((socket (tcp-listen port)))
                                    (letrec ((loop (lambda ()
                                                     (let-values (((i o) (tcp-accept socket)))
                                                       (matrix-plot-out ((roomClosure 'room-get)) o)
                                                       (close-output-port o)
                                                       (loop)))))
                                      (loop))))))))

;; server entra em loop aguardando conexoes
;; ao encontrar alguma cria uma thread para trabalhar com ela
(define loop-server
  (lambda (socket roomClosure)
    (let-values (((in out) (tcp-accept socket)))
      (create-client-thread! in out roomClosure)
      (loop-server socket roomClosure))))

;; inicia o servidor da sala em 'room-filename'
;; espera conexoes na porta pre-definida
(define start-server
  (lambda ()
    (print "### SCHEME VIRTUAL WORLD ###")
    (print "> Digite a porta de conexao, porta para ver mapa e o arquivo da sala")
    (let* ((port (read))
           (map-port (read))
           (room-filename (symbol->string (read)))
           (roomClosure (make-room-closure room-filename))
           (socket (tcp-listen port)))
      (print "> servidor iniciado!")
      (map-room-thread! map-port roomClosure)
      (loop-server socket roomClosure))))

(start-server)
