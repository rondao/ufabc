;;; This is an implementation of the SRFI for charsets; it is restricted
;;; to latin1 character sets, i.e., with (char->integer ch) < 256.

;;;; Missing char-set-cursor and company.

(declare (fixnum) (not safe))

(define-structure char-set body)

(define (##check-arg pred val caller)
  (if (not (pred val)) (error "Bad argument type" val caller)))

(define (##check-char-set cs proc)
  (or (char-set? cs)
      (error "Not a char-set" cs proc)))

(define (##char->integer/check ch proc)
  (let ((x (char->integer ch)))
    (if (and (>= x 0) (< x 256))
        x
        (error "Character out of range" x proc))))

(define (##char-set-body/check cs proc)
  (if (char-set? cs) (char-set-body cs)
      (error "Not a char-set" cs proc)))

(##define-macro (##map! proc lst)
  `(let ((proc ,proc)
         (lst ,lst))
     (let loop ((l lst))
       (if (null? l)
           lst
           (begin
             (set-car! l (proc (car l)))
             (loop (cdr l)))))))

(##define-macro (##reverse-map proc lst)
  `(let ((proc ,proc))
     (let loop ((l ,lst) (result '()))
       (if (null? l)
           result
           (loop (cdr l)
                 (cons (proc (car l)) result))))))

(##define-macro (##for-each! proc lst)
  `(let ((proc ,proc)
         (lst ,lst))
     (let loop ((l lst))
       (if (not (null? l))
           (begin
             (proc (car l))
             (loop (cdr l)))))))

(##define-macro (##char-set-body-for-each proc cs-body)
  `(let ((proc ,proc)
         (cs-body ,cs-body))
     (let outer ((index 0) (base 0))
       (if (< index 16)
           (let ((block (##u16vector-ref cs-body index)))
             (if (zero? block)
                 (outer (+ index 1) (+ base 16))
                 (let inner ((i 0))
                   (if (> i 15)
                       (outer (+ index 1) (+ base 16))
                       (if (zero? (##fixnum.bitwise-and block (##fixnum.arithmetic-shift-left 1 i)))
                           (inner (+ i 1))
                           (begin
                             (proc (integer->char (+ base i)))
                             (inner (+ i 1))))))))))))

(##define-macro (##char-set-body-over-blocks head proc)
  `(,head ,@(do ((i 15 (- i 1))
                 (ans '() (cons (eval (list proc i)) ans)))
                ((< i 0) ans))))

(define (##char-set-body-copy x)
  (##char-set-body-over-blocks ##u16vector (lambda (i) `(##u16vector-ref x ,i))))

(define (##char-set-empty-body)
  (##char-set-body-over-blocks ##u16vector (lambda (i) 0)))

(define (##char-set-full-body)
  (##char-set-body-over-blocks ##u16vector (lambda (i) 65535)))

(define (char-set-copy x) ; newly allocated char-set-body, too
  (make-char-set
   (##char-set-body-copy (##char-set-body/check x char-set-copy))))


(##define-macro (##char-set= cs1-body cs2-body)
  `(let ((cs1-body ,cs1-body)
         (cs2-body ,cs2-body))
     (or (eq? cs1-body cs2-body)
         (##char-set-body-over-blocks
          and
          (lambda (i)
            `(= (##u16vector-ref cs2-body ,i)
                (##u16vector-ref cs1-body ,i)))))))

;;;; XXX: (char-set=) should return #t
(define (char-set= cs1 . rest)
  (let ((s1 (##char-set-body/check cs1 char-set=)))
    (let lp ((rest rest))
      (or (not (pair? rest))
          (let ((car-rest (car rest)))
            (and (##char-set= s1 (##char-set-body/check car-rest char-set=))
                 (lp (cdr rest))))))))

(##define-macro (##char-set<= cs1-body cs2-body)
  `(let ((cs1-body ,cs1-body)
         (cs2-body ,cs2-body))
     (or (eq? cs1-body cs2-body)
         (##char-set-body-over-blocks
          and
          (lambda (i)
            `(zero? (##fixnum.bitwise-and (##fixnum.bitwise-not (##u16vector-ref cs2-body ,i))
                                          (##u16vector-ref cs1-body ,i))))))))

(define (char-set<= cs1 . rest)
  (let lp ((s1 (##char-set-body/check cs1 char-set<=))  (rest rest))
    (or (not (pair? rest))
        (let ((car-rest (car rest)))
          (let ((s2 (##char-set-body/check car-rest char-set<=)))
            (and (##char-set<= s1 s2)
                 (lp s2 (cdr rest))))))))

(##define-macro (macro-char-set-body-1? cs-body x)
  `(let ((x ,x)
         (cs-body ,cs-body))
     (let ((index (##fixnum.arithmetic-shift-right x 4))
           (shift (##fixnum.bitwise-and x 15)))
       (not (zero? (##fixnum.bitwise-ior (##u16vector-ref cs-body index)
                                         (##fixnum.arithmetic-shift-left 1 shift)))))))

;;; Hash
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Compute (c + 37 c + 37^2 c + ...) modulo BOUND.
;;; If you keep BOUND small enough, the intermediate calculations will
;;; always be fixnums. How small is dependent on the underlying Scheme system;
;;; we use a default BOUND of 2^22 = 4194304, which should hack it in
;;; Schemes that give you at least 29 signed bits for fixnums. The core
;;; calculation that you don't want to overflow is, worst case,
;;;     (+ 65535 (* 37 (- bound 1)))
;;; where 65535 is the max character code. Choose the default BOUND to be the
;;; biggest power of two that won't cause this expression to fixnum overflow,
;;; and everything will be copacetic.

(define (char-set-hash cs #!optional (bound 4194304))
  (or (and (integer? bound)
           (<= 0 bound)
           (<= bound 4194304))
      (error "Bad bound" bound char-set-hash))
  (let ((body (##char-set-body/check cs char-set-hash))
        (bound (if (zero? bound) 4194304 bound))
        (ans 0))
    (##char-set-body-for-each (lambda (ch)
                                (set! ans (remainder (+ (char->integer ch)
                                                        (* 37 ans))
                                                     bound)))
                              body)
    ans))

(define (char-set-hash-ci cs #!optional (bound 4194304))
  (or (and (integer? bound)
           (<= 0 bound)
           (<= bound 4194304))
      (error "Bad bound" bound char-set-hash-ci))
  (let ((body (##char-set-body/check cs char-set-hash-ci))
        (bound (if (zero? bound) 4194304 bound))
        (ans 0))
    (let ((ans 0))
      (##char-set-body-for-each (lambda (ch)
                                  (set! ans (remainder (+ (char->integer (char-downcase ch))
                                                          (* 37 ans))
                                                       bound)))
                                body)
      ans)))

(define (##char-set-size cs)
  (let ((table '#(0 1 1 2 1 2 2 3 1 2 2 3 2 3 3 4))
        (cs-body (char-set-body cs)))
    (##char-set-body-over-blocks
     +
     (lambda (i)
       `(let ((word (##u16vector-ref cs-body ,i)))
          (+ (vector-ref table (##fixnum.bitwise-and 15 word))
             (vector-ref table (##fixnum.bitwise-and 15 (##fixnum.arithmetic-shift-right word 4)))
             (vector-ref table (##fixnum.bitwise-and 15 (##fixnum.arithmetic-shift-right word 8)))
             (vector-ref table (##fixnum.arithmetic-shift-right word 12))))))))

(define (char-set-size cs)
  (##check-char-set cs char-set-size)
  (##char-set-size cs))

(define (char-set-count pred cs)
  (##check-arg procedure? pred char-set-count)
  (let ((body (##char-set-body/check cs char-set-count))
        (count 0))
    (##char-set-body-for-each (lambda (ch)
                                (if (pred ch)
                                    (set! count (+ count 1))))
                              body)
    count))

(##define-macro (macro-char-set-body-set-1! cs-body x)
  `(let ((x ,x)
         (cs-body ,cs-body))
     (let ((index (##fixnum.arithmetic-shift-right x 4))
           (shift (##fixnum.bitwise-and x 15)))
       (##u16vector-set! cs-body index (##fixnum.bitwise-ior (##u16vector-ref cs-body index)
                                                        (##fixnum.arithmetic-shift-left 1 shift))))))

(##define-macro (macro-char-set-body-set-1? cs-body x)
  `(let ((x ,x)
         (cs-body ,cs-body))
     (let ((index (##fixnum.arithmetic-shift-right x 4))
           (shift (##fixnum.bitwise-and x 15)))
       (not (zero? (##fixnum.bitwise-and (##u16vector-ref cs-body index)
                                    (##fixnum.arithmetic-shift-left 1 shift)))))))

(define (char-set-contains? cs ch)
  (let ((index (##char->integer/check ch char-set-contains?))
        (body (##char-set-body/check cs char-set-contains?)))
    (and (<= 0 index 255)
         (macro-char-set-body-set-1? body (char->integer ch)))))

(##define-macro (macro-char-set-body-set-0! cs-body x)
  `(let ((x ,x)
         (cs-body ,cs-body))
     (let ((index (##fixnum.arithmetic-shift-right x 4))
           (shift (##fixnum.bitwise-and x 15)))
       (##u16vector-set! cs-body index (##fixnum.bitwise-and (##u16vector-ref cs-body index)
                                                        (##fixnum.bitwise-not (##fixnum.arithmetic-shift-left 1 shift)))))))

(##define-macro (##char-set-body-adjoin! cs-body ints cont)
  `(let ((cs-body ,cs-body)
         (ints ,ints)
         (cont ,cont))
     (let loop ((ints ints))
       (if (null? ints)
           (cont cs-body)
           (begin
             (macro-char-set-body-set-1! cs-body (car ints))
             (loop (cdr ints)))))))

(define (char-set-adjoin cs . chars)
  (##check-char-set cs char-set-adjoin)
  (##char-set-body-adjoin! (##char-set-body-copy (char-set-body cs))
                           (##map! (lambda (ch)
                                     (##check-arg char? ch char-set-adjoin)
                                     (##char->integer/check ch char-set-adjoin))
                                   chars)
                           make-char-set))

(define (char-set-adjoin! cs . chars)
  (##check-char-set cs char-set-adjoin!)
  (##char-set-body-adjoin! (char-set-body cs)
                            (##map! (lambda (ch)
                                      (##check-arg char? ch char-set-adjoin!)
                                      (##char->integer/check ch char-set-adjoin!))
                                    chars)
                            (lambda (body) cs)))

(##define-macro (##char-set-body-delete! cs-body ints cont)
  `(let ((cs-body ,cs-body)
         (ints ,ints)
         (cont ,cont))
     (let loop ((ints ints))
       (if (null? ints)
           (cont cs-body)
           (begin
             (macro-char-set-body-set-0! cs-body (car ints))
             (loop (cdr ints)))))))

(define (char-set-delete cs . chars)
  (##check-char-set cs char-set-delete)
  (##char-set-body-delete! (##char-set-body-copy (char-set-body cs))
                           (##map! (lambda (ch)
                                     (##check-arg char? ch char-set-delete)
                                     (##char->integer/check ch char-set-delete))
                                   chars)
                           make-char-set))

(define (char-set-delete! cs . chars)
  (##check-char-set cs char-set-delete!)
  (##char-set-body-delete! (char-set-body cs)
                           (##map! (lambda (ch)
                                     (##check-arg char? ch char-set-delete!)
                                     (##char->integer/check ch char-set-delete!))
                                   chars)
                           (lambda (body) cs)))


(define (char-set-map proc cs)
  (##check-arg procedure? proc char-set-map)
  (##check-char-set cs char-set-map)
  (let ((new-char-set-body (##char-set-empty-body)))
    (##char-set-body-for-each (lambda (ch)
                                (let ((result (proc ch)))
                                  (##check-arg char? result char-set-map)
                                  (let ((int-result (##char->integer/check result char-set-map)))
                                    (macro-char-set-body-set-1! new-char-set-body int-result))))
                              (char-set-body cs))
    (make-char-set new-char-set-body)))

(define (char-set-for-each proc cs)
  (##check-arg procedure? proc char-set-for-each)
  (let ((body (##char-set-body/check cs char-set-for-each)))
    (##char-set-body-for-each proc body)))

(define (char-set-fold kons knil cs)
  (##check-arg procedure? kons char-set-fold)
  (let ((body (##char-set-body/check cs char-set-fold))
        (ans knil))
    (##char-set-body-for-each (lambda (ch)
                                (set! ans (kons ch ans)))
                              body)
    ans))

(##define-macro (##char-set-unfold p f g seed body cont)
  `(let ((p ,p) (f ,f) (g ,g) (seed ,seed) (body ,body) (cont ,cont))
     (let lp ((seed seed))
       (cond ((not (p seed))                                             ; P says we are done.
              (macro-char-set-body-set-1! body (char->integer (f seed))) ; Add (F SEED) to set.
              (lp (g seed)))                                             ; Loop on (G SEED).
             (else
              (cont body))))))

(define (char-set-unfold p f g seed #!optional base)
  (##check-arg procedure? p char-set-unfold)
  (##check-arg procedure? f char-set-unfold)
  (##check-arg procedure? g char-set-unfold)
  (let ((body (if base
                  (##char-set-body-copy (##char-set-body/check base char-set-unfold))
                  (##char-set-empty-body))))
    (##char-set-unfold p f g seed body make-char-set)))

(define (char-set-unfold! p f g seed base)
  (##check-arg procedure? p char-set-unfold)
  (##check-arg procedure? f char-set-unfold)
  (##check-arg procedure? g char-set-unfold)
  (let ((body (##char-set-body/check base char-set-unfold!)))
    (##char-set-unfold p f g seed body (lambda (body) base))))

(define (char-set-every pred cs)
  (##check-arg procedure? pred char-set-every)
  (let ((body (##char-set-body/check cs char-set-every)))
    (call-with-current-continuation
     (lambda (exit)
       (##char-set-body-for-each (lambda (ch)
                                   (or (pred ch) (exit #f)))
                                 body)
       #t))))

(define (char-set-any pred cs)
  (##check-arg procedure? pred char-set-any)
  (let ((body (##char-set-body/check cs char-set-any)))
    (call-with-current-continuation
     (lambda (exit)
       (##char-set-body-for-each (lambda (ch)
                                   (and (pred ch) (exit #t)))
                                 body)
       #f))))

(define (char-set . chars)
  (##char-set-body-adjoin! (##char-set-empty-body)
                           (##map! (lambda (ch)
                                     (##check-arg char? ch char-set)
                                     (##char->integer/check ch char-set))
                                   chars)
                           make-char-set))

(define (list->char-set chars #!optional bs)
  (or (and (list? chars)
           (##char-set-body-adjoin!
            (if bs
                (##char-set-body-copy (##char-set-body/check bs list->char-set))
                (##char-set-empty-body))
            (##reverse-map (lambda (ch)
                             (##check-arg char? ch list->char-set)
                             (##char->integer/check ch list->char-set))
                           chars)
            make-char-set))
      (error "list->char-set: chars is not a list" chars)))

(define (list->char-set! chars bs)
  (let ((body (##char-set-body/check bs list->char-set!)))
    (if (list? chars)
        (##char-set-body-adjoin! body
                                 (##reverse-map (lambda (ch)
                                                  (##check-arg char? ch list->char-set!)
                                                  (##char->integer/check ch list->char-set!))
                                                chars)
                                 (lambda (body) bs))
        (error "list->char-set!: chars is not a list" chars))))

(define (char-set->list cs)
  (let ((body (##char-set-body/check cs char-set->list))
        (result '()))
    (##char-set-body-for-each (lambda (ch)
                                (set! result (cons ch result)))
                              body)
    result))

(define (string->char-set str #!optional bs)
  (##check-arg string? str string->char-set)
  (let ((bs-body (if bs
                     (##char-set-body-copy (##char-set-body/check bs string->char-set))
                     (##char-set-empty-body))))
    (do ((i (- (string-length str) 1) (- i 1)))
        ((< i 0) (make-char-set bs-body))
      (macro-char-set-body-set-1!
       bs-body
       (##char->integer/check (string-ref str i) string->char-set)))))

(define (string->char-set! str bs)
  (##check-arg string? str string->char-set!)
  (let ((bs-body (##char-set-body/check bs string->char-set!)))
    (do ((i (- (string-length str) 1) (- i 1)))
        ((< i 0) bs)
      (macro-char-set-body-set-1!
       bs-body
       (##char->integer/check (string-ref str i) string->char-set!)))))

(define (char-set->string cs)
  (let* ((body (##char-set-body/check cs char-set->string))
         (n (##char-set-size cs))
         (str (make-string n)))
    (let ((i 0))
      (##char-set-body-for-each (lambda (ch)
                                  (string-set! str i ch)
                                  (set! i (+ i 1)))
                                body)
      str)))

;;; -- predicate -> char-set

(##define-macro (##predicate->char-set! pred body cont)
  `(let ((pred ,pred)
         (body ,body)
         (cont ,cont))
     (let lp ((i 255))
       (cond ((>= i 0)
              (if (pred (integer->char i))
                  (macro-char-set-body-set-1! body i))
              (lp (- i 1)))
             (else
              (cont body))))))

(define (predicate->char-set predicate #!optional base)
  (##check-arg procedure? predicate predicate->char-set)
  (let ((body (if base
                  (##char-set-body-copy (##char-set-body/check base predicate->char-set))
                  (##char-set-empty-body))))
    (##predicate->char-set! predicate body make-char-set)))

(define (predicate->char-set! predicate base)
  (##check-arg procedure? predicate predicate->char-set!)
  (let ((base-body (##char-set-body/check base predicate->char-set!)))
    (##predicate->char-set! predicate base-body (lambda (body) base))))

(define (->char-set x)
  (cond ((char-set? x) x)
        ((string? x) (string->char-set x))
        ((char? x) (char-set x))
        ((procedure? x) (predicate->char-set x))
        (else (error "->char-set: Not a charset, string, or char."
                     x))))

(##define-macro (##char-set-body-invert! body)
  `(let ((body ,body))
     (##char-set-body-over-blocks
      begin
      (lambda (i)
        `(##u16vector-set! body ,i (##fixnum.bitwise-and 65535
                                                         (##fixnum.bitwise-not (##u16vector-ref body ,i))))))))

(##define-macro (##char-set-body-union! cs1-body cs2-body)
  `(let ((cs1-body ,cs1-body)
         (cs2-body ,cs2-body))
     (##char-set-body-over-blocks
      begin
      (lambda (i)
        `(##u16vector-set! cs1-body ,i (##fixnum.bitwise-ior (##u16vector-ref cs1-body ,i)
                                                             (##u16vector-ref cs2-body ,i)))))))

(##define-macro (##char-set-body-xor! cs1-body cs2-body)
  `(let ((cs1-body ,cs1-body)
         (cs2-body ,cs2-body))
     (##char-set-body-over-blocks
      begin
      (lambda (i)
        `(##u16vector-set! cs1-body ,i (##fixnum.bitwise-xor (##u16vector-ref cs1-body ,i)
                                                             (##u16vector-ref cs2-body ,i)))))))

(##define-macro (##char-set-body-intersection! cs1-body cs2-body)
  `(let ((cs1-body ,cs1-body)
         (cs2-body ,cs2-body))
     (##char-set-body-over-blocks
      begin
      (lambda (i)
        `(##u16vector-set! cs1-body ,i (##fixnum.bitwise-and (##u16vector-ref cs1-body ,i)
                                                             (##u16vector-ref cs2-body ,i)))))))

(##define-macro (##char-set-body-difference! cs1-body cs2-body)
  `(let ((cs1-body ,cs1-body)
         (cs2-body ,cs2-body))
     (##char-set-body-over-blocks
      begin
      (lambda (i)
        `(##u16vector-set! cs1-body ,i (##fixnum.bitwise-and (##u16vector-ref cs1-body ,i)
                                                             (##fixnum.bitwise-not (##u16vector-ref cs2-body ,i))))))))

(##define-macro (##char-set-body-diff+intersection! cs1-body cs2-body)
  `(let ((cs1-body ,cs1-body)
         (cs2-body ,cs2-body))
     (##char-set-body-over-blocks
      begin
      (lambda (i)
        `(let ((word1 (##u16vector-ref cs1-body ,i))
               (word2 (##u16vector-ref cs2-body ,i)))
           (let ((diff  (##fixnum.bitwise-and word1 (##fixnum.bitwise-not word2)))
                 (inter (##fixnum.bitwise-and word1 word2)))
             (##u16vector-set! cs1-body ,i diff)
             (##u16vector-set! cs2-body ,i inter)))))))

(define (char-set-invert cs)
  (let ((body (##char-set-body-copy (##char-set-body/check cs char-set-invert))))
    (make-char-set (##char-set-body-invert! body))))

(define (char-set-invert! cs)
  (begin
    (##char-set-body-invert! (##char-set-body/check cs char-set-invert!))
    cs))

(define (char-set-union . csets)
  (let ((body (##char-set-empty-body)))
    (##for-each! (lambda (cs)
                   (let ((cs-body (##char-set-body/check cs char-set-union)))
                     (##char-set-body-union! body cs-body)))
                 csets)
    (make-char-set body)))

(define (char-set-union! cset1 . csets)
  (let ((body (##char-set-body/check cset1 char-set-union!)))
    (##for-each! (lambda (cs)
                   (let ((cs-body (##char-set-body/check cs char-set-intersection)))
                     (##char-set-body-union! body cs-body)))
                 csets)
    cset1))

(define (char-set-intersection . csets)
  (let ((body (##char-set-full-body)))
    (##for-each! (lambda (cs)
                   (let ((cs-body (##char-set-body/check cs char-set-intersection)))
                     (##char-set-body-intersection! body cs-body)))
                 csets)
    (make-char-set body)))

(define (char-set-intersection! cset1 . csets)
  (let ((body (##char-set-body/check cset1 char-set-intersection!)))
    (##for-each! (lambda (cs)
                   (let ((cs-body (##char-set-body/check cs char-set-intersection!)))
                     (##char-set-body-intersection! body cs-body)))
                 csets)
    cset1))

(define (char-set-xor . csets)
  (let ((body (##char-set-empty-body)))
    (##for-each! (lambda (cs)
                   (let ((cs-body (##char-set-body/check cs char-set-xor)))
                     (##char-set-body-xor! body cs-body)))
                 csets)
    (make-char-set body)))

(define (char-set-xor! cset1 . csets)
  (let ((body (##char-set-body/check cset1 char-set-xor!)))
    (##for-each! (lambda (cs)
                   (let ((cs-body (##char-set-body/check cs char-set-xor!)))
                     (##char-set-body-xor! body cs-body)))
                 csets)
    cset1))

(define (char-set-difference cs1 . csets)
  (##check-char-set cs1 char-set-difference)
  (let ((body (##char-set-body-copy (char-set-body cs1))))
    (##for-each! (lambda (cs)
                   (let ((cs-body (##char-set-body/check cs char-set-difference)))
                     (##char-set-body-difference! body cs-body)))
                 csets)
    (make-char-set body)))

(define (char-set-difference! cs1 . csets)
  (let ((body (##char-set-body/check cs1 char-set-difference!)))
    (##for-each! (lambda (cs)
                   (let ((cs-body (##char-set-body/check cs char-set-difference!)))
                     (##char-set-body-difference! body cs-body)))
                 csets)
    cs1))

;;; -- Difference & intersection

(define (char-set-diff+intersection! cs1 cs2)
  (let ((body1 (##char-set-body/check cs1 char-set-diff+intersection!))
        (body2 (##char-set-body/check cs2 char-set-diff+intersection!)))
    (begin
      (##char-set-body-diff+intersection! body1 body2)
      (values cs1 cs2))))

(define (char-set-diff+intersection cs1 cs2)
  (let ((body1 (##char-set-body/check cs1 char-set-diff+intersection))
        (body2 (##char-set-body/check cs2 char-set-diff+intersection)))
    (let ((cp1 (##char-set-body-copy body1))
          (cp2 (##char-set-body-copy body2)))
      (##char-set-body-diff+intersection! cp1 cp2)
      (values (make-char-set cp1) (make-char-set cp2)))))

(define char-set:empty
  (make-char-set (##char-set-empty-body)))

(define char-set:full
  (make-char-set (##char-set-full-body)))

;;; -- UCS-range -> char-set

(define (##range->char-set-body! x y body cont)
  (let ((first-block-index (##fixnum.arithmetic-shift-right x 4))
        (last-block-index (##fixnum.arithmetic-shift-right y 4)))
    (let loop ((i (+ first-block-index 1)))
      (cond ((< i last-block-index)
             (##u16vector-set! body i 65535)
             (loop (+ i 1)))
            (else
             (if (= first-block-index last-block-index)
                 (##u16vector-set! body first-block-index
                                 (##fixnum.bitwise-ior (##u16vector-ref body first-block-index)
                                                  (##fixnum.bitwise-and (##fixnum.arithmetic-shift-right 65535
                                                                                  (- 16
                                                                                     (##fixnum.bitwise-and y 15)))
                                                                   (##fixnum.arithmetic-shift-left 65535
                                                                                 (##fixnum.bitwise-and x 15)))))
                 (begin
                   (if (< last-block-index 16)
                       (##u16vector-set! body last-block-index
                                         (##fixnum.bitwise-ior (##u16vector-ref body last-block-index)
                                                          (##fixnum.arithmetic-shift-right 65535
                                                                         (- 16
                                                                            (##fixnum.bitwise-and y 15))))))
                   (##u16vector-set! body first-block-index
                                     (##fixnum.bitwise-ior (##u16vector-ref body first-block-index)
                                                      (##fixnum.bitwise-and 65535
                                                                       (##fixnum.arithmetic-shift-left 65535
                                                                                     (##fixnum.bitwise-and x 15)))))))
             (cont body))))))

(define (##check-char-set-range lower upper proc)
  (or (and (<= 0 lower) (<= lower 256))
      (error "Bad lower range" lower proc))
  (or (and (<= 0 lower upper) (<= upper 256))
      (error "Bad upper range" upper proc)))

(define (ucs-range->char-set lower upper #!optional (error? #t) bs)
  (##check-char-set-range lower upper ucs-range->char-set)
  (let ((body (if bs
                  (##char-set-body-copy (##char-set-body/check bs ucs-range->char-set))
                  (##char-set-empty-body))))
    (##range->char-set-body! lower upper body make-char-set)))

(define (ucs-range->char-set! lower upper error? base-cs)
  (##check-char-set-range lower upper ucs-range->char-set!)
  (let ((body (##char-set-body/check base-cs ucs-range->char-set!)))
    (##range->char-set-body! lower upper body (lambda (body) base-cs))))

(define char-set:lower-case
  (let* ((a-z (ucs-range->char-set #x61 #x7B))
         (latin1 (ucs-range->char-set! #xdf #xf7  #t a-z))
         (latin2 (ucs-range->char-set! #xf8 #x100 #t latin1)))
    (char-set-adjoin! latin2 (integer->char #xb5))))

(define char-set:upper-case
  (let ((A-Z (ucs-range->char-set #x41 #x5B)))
    ;; Add in the Latin-1 upper-case chars.
    (ucs-range->char-set! #xd8 #xdf #t
                          (ucs-range->char-set! #xc0 #xd7 #t A-Z))))

(define char-set:title-case char-set:empty)

(define char-set:letter
  (let ((u/l (char-set-union char-set:upper-case char-set:lower-case)))
    (char-set-adjoin! u/l
                      (integer->char #xaa)      ; FEMININE ORDINAL INDICATOR
                      (integer->char #xba))))   ; MASCULINE ORDINAL INDICATOR

(define char-set:digit     (string->char-set "0123456789"))
(define char-set:hex-digit (string->char-set "0123456789abcdefABCDEF"))

(define char-set:letter+digit
  (char-set-union char-set:letter char-set:digit))

(define char-set:punctuation
  (let ((ascii (string->char-set "!\"#%&'()*,-./:;?@[\\]_{}"))
        (latin-1-chars (map integer->char '(#xA1 ; INVERTED EXCLAMATION MARK
                                            #xAB ; LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
                                            #xAD ; SOFT HYPHEN
                                            #xB7 ; MIDDLE DOT
                                            #xBB ; RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
                                            #xBF)))) ; INVERTED QUESTION MARK
    (list->char-set! latin-1-chars ascii)))

(define char-set:symbol
  (let ((ascii (string->char-set "$+<=>^`|~"))
        (latin-1-chars (map integer->char '(#x00A2 ; CENT SIGN
                                            #x00A3 ; POUND SIGN
                                            #x00A4 ; CURRENCY SIGN
                                            #x00A5 ; YEN SIGN
                                            #x00A6 ; BROKEN BAR
                                            #x00A7 ; SECTION SIGN
                                            #x00A8 ; DIAERESIS
                                            #x00A9 ; COPYRIGHT SIGN
                                            #x00AC ; NOT SIGN
                                            #x00AE ; REGISTERED SIGN
                                            #x00AF ; MACRON
                                            #x00B0 ; DEGREE SIGN
                                            #x00B1 ; PLUS-MINUS SIGN
                                            #x00B4 ; ACUTE ACCENT
                                            #x00B6 ; PILCROW SIGN
                                            #x00B8 ; CEDILLA
                                            #x00D7 ; MULTIPLICATION SIGN
                                            #x00F7)))) ; DIVISION SIGN
    (list->char-set! latin-1-chars ascii)))


(define char-set:graphic
  (char-set-union char-set:letter+digit char-set:punctuation char-set:symbol))

(define char-set:whitespace
  (list->char-set (map integer->char '(#x09 ; HORIZONTAL TABULATION
                                       #x0A ; LINE FEED
                                       #x0B ; VERTICAL TABULATION
                                       #x0C ; FORM FEED
                                       #x0D ; CARRIAGE RETURN
                                       #x20 ; SPACE
                                       #xA0)))) ; NO-BREAK SPACE

(define char-set:printing (char-set-union char-set:whitespace char-set:graphic))

(define char-set:blank
  (list->char-set (map integer->char '(#x09 ; HORIZONTAL TABULATION
                                       #x20 ; SPACE
                                       #xA0)))) ; NO-BREAK SPACE


(define char-set:iso-control
  (ucs-range->char-set! #x7F #xA0 #t (ucs-range->char-set 0 32)))

(define char-set:ascii (ucs-range->char-set 0 128))

(declare (generic) (safe))


