;; Tabela hash para armazenar em cache,
;;  e numero minimo de requisicoes para armazenar.
(define *cache* (make-table))
(define *cache-num* 3)

;; Cria uma string para a cache,
;;  ela possui todo o conteudo do arquivo.
(define load-cache
  (lambda (in)
    (let loop ((cache "")
               (linha (read-line in)))
      (if (eof-object? linha)
          cache
          (loop (string-append cache linha "\n") (read-line in))))))

;; Verifica se um arquivo se encontra na cache,
;;  se sim, sera retornado o arquivo diretamente,
;;  se nao, sera contabilizado o numero de requisicoes.
;; Caso as requisicoes atinjam *cache-num*,
;;  o arquivo sera gravado na cache.
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