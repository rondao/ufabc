;; Monta uma lista de tamanho n com o resultado de 'init-proc'.
(define make-list
  (lambda (n init-proc)
    (let loop ((i (- n 1))
               (l '()))
      (if (< i 0)
          l
          (loop (- i 1)
                (cons (init-proc) l))))))

;; Cria um Pool com n atores de comportamento 'proc'.
(define make-pool
  (lambda (n proc)
    (make-list n (lambda () (spawn proc)))))