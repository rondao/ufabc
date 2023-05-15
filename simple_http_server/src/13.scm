(declare (fixnum) (not safe))

;;; an implementation of the string library SRFI.


(##define-macro (##every? proc lst)
  `(let ((proc ,proc))
     (let loop ((lst ,lst))
       (or (null? lst)
	   (and (proc (car lst))
		(loop (cdr lst)))))))

(define (##check-arg pred val caller)
  (if (not (pred val)) (error "Bad argument type" val caller) val))

(define-macro (##check-str-spec s+start+end caller)
  (receive (s start end) (apply values s+start+end)
    (let ((slen (gensym)))
      `(if (string? ,s)
           (let ((,slen (string-length ,s)))
             (or (and (integer? ,start)
                      (<= 0 ,start)
                      (< ,start ,slen))
                 (error "Bad start index" ,start ,caller))
             (if ,end
                 (or (and (integer? ,end)
                          (<= ,start ,end ,slen))
                     (error "Bad end index" ,end ,caller))
                 (set! ,end ,slen)))
           (error "Expected string" ,s ,caller)))))



(define (string-null? s)
  (##check-arg string? s string-null?)
  (zero? (string-length s)))

(define (string-reverse s #!optional (start 0) end)
  (##check-str-spec (s start end) string-reverse)
  (let* ((len (- end start))
         (ans (make-string len)))
    (do ((i start (+ i 1))
         (j (- len 1) (- j 1)))
        ((< j 0) ans)
      (string-set! ans j (string-ref s i)))))

(define (string-reverse! s #!optional (start 0) end)
  (##check-str-spec (s start end) string-reverse!)
  (do ((i (- end 1) (- i 1))
       (j start (+ j 1)))
      ((<= i j))
    (let ((ci (string-ref s i)))
      (string-set! s i (string-ref s j))
      (string-set! s j ci))))

(define (reverse-list->string clist)
  (if (list? clist)
      (let* ((len (length clist))
	     (s (make-string len)))
	(do ((i     (- len 1) (- i 1))   
	     (clist clist     (cdr clist)))
	    ((not (pair? clist)) s)
	  (let ((char (car clist)))
            (##check-arg char? char reverse-list->string)
	    (string-set! s i char))))
      (error "reverse-list->string: expect LIST: " clist)))

(define (string->list s #!optional (start 0) end)
  (##check-str-spec (s start end) string->list)
  (do ((i (- end 1) (- i 1))
       (ans '() (cons (string-ref s i) ans)))
      ((< i start) ans)))

(define (string-copy s #!optional (start 0) end)
  (##check-str-spec (s start end) string-copy)
  (substring s start end))

(define (##substring/shared s start end)
  (if (and (zero? start) 
	   (= end (string-length s))) 
      s
      (substring s start end)))

(define (substring/shared s start #!optional end)
  (##check-str-spec (s start end) substring/shared)
  (##substring/shared s start end))

(define (string-map proc s #!optional (start 0) end)
  (##check-arg procedure? proc string-map)
  (##check-str-spec (s start end) string-map)
  (##string-map proc s start end))

(define (##string-map proc s start end)	; Internal utility
  (let* ((len (- end start))
	 (ans (make-string len)))
    (do ((i (- end 1) (- i 1))
	 (j (- len 1) (- j 1)))
	((< j 0) ans)
      (string-set! ans j (proc (string-ref s i))))))

(define (string-map! proc s #!optional (start 0) end)
  (##check-arg procedure? proc string-map!)
  (##check-str-spec (s start end) string-map!)
  (##string-map! proc s start end))

(define (##string-map! proc s start end)
  (do ((i (- end 1) (- i 1)))
      ((< i start))
    (string-set! s i (proc (string-ref s i)))))

(define (string-fold kons knil s #!optional (start 0) end)
  (##check-str-spec (s start end) string-fold)
  (##check-arg procedure? kons string-fold)
  (##string-fold kons knil s start end))

(define (##string-fold kons knil s start end)
  (do ((v knil (kons v (string-ref s i)))
       (i start (+ i 1)))
      ((>= i end) v)))

(define (string-fold-right kons knil s #!optional (start 0) end)
    (##check-arg procedure? kons string-fold-right)
    (##check-str-spec (s start end) string-fold-right)
    (##string-fold-right kons knil s start end))

(define (##string-fold-right kons knil s start end)
  (do ((v knil (kons (string-ref s i) v))
       (i (- end 1) (- i 1)))
      ((< i start) v)))

(define (string-unfold p f g seed #!optional (base "") (make-final (lambda (x) "")))
  (##check-arg procedure? p string-unfold)
  (##check-arg procedure? f string-unfold)
  (##check-arg procedure? g string-unfold)
  (##check-arg procedure? make-final string-unfold)
  (##check-arg string? base string-unfold)
  (let lp ((chunks '())                 ; Previously filled chunks
           (nchars 0)                   ; Number of chars in CHUNKS
           (chunk (make-string 40)); Current chunk into which we write
           (chunk-len 40)
           (i 0)                  ; Number of chars written into CHUNK
           (seed seed))
    (let lp2 ((i i) (seed seed))
      (if (not (p seed))
          (let ((c (f seed))
                (seed (g seed)))
            (if (< i chunk-len)
                (begin (string-set! chunk i c)
                       (lp2 (+ i 1) seed))
		      
                (let* ((nchars2 (+ chunk-len nchars))
                       (chunk-len2 (min 4096 nchars2))
                       (new-chunk (make-string chunk-len2)))
                  (string-set! new-chunk 0 c)
                  (lp (cons chunk chunks) (+ nchars chunk-len)
                      new-chunk chunk-len2 1 seed))))
		
          ;; We're done. Make the answer string & install the bits.
          (let* ((final (make-final seed))
                 (flen (string-length final))
                 (base-len (string-length base))
                 (j (+ base-len nchars i))
                 (ans (make-string (+ j flen))))
            (##string-copy! ans j final 0 flen)	; Install FINAL.
            (let ((j (- j i)))
              (##string-copy! ans j chunk 0 i) ; Install CHUNK[0,I).
              (let lp ((j j) (chunks chunks)) ; Install CHUNKS.
                (if (pair? chunks)
                    (let* ((chunk  (car chunks))
                           (chunks (cdr chunks))
                           (chunk-len (string-length chunk))
                           (j (- j chunk-len)))
                      (##string-copy! ans j chunk 0 chunk-len)
                      (lp j chunks)))))
            (##string-copy! ans 0 base 0 base-len) ; Install BASE.
            ans)))))

(define (string-unfold-right p f g seed #!optional (base "") (make-final (lambda (x) "")))
  (##check-arg procedure? p string-unfold-right)
  (##check-arg procedure? f string-unfold-right)
  (##check-arg procedure? g string-unfold-right)
  (##check-arg procedure? make-final string-unfold-right)
  (##check-arg string? base string-unfold-right)
  (let lp ((chunks '())                 ; Previously filled chunks
           (nchars 0)                   ; Number of chars in CHUNKS
           (chunk (make-string 40)); Current chunk into which we write
           (chunk-len 40)
           (i 2)                  ; Number of chars available in CHUNK
           (seed seed))
    (let lp2 ((i i) (seed seed))	; Fill up CHUNK from right
      (if (not (p seed))		; to left.
          (let ((c (f seed))
                (seed (g seed)))
            (if (> i 0)
                (let ((i (- i 1)))
                  (string-set! chunk i c)
                  (lp2 i seed))
		     
                (let* ((nchars2 (+ chunk-len nchars))
                       (chunk-len2 (min 4096 nchars2))
                       (new-chunk (make-string chunk-len2))
                       (i (- chunk-len2 1)))
                  (string-set! new-chunk i c)
                  (lp (cons chunk chunks) (+ nchars chunk-len)
                      new-chunk chunk-len2 i seed))))
	       
          ;; We're done. Make the answer string & install the bits.
          (let* ((final (make-final seed))
                 (flen (string-length final))
                 (base-len (string-length base))
                 (chunk-used (- chunk-len i))
                 (j (+ base-len nchars chunk-used))
                 (ans (make-string (+ j flen))))
            (##string-copy! ans 0 final 0 flen) ; Install FINAL.
            (##string-copy! ans flen chunk i chunk-len) ; Install CHUNK[I,).
            (let lp ((j (+ flen chunk-used)) ; Install CHUNKS.
                     (chunks chunks))		
              (if (pair? chunks)
                  (let* ((chunk  (car chunks))
                         (chunks (cdr chunks))
                         (chunk-len (string-length chunk)))
                    (##string-copy! ans j chunk 0 chunk-len)
                    (lp (+ j chunk-len) chunks))
                  (##string-copy! ans j base 0 base-len))) ; Install BASE.
            ans)))))

(define (string-for-each proc s #!optional (start 0) end)
  (##check-arg procedure? proc string-for-each)
  (##check-str-spec (s start end) string-for-each)
  (let lp ((i start))
    (if (< i end)
        (begin (proc (string-ref s i)) 
               (lp (+ i 1))))))

(define (string-for-each-index proc s #!optional (start 0) end)
  (##check-arg procedure? proc string-for-each-index)
  (##check-str-spec (s start end) string-for-each-index)
  (let lp ((i start))
    (if (< i end) 
        (begin (proc i) 
               (lp (+ i 1))))))

(define (string-every criterion s #!optional (start 0) end)
  (##check-str-spec (s start end) string-every)
  (cond ((char? criterion)
         (let lp ((i start))
           (or (>= i end)
               (and (char=? criterion (string-ref s i))
                    (lp (+ i 1))))))
	 
        ((char-set? criterion)
         (let lp ((i start))
           (or (>= i end)
               (and (char-set-contains? criterion (string-ref s i))
                    (lp (+ i 1))))))
	 
        ((procedure? criterion)		; Slightly funky loop so that
         (or (= start end)              ; final (PRED S[END-1]) call
             (let lp ((i start))        ; is a tail call.
               (let ((c (string-ref s i))
                     (i1 (+ i 1)))
                 (if (= i1 end) (criterion c) ; Tail call.
                     (and (criterion c) (lp i1)))))))
	 
        (else (error "Second param is neither char-set, char, or predicate procedure."
                     string-every criterion))))


(define (string-any criterion s #!optional (start 0) end)
  (##check-str-spec (s start end) string-any)
  (cond ((char? criterion)
         (let lp ((i start))
           (and (< i end)
                (or (char=? criterion (string-ref s i))
                    (lp (+ i 1))))))
	 
        ((char-set? criterion)
         (let lp ((i start))
           (and (< i end)
                (or (char-set-contains? criterion (string-ref s i))
                    (lp (+ i 1))))))
	 
        ((procedure? criterion)		; Slightly funky loop so that
         (and (< start end)             ; final (PRED S[END-1]) call
              (let lp ((i start))       ; is a tail call.
                (let ((c (string-ref s i))
                      (i1 (+ i 1)))
                  (if (= i1 end) (criterion c) ; Tail call
                      (or (criterion c) (lp i1)))))))
	 
        (else (error "Second param is neither char-set, char, or predicate procedure."
                     string-any criterion))))


(define (string-tabulate proc len)
  (##check-arg procedure? proc string-tabulate)
  (##check-arg integer? len string-tabulate)
  (let ((s (make-string len)))
    (do ((i (- len 1) (- i 1)))
        ((< i 0) s)
      (string-set! s i (proc i)))))

(define (string-fill! s char #!optional (start 0) end)
  (##check-arg char? char string-fill!)
  (##check-str-spec (s start end) string-fill!)
  (do ((i (- end 1) (- i 1)))
      ((< i start))
    (string-set! s i char)))

(define (string-copy! to tstart from #!optional (fstart 0) fend)
  (##check-str-spec (from fstart fend) string-copy!)
  (or (and (integer? tstart) (<= 0 tstart) (< tstart (string-length to)))
      (error "Bad starting index" tstart string-copy!))
  (let ((tend (+ tstart (- fend fstart))))
    (##check-str-spec (to tstart tend) string-copy!)
    (##string-copy! to tstart from fstart fend)))

;;; Library-internal routine
(define (##string-copy! to tstart from fstart fend)
  (if (> fstart tstart)
      (do ((i fstart (+ i 1))
	   (j tstart (+ j 1)))
	  ((>= i fend))
	(string-set! to j (string-ref from i)))

      (do ((i (- fend 1)                    (- i 1))
	   (j (+ -1 tstart (- fend fstart)) (- j 1)))
	  ((< i fstart))
	(string-set! to j (string-ref from i)))))

(define (##string-prefix-length s1 start1 end1 s2 start2 end2)
  (let* ((delta (min (- end1 start1) (- end2 start2)))
	 (end1 (+ start1 delta)))

    (if (and (eq? s1 s2) (= start1 start2))	; EQ fast path
	delta

	(let lp ((i start1) (j start2))		; Regular path
	  (if (or (>= i end1)
		  (not (char=? (string-ref s1 i)
			       (string-ref s2 j))))
	      (- i start1)
	      (lp (+ i 1) (+ j 1)))))))

(define (##string-suffix-length s1 start1 end1 s2 start2 end2)
  (let* ((delta (min (- end1 start1) (- end2 start2)))
	 (start1 (- end1 delta)))

    (if (and (eq? s1 s2) (= end1 end2))		; EQ fast path
	delta

	(let lp ((i (- end1 1)) (j (- end2 1)))	; Regular path
	  (if (or (< i start1)
		  (not (char=? (string-ref s1 i)
			       (string-ref s2 j))))
	      (- (- end1 i) 1)
	      (lp (- i 1) (- j 1)))))))

(define (##string-prefix-length-ci s1 start1 end1 s2 start2 end2)
  (let* ((delta (min (- end1 start1) (- end2 start2)))
	 (end1 (+ start1 delta)))

    (if (and (eq? s1 s2) (= start1 start2))	; EQ fast path
	delta

	(let lp ((i start1) (j start2))		; Regular path
	  (if (or (>= i end1)
		  (not (char-ci=? (string-ref s1 i)
				  (string-ref s2 j))))
	      (- i start1)
	      (lp (+ i 1) (+ j 1)))))))

(define (##string-suffix-length-ci s1 start1 end1 s2 start2 end2)
  (let* ((delta (min (- end1 start1) (- end2 start2)))
	 (start1 (- end1 delta)))

    (if (and (eq? s1 s2) (= end1 end2))		; EQ fast path
	delta

	(let lp ((i (- end1 1)) (j (- end2 1)))	; Regular path
	  (if (or (< i start1)
		  (not (char-ci=? (string-ref s1 i)
				  (string-ref s2 j))))
	      (- (- end1 i) 1)
	      (lp (- i 1) (- j 1)))))))


(define (string-prefix-length s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-prefix-length)
  (##check-str-spec (s2 start2 end2) string-prefix-length)
  (##string-prefix-length s1 start1 end1 s2 start2 end2))

(define (string-suffix-length s1 s2  #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-suffix-length)
  (##check-str-spec (s2 start2 end2) string-suffix-length)
  (##string-suffix-length s1 start1 end1 s2 start2 end2))

(define (string-prefix-length-ci s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-prefix-length-ci)
  (##check-str-spec (s2 start2 end2) string-prefix-length-ci)
  (##string-prefix-length-ci s1 start1 end1 s2 start2 end2))

(define (string-suffix-length-ci s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-suffix-length-ci)
  (##check-str-spec (s2 start2 end2) string-suffix-length-ci)
  (##string-suffix-length-ci s1 start1 end1 s2 start2 end2))

(define (string-prefix? s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-prefix?)
  (##check-str-spec (s2 start2 end2) string-prefix?)
  (##string-prefix? s1 start1 end1 s2 start2 end2))

(define (string-suffix? s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-suffix?)
  (##check-str-spec (s2 start2 end2) string-suffix?)
  (##string-suffix? s1 start1 end1 s2 start2 end2))

(define (string-prefix-ci? s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-prefix-ci?)
  (##check-str-spec (s2 start2 end2) string-prefix-ci?)
  (##string-prefix-ci? s1 start1 end1 s2 start2 end2))

(define (string-suffix-ci? s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-suffix-ci?)
  (##check-str-spec (s2 start2 end2) string-suffix-ci?)
  (##string-suffix-ci? s1 start1 end1 s2 start2 end2))

;;; Here are the internal routines that do the real work.

(define (##string-prefix? s1 start1 end1 s2 start2 end2)
  (let ((len1 (- end1 start1)))
    (and (<= len1 (- end2 start2))	; Quick check
	 (= (##string-prefix-length s1 start1 end1
				    s2 start2 end2)
	    len1))))

(define (##string-suffix? s1 start1 end1 s2 start2 end2)
  (let ((len1 (- end1 start1)))
    (and (<= len1 (- end2 start2))	; Quick check
	 (= len1 (##string-suffix-length s1 start1 end1
					 s2 start2 end2)))))

(define (##string-prefix-ci? s1 start1 end1 s2 start2 end2)
  (let ((len1 (- end1 start1)))
    (and (<= len1 (- end2 start2))	; Quick check
	 (= len1 (##string-prefix-length-ci s1 start1 end1
					    s2 start2 end2)))))

(define (##string-suffix-ci? s1 start1 end1 s2 start2 end2)
  (let ((len1 (- end1 start1)))
    (and (<= len1 (- end2 start2))	; Quick check
	 (= len1 (string-suffix-length-ci s1 start1 end1
					  s2 start2 end2)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Primitive string-comparison functions.
;;; Continuation order is different from MIT Scheme.
;;; Continuations are applied to s1's mismatch index;
;;; in the case of equality, this is END1.

(define (##string-compare s1 start1 end1 s2 start2 end2
			   proc< proc= proc>)
  (let ((size1 (- end1 start1))
	(size2 (- end2 start2)))
    (let ((match (##string-prefix-length s1 start1 end1 s2 start2 end2)))
      (if (= match size1)
	  (if (= match size2)
	      (proc= end1)
	      (proc< end1))
	  (let ((ans (+ match start1)))
	    (if (= match size2)
		(proc> ans)
		(if (char<? (string-ref s1 (+ start1 match))
			    (string-ref s2 (+ start2 match)))
		    (proc< ans)
		    (proc> ans))))))))

(define (##string-compare-ci s1 start1 end1 s2 start2 end2
			      proc< proc= proc>)
  (let ((size1 (- end1 start1))
	(size2 (- end2 start2)))
    (let ((match (##string-prefix-length-ci s1 start1 end1 s2 start2 end2)))
      (if (= match size1)
	  (if (= match size2) 
	      (proc= end1)
	      (proc< end1))
	  (let ((ans (+ start1 match)))
	    (if (= match size2)
		(proc> ans)
		(if (char-ci<? (string-ref s1 (+ start1 match))
			       (string-ref s2 (+ start2 match)))
		    (proc< ans)
		    (proc> ans))))))))


;XXXXX
(define (string-compare s1 s2 proc< proc= proc> #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-compare)
  (##check-str-spec (s2 start2 end2) string-compare)
  (##check-arg procedure? proc< string-compare)
  (##check-arg procedure? proc= string-compare)
  (##check-arg procedure? proc> string-compare)
  (##string-compare s1 start1 end1 s2 start2 end2 proc< proc= proc>))

(define (string-compare-ci s1 s2 proc< proc= proc> #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-compare-ci)
  (##check-str-spec (s2 start2 end2) string-compare-ci)
  (##check-arg procedure? proc< string-compare-ci)
  (##check-arg procedure? proc= string-compare-ci)
  (##check-arg procedure? proc> string-compare-ci)
  (##string-compare-ci s1 start1 end1 s2 start2 end2 proc< proc= proc>))


;;; string=          string<>		string-ci=          string-ci<>
;;; string<          string>		string-ci<          string-ci>
;;; string<=         string>=		string-ci<=         string-ci>=
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Simple definitions in terms of the previous comparison funs.
;;; I sure hope the ##STRING-COMPARE calls get integrated.
;;; In Gambit-C, they sure did; along with the calls to proc=, proc<, and proc>.

(define (string= s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string=)
  (##check-str-spec (s2 start2 end2) string=)
  (and (= (- end1 start1) (- end2 start2))                 ; Quick filter
       (or (and (eq? s1 s2) (= start1 start2))             ; Fast path
           (##string-compare s1 start1 end1 s2 start2 end2 ; Real test
                             (lambda (i) #f)
                             (lambda (i) i)
                             (lambda (i) #f)))))

(define (string<> s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string<>)
  (##check-str-spec (s2 start2 end2) string<>)
  (or (not (= (- end1 start1) (- end2 start2)))             ; Fast path
      (and (not (and (eq? s1 s2) (= start1 start2)))        ; Quick filter
           (##string-compare s1 start1 end1 s2 start2 end2  ; Real test
                             (lambda (i) i)
                             (lambda (i) #f)
                             (lambda (i) i)))))

(define (string< s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string<)
  (##check-str-spec (s2 start2 end2) string<)
  (if (and (eq? s1 s2) (= start1 start2))             ; Fast path
      (< end1 end2)
      (##string-compare s1 start1 end1 s2 start2 end2 ; Real test
                        (lambda (i) i)
                        (lambda (i) #f)
                        (lambda (i) #f))))

(define (string> s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string>)
  (##check-str-spec (s2 start2 end2) string>)
  (if (and (eq? s1 s2) (= start1 start2))             ; Fast path
      (> end1 end2)
      (##string-compare s1 start1 end1 s2 start2 end2 ; Real test
                        (lambda (i) #f)
                        (lambda (i) #f)
                        (lambda (i) i))))

(define (string<= s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string<=)
  (##check-str-spec (s2 start2 end2) string<=)
  (if (and (eq? s1 s2) (= start1 start2))             ; Fast path
      (<= end1 end2)
      (##string-compare s1 start1 end1 s2 start2 end2 ; Real test
                        (lambda (i) i)
                        (lambda (i) i)
                        (lambda (i) #f))))

(define (string>= s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string>=)
  (##check-str-spec (s2 start2 end2) string>=)
  (if (and (eq? s1 s2) (= start1 start2))             ; Fast path
      (>= end1 end2)
      (##string-compare s1 start1 end1 s2 start2 end2 ; Real test
                        (lambda (i) #f)
                        (lambda (i) i)
                        (lambda (i) i))))

(define (string-ci= s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-ci=)
  (##check-str-spec (s2 start2 end2) string-ci=)
  (and (= (- end1 start1) (- end2 start2))                    ; Quick filter
       (or (and (eq? s1 s2) (= start1 start2))                ; Fast path
           (##string-compare-ci s1 start1 end1 s2 start2 end2 ; Real test
                                (lambda (i) #f)
                                (lambda (i) i)
                                (lambda (i) #f)))))

(define (string-ci<> s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-ci<>)
  (##check-str-spec (s2 start2 end2) string-ci<>)
  (or (not (= (- end1 start1) (- end2 start2)))               ; Fast path
      (and (not (and (eq? s1 s2) (= start1 start2)))          ; Quick filter
           (##string-compare-ci s1 start1 end1 s2 start2 end2 ; Real test
                                (lambda (i) i)
                                (lambda (i) #f)
                                (lambda (i) i)))))

(define (string-ci< s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-ci<)
  (##check-str-spec (s2 start2 end2) string-ci<)
  (if (and (eq? s1 s2) (= start1 start2))                ; Fast path
      (< end1 end2)
      (##string-compare-ci s1 start1 end1 s2 start2 end2 ; Real test
                           (lambda (i) i)
                           (lambda (i) #f)
                           (lambda (i) #f))))

(define (string-ci> s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-ci>)
  (##check-str-spec (s2 start2 end2) string-ci>)
  (if (and (eq? s1 s2) (= start1 start2))                ; Fast path
      (> end1 end2)
      (##string-compare-ci s1 start1 end1 s2 start2 end2 ; Real test
                           (lambda (i) #f)
                           (lambda (i) #f)
                           (lambda (i) i))))

(define (string-ci<= s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-ci<=)
  (##check-str-spec (s2 start2 end2) string-ci<=)
  (if (and (eq? s1 s2) (= start1 start2))                ; Fast path
      (<= end1 end2)
      (##string-compare-ci s1 start1 end1 s2 start2 end2 ; Real test
                           (lambda (i) i)
                           (lambda (i) i)
                           (lambda (i) #f))))

(define (string-ci>= s1 s2 #!optional (start1 0) end1 (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-ci>=)
  (##check-str-spec (s2 start2 end2) string-ci>=)
  (if (and (eq? s1 s2) (= start1 start2))                ; Fast path
      (>= end1 end2)
      (##string-compare-ci s1 start1 end1 s2 start2 end2 ; Real test
                           (lambda (i) #f)
                           (lambda (i) i)
                           (lambda (i) i))))

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

(##define-macro (##string-hash s char->int bound start end)
  `(let ((s ,s) (char->int ,char->int) (bound ,bound) (start ,start) (end ,end))
     (do ((i start (+ i 1))
	  (ans 0 (remainder (+ (char->int (string-ref s i)) 
			       (* 37 ans))
			    bound)))
	 ((= i end) ans))))

(define (string-hash s #!optional (bound 4194304) (start 0) end)
  (##check-str-spec (s start end) string-hash)
  (or (and (integer? bound) (<= 0 bound 4194304))
      (error "Bad bound" bound string-hash))
  (if (zero? bound) (set! bound 4194304))
  (##string-hash s char->integer bound start end))

(define (string-hash-ci s #!optional (bound 4194304) (start 0) end)
  (##check-str-spec (s start end) string-hash-ci)
  (or (and (integer? bound) (<= 0 bound 4194304))
      (error "Bad bound" bound string-hash-ci))
  (if (zero? bound) (set! bound 4194304))
  (##string-hash s (lambda (c) (char->integer (char-downcase c))) bound start end))

;;; Case hacking
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; string-upcase  s [start end]
;;; string-upcase! s [start end]
;;; string-downcase  s [start end]
;;; string-downcase! s [start end]
;;;
;;; string-titlecase  s [start end]
;;; string-titlecase! s [start end]
;;;   Capitalize every contiguous alpha sequence: capitalise
;;;   first char, lowercase rest.

(define (##string-upcase s start end)
  (##string-map char-upcase s start end))

(define (##string-upcase! s start end)
  (##string-map! char-upcase s start end))

(define (##string-downcase s start end)
  (##string-map char-downcase s start end))

(define (##string-downcase! s start end)
  (##string-map! char-downcase s start end))

(define (string-upcase  s #!optional (start 0) end)
  (##check-str-spec (s start end) string-upcase)
  (##string-map char-upcase s start end))

(define (string-upcase! s #!optional (start 0) end)
  (##check-str-spec (s start end) string-upcase!)
  (##string-map! char-upcase s start end))

(define (string-downcase  s #!optional (start 0) end)
  (##check-str-spec (s start end) string-downcase)
  (##string-map char-downcase s start end))

(define (string-downcase! s #!optional (start 0) end)
  (##check-str-spec (s start end) string-downcase!)
  (##string-map! char-downcase s start end))

(define (char-cased? ch)
  (char-set-contains? char-set:letter ch))

(define char-titlecase char-upcase)

(define (##string-titlecase! s start end)
  (let lp ((i start))
    (cond ((##string-index s char-cased? i end) =>
           (lambda (i)
	     (string-set! s i (char-titlecase (string-ref s i)))
	     (let ((i1 (+ i 1)))
	       (cond ((##string-skip s char-cased? i1 end) =>
		      (lambda (j)
			(##string-downcase! s i1 j)
			(lp (+ j 1))))
		     (else (##string-downcase! s i1 end)))))))))

(define (string-titlecase s #!optional (start 0) end)
  (##check-str-spec (s start end) string-titlecase)
  (let ((ans (substring s start end)))
    (##string-titlecase! ans 0 (- end start))
    ans))

(define (string-titlecase! s #!optional (start 0) end)
  (##check-str-spec (s start end) string-titlecase!)
  (##string-titlecase! s start end))

;;; String search
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; string-index       string char/char-set/pred [start end]
;;; string-index-right string char/char-set/pred [start end]
;;; string-skip        string char/char-set/pred [start end]
;;; string-skip-right  string char/char-set/pred [start end]
;;; string-count       char/char-set/pred string [start end]
;;;     There's a lot of replicated code here for efficiency.
;;;     For example, the char/char-set/pred discrimination has
;;;     been lifted above the inner loop of each proc.


(define (string-index s criterion #!optional (start 0) end)
  (##check-str-spec (s start end) string-index)
  (##string-index s criterion start end))

(define (##string-index s criterion start end)
  (cond ((char? criterion)
	 (let lp ((i start))
	   (and (< i end)
		(if (char=? criterion (string-ref s i)) i
		    (lp (+ i 1))))))
	((char-set? criterion)
	 (let lp ((i start))
	   (and (< i end)
		(if (char-set-contains? criterion (string-ref s i)) i
		    (lp (+ i 1))))))
	((procedure? criterion)
	 (let lp ((i start))
	   (and (< i end)
		(if (criterion (string-ref s i)) i
		    (lp (+ i 1))))))
	(else (error "Second param is neither char-set, char, or predicate procedure."
		     string-index criterion))))

(define (string-index-right s criterion #!optional (start 0) end)
  (##check-str-spec (s start end) string-index-right)
  (##string-index-right s criterion start end))

(define (##string-index-right s criterion start end)
  (cond ((char? criterion)
	 (let lp ((i (- end 1)))
	   (and (>= i 0)
		(if (char=? criterion (string-ref s i)) i
		    (lp (- i 1))))))
	((char-set? criterion)
	 (let lp ((i (- end 1)))
	   (and (>= i 0)
		(if (char-set-contains? criterion (string-ref s i)) i
		    (lp (- i 1))))))
	((procedure? criterion)
	 (let lp ((i (- end 1)))
	   (and (>= i 0)
		(if (criterion (string-ref s i)) i
		    (lp (- i 1))))))
	(else (error "Second param is neither char-set, char, or predicate procedure."
		     string-index-right criterion))))

(define (string-skip s criterion #!optional (start 0) end)
  (##check-str-spec (s start end) string-skip)
  (##string-skip s criterion start end))

(define (##string-skip s criterion start end)
  (cond ((char? criterion)
	 (let lp ((i start))
	   (and (< i end)
		(if (char=? criterion (string-ref s i))
		    (lp (+ i 1))
		    i))))
	((char-set? criterion)
	 (let lp ((i start))
	   (and (< i end)
		(if (char-set-contains? criterion (string-ref s i))
		    (lp (+ i 1))
		    i))))
	((procedure? criterion)
	 (let lp ((i start))
	   (and (< i end)
		(if (criterion (string-ref s i)) (lp (+ i 1))
		    i))))
	(else (error "Second param is neither char-set, char, or predicate procedure."
		     string-skip criterion))))

(define (string-skip-right s criterion #!optional (start 0) end)
  (##check-str-spec (s start end) string-skip-right)
  (##string-skip-right s criterion start end))

(define (##string-skip-right s criterion start end)
  (cond ((char? criterion)
	 (let lp ((i (- end 1)))
	   (and (>= i 0)
		(if (char=? criterion (string-ref s i))
		    (lp (- i 1))
		    i))))
	((char-set? criterion)
	 (let lp ((i (- end 1)))
	   (and (>= i 0)
		(if (char-set-contains? criterion (string-ref s i))
		    (lp (- i 1))
		    i))))
	((procedure? criterion)
	 (let lp ((i (- end 1)))
	   (and (>= i 0)
		(if (criterion (string-ref s i)) (lp (- i 1))
		    i))))
	(else (error "CRITERION param is neither char-set or char."
		     string-skip-right criterion))))


(define (string-count s criterion #!optional (start 0) end)
  (##check-str-spec (s start end) string-count)
  (##string-count s criterion start end))

(define (##string-count s criterion start end)
  (cond ((char? criterion)
	 (do ((i start (+ i 1))
	      (count 0 (if (char=? criterion (string-ref s i))
			   (+ count 1)
			   count)))
	     ((>= i end) count)))
	
	((char-set? criterion)
	 (do ((i start (+ i 1))
	      (count 0 (if (char-set-contains? criterion (string-ref s i))
			   (+ count 1)
			   count)))
	     ((>= i end) count)))
	
	((procedure? criterion)
	 (do ((i start (+ i 1))
	      (count 0 (if (criterion (string-ref s i)) (+ count 1) count)))
	     ((>= i end) count)))
	
	(else (error "CRITERION param is neither char-set or char."
		     string-count criterion))))


;;; Cutting & pasting strings
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; string-take string nchars
;;; string-drop string nchars
;;;
;;; string-take-right string nchars
;;; string-drop-right string nchars
;;;
;;; string-pad string k [char start end] 
;;; string-pad-right string k [char start end] 
;;; 
;;; string-trim       string [char/char-set/pred start end] 
;;; string-trim-right string [char/char-set/pred start end] 
;;; string-trim-both  string [char/char-set/pred start end] 
;;;
;;; These trimmers invert the char-set meaning from MIT Scheme -- you
;;; say what you want to trim.


(define (string-take s n)
  (##check-str-spec (s 0 n) string-take)
  (##substring/shared s 0 n))

(define (string-take-right s n)
  (##check-str-spec (s 0 n) string-take-right)
  (let ((len (string-length s)))
    (##substring/shared s (- len n) len)))

(define (string-drop s n)
  (##check-str-spec (s 0 n) string-drop)
  (let ((len (string-length s)))
    (##substring/shared s n len)))

(define (string-drop-right s n)
  (##check-str-spec (s 0 n) string-drop-right)
  (let ((len (string-length s)))
    (##substring/shared s 0 (- len n))))

(define (string-trim s #!optional (criterion char-set:whitespace) (start 0) end)
  (##check-str-spec (s start end) string-trim)
  (cond ((string-skip s criterion start end) =>
         (lambda (i) (##substring/shared s i end)))
        (else "")))

(define (string-trim-right s #!optional (criterion char-set:whitespace) (start 0) end)
  (##check-str-spec (s start end) string-trim-right)
  (cond ((string-skip-right s criterion start end) =>
         (lambda (i) (##substring/shared s 0 (+ 1 i))))
        (else "")))

(define (string-trim-both s #!optional (criterion char-set:whitespace) (start 0) end)
  (##check-str-spec (s start end) string-trim-both)
  (cond ((string-skip s criterion start end) =>
         (lambda (i)
           (##substring/shared s i (+ 1 (string-skip-right s criterion i end)))))
        (else "")))


(define (string-pad s n #!optional (char #\space) (start 0) end)
  (##check-str-spec (s start end) string-pad)
  (##check-arg char? char string-pad)
  (or (and (integer? n) (<= 0 n))
      (error "Invalid length" n string-pad))
  (let ((len (- end start)))
    (if (<= n len)
        (##substring/shared s (- end n) end)
        (let ((ans (make-string n char)))
          (##string-copy! ans (- n len) s start end)
          ans))))

(define (string-pad-right s n #!optional (char #\space) (start 0) end)
  (##check-str-spec (s start end) string-pad-right)
  (##check-arg char? char string-pad-right)
  (or (and (integer? n) (<= 0 n))
      (error "Invalid length" n string-pad-right))
  (let ((len (- end start)))
    (if (<= n len)
        (##substring/shared s start (+ start n))
        (let ((ans (make-string n char)))
          (##string-copy! ans 0 s start end)
          ans))))

;;; Filtering strings
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; string-delete char/char-set/pred string [start end]
;;; string-filter char/char-set/pred string [start end]
;;;
;;; If the criterion is a char or char-set, we scan the string twice with
;;;   string-fold -- once to determine the length of the result string, 
;;;   and once to do the filtered copy.
;;; If the criterion is a predicate, we don't do this double-scan strategy, 
;;;   because the predicate might have side-effects or be very expensive to
;;;   compute. So we preallocate a temp buffer pessimistically, and only do
;;;   one scan over S. This is likely to be faster and more space-efficient
;;;   than consing a list.

(define (string-delete criterion s #!optional (start 0) end)
  (##check-str-spec (s start end) string-delete)
  (if (procedure? criterion)
      (let* ((slen (- end start))
             (temp (make-string slen))
             (ans-len (##string-fold (lambda (i c)
                                       (if (criterion c) i
                                           (begin (string-set! temp i c)
                                                  (+ i 1))))
                                     0 s start end)))
        (if (= ans-len slen) temp (substring temp 0 ans-len)))
       
      (let* ((cset (cond ((char-set? criterion) criterion)
                         ((char? criterion) (char-set criterion))
                         (else (error "string-delete criterion not predicate, char or char-set" criterion))))
             (len (##string-fold (lambda (i c) (if (char-set-contains? cset c)
                                                   i
                                                   (+ i 1)))
                                 0 s start end))
             (ans (make-string len)))
        (##string-fold (lambda (i c) (if (char-set-contains? cset c)
                                         i
                                         (begin (string-set! ans i c)
                                                (+ i 1))))
                       0 s start end)
        ans)))

(define (string-filter criterion s #!optional (start 0) end)
  (##check-str-spec (s start end) string-filter)
  (if (procedure? criterion)
      (let* ((slen (- end start))
             (temp (make-string slen))
             (ans-len (##string-fold (lambda (i c)
                                       (if (criterion c)
                                           (begin (string-set! temp i c)
                                                  (+ i 1))
                                           i))
                                     0 s start end)))
        (if (= ans-len slen) temp (substring temp 0 ans-len)))
       
      (let* ((cset (cond ((char-set? criterion) criterion)
                         ((char? criterion) (char-set criterion))
                         (else (error "string-delete criterion not predicate, char or char-set" criterion))))
	      
             (len (##string-fold (lambda (i c) (if (char-set-contains? cset c)
                                                   (+ i 1)
                                                   i))
                                 0 s start end))
             (ans (make-string len)))
        (##string-fold (lambda (i c) (if (char-set-contains? cset c)
                                         (begin (string-set! ans i c)
                                                (+ i 1))
                                         i))
                       0 s start end)
        ans)))

;;; Searching for an occurrence of a substring
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(define (string-contains text pattern #!optional (t-start 0) t-end (p-start 0) p-end)
  (##check-str-spec (text t-start t-end) string-contains)
  (##check-str-spec (pattern p-start p-end) string-contains)
  (##kmp-search pattern text char=? p-start p-end t-start t-end))

(define (string-contains-ci text pattern #!optional (t-start 0) t-end (p-start 0) p-end)
  (##check-str-spec (text t-start t-end) string-contains-ci)
  (##check-str-spec (pattern p-start p-end) string-contains-ci)
  (##kmp-search pattern text char-ci=? p-start p-end t-start t-end))


;;; Knuth-Morris-Pratt string searching
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; See
;;;     "Fast pattern matching in strings"
;;;     SIAM J. Computing 6(2):323-350 1977
;;;     D. E. Knuth, J. H. Morris and V. R. Pratt
;;; also described in
;;;     "Pattern matching in strings"
;;;     Alfred V. Aho
;;;     Formal Language Theory - Perspectives and Open Problems
;;;     Ronald V. Brook (editor)
;;; This algorithm is O(m + n) where m and n are the 
;;; lengths of the pattern and string respectively

;;; KMP search source[start,end) for PATTERN. Return starting index of
;;; leftmost match or #f.

(define (##kmp-search pattern text c= p-start p-end t-start t-end)
  (let ((plen (- p-end p-start))
	(rv (##make-kmp-restart-vector pattern c= p-start p-end)))

    ;; The search loop. TJ & PJ are redundant state.
    (let lp ((ti t-start) (pi 0)
	     (tj (- t-end t-start))	; (- tlen ti) -- how many chars left.
	     (pj plen))			; (- plen pi) -- how many chars left.

      (if (= pi plen) (- ti plen)			; Win.
	  
	  (and (<= pj tj)				; Lose.
		 
	       (if (c= (string-ref text ti)		; Search.
		       (string-ref pattern (+ p-start pi)))
		   (lp (+ 1 ti) (+ 1 pi) (- tj 1) (- pj 1))	; Advance.
		   
		   (let ((pi (vector-ref rv pi)))		; Retreat.
		     (if (= pi -1)
			 (lp (+ ti 1)  0   (- tj 1)  plen)	; Punt.
			 (lp ti        pi  tj        (- plen pi))))))))))

;;; (make-kmp-restart-vector pattern [c= start end]) -> integer-vector
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Compute the KMP restart vector RV for string PATTERN.  If
;;; we have matched chars 0..i-1 of PATTERN against a search string S, and
;;; PATTERN[i] doesn't match S[k], then reset i := RV[i], and try again to
;;; match S[k].  If RV[i] = -1, then punt S[k] completely, and move on to
;;; S[k+1] and PATTERN[0] -- no possible match of PAT[0..i] contains S[k].
;;;
;;; In other words, if you have matched the first i chars of PATTERN, but
;;; the i+1'th char doesn't match, RV[i] tells you what the next-longest
;;; prefix of PATTERN is that you have matched.
;;;
;;; - C= (default CHAR=?) is used to compare characters for equality.
;;;   Pass in CHAR-CI=? for case-folded string search.
;;;
;;; - START & END restrict the pattern to the indicated substring; the
;;;   returned vector will be of length END - START. The numbers stored
;;;   in the vector will be values in the range [0,END-START) -- that is,
;;;   they are valid indices into the restart vector; you have to add START
;;;   to them to use them as indices into PATTERN.
;;;
;;; I've split this out as a separate function in case other constant-string
;;; searchers might want to use it.
;;;
;;; E.g.:
;;;    a b d  a b x
;;; #(-1 0 0 -1 1 2)

(define (make-kmp-restart-vector pattern #!optional (c= char=?) (start 0) end)
  (##check-str-spec (pattern start end) make-kmp-restart-vector)
  (##check-arg procedure? c= make-kmp-restart-vector)
  (##make-kmp-restart-vector pattern c= start end))
   
(define (##make-kmp-restart-vector pattern c= start end)
  (let* ((rvlen (- end start))
	 (rv (make-vector rvlen -1)))
    (if (> rvlen 0)
	(let ((rvlen-1 (- rvlen 1))
	      (c0 (string-ref pattern start)))
	  
	  ;; Here's the main loop. We have set rv[0] ... rv[i].
	  ;; K = I + START -- it is the corresponding index into PATTERN.
	  (let lp1 ((i 0) (j -1) (k start))	
	    (if (< i rvlen-1)
		
		(let ((ck (string-ref pattern k)))
		  ;; lp2 invariant:
		  ;;   pat[(k-j) .. k-1] matches pat[start .. start+j-1]
		  ;;   or j = -1.
		  (let lp2 ((j j))
		    
		    (cond ((= j -1)
			   (let ((i1 (+ i 1)))
			     (vector-set! rv i1 (if (c= ck c0) -1 0))
			     (lp1 i1 0 (+ k 1))))
			  
			  ;; pat[(k-j) .. k] matches pat[start..start+j].
			  ((c= ck (string-ref pattern (+ j start)))
			   (let* ((i1 (+ 1 i))
				  (j1 (+ 1 j)))
			     (vector-set! rv i1 j1)
			     (lp1 i1 j1 (+ k 1))))
			  
			  (else (lp2 (vector-ref rv j))))))))))
    rv))

;;; We've matched I chars from PAT. C is the next char from the search string.
;;; Return the new I after handling C. 
;;;
;;; The pattern is (VECTOR-LENGTH RV) chars long, beginning at index PAT-START
;;; in PAT (PAT-START is usually 0). The I chars of the pattern we've matched
;;; are 
;;;     PAT[PAT-START .. PAT-START + I].
;;;
;;; It's *not* an oversight that there is no friendly error checking or
;;; defaulting of arguments. This is a low-level, inner-loop procedure
;;; that we want integrated/inlined into the point of call.

(define (##kmp-step pat rv c i c= p-start)
  (let lp ((i i))
    (if (c= c (string-ref pat (+ i p-start)))	; Match =>
	(+ i 1)					;   Done.
	(let ((i (vector-ref rv i)))		; Back up in PAT.
	  (if (= i -1) 0			; Can't back up further.
	      (lp i))))))			; Keep trying for match.

;;; Zip through S[start,end), looking for a match of PAT. Assume we've
;;; already matched the first I chars of PAT when we commence at S[start].
;;; - <0:  If we find a match *ending* at index J, return -J.
;;; - >=0: If we get to the end of the S[start,end) span without finding
;;;   a complete match, return the number of chars from PAT we'd matched
;;;   when we ran off the end.
;;;
;;; This is useful for searching *across* buffers -- that is, when your
;;; input comes in chunks of text. We hand-integrate the KMP-STEP loop
;;; for speed.

(define (string-kmp-partial-search pat rv s i #!optional (c= char=?) (p-start 0) (s-start 0) s-end)
  (##check-str-spec (s s-start s-end) string-kmp-partial-search)
  (##check-arg vector? rv string-kmp-partial-search)
  (##check-arg string? pat string-kmp-partial-search)
  (or (and (integer? p-start) (<= 0 p-start) (< p-start (string-length pat)))
      (error "Bad pattern start" p-start string-kmp-partial-search))
  (or (and (integer? i) (<= 0 i) (< i (string-length pat)))
      (error "Bad pattern index" p-start string-kmp-partial-search))
  ;; Enough prelude. Here's the actual code.
  (let ((patlen (vector-length rv)))
    (let lp ((si s-start)		; An index into S.
             (vi i))			; An index into RV.
      (cond ((= vi patlen) (- si))	; Win.
            ((= si s-end) vi)		; Ran off the end.
            (else			; Match s[si] & loop.
             (let ((c (string-ref s si)))
               (lp (+ si 1)	
                   (let lp2 ((vi vi))	; This is just KMP-STEP.
                     (if (c= c (string-ref pat (+ vi p-start)))
                         (+ vi 1)
                         (let ((vi (vector-ref rv vi)))
                           (if (= vi -1) 0
                               (lp2 vi))))))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; STRING-APPEND/SHARED has license to return a string that shares storage
;;; with any of its arguments. In particular, if there is only one non-empty
;;; string amongst its parameters, it is permitted to return that string as
;;; its result. STRING-APPEND, by contrast, always allocates new storage.
;;;
;;; STRING-CONCATENATE & STRING-CONCATENATE/SHARED are passed a list of
;;; strings, which they concatenate into a result string. STRING-CONCATENATE
;;; always allocates a fresh string; STRING-CONCATENATE/SHARED may (or may
;;; not) return a result that shares storage with any of its arguments. In
;;; particular, if it is applied to a singleton list, it is permitted to
;;; return the car of that list as its value.

(define (string-append/shared . strings) 
  (string-concatenate/shared strings))

(define (string-concatenate/shared strings)
  (if (list? strings)
      (if (##every? string? strings)
	  (let lp ((strings strings) (nchars 0) (first #f))
	    (cond ((pair? strings)			; Scan the args, add up total
		   (let* ((string  (car strings))	; length, remember 1st 
			  (tail (cdr strings))		; non-empty string.
			  (slen (string-length string)))
		     (if (zero? slen)
			 (lp tail nchars first)
			 (lp tail (+ nchars slen) (or first strings)))))
		  
		  ((zero? nchars) "")
		  
		  ;; Just one non-empty string! Return it.
		  ((= nchars (string-length (car first))) (car first))
		  
		  (else (let ((ans (make-string nchars)))
			  (let lp ((strings first) (i 0))
			    (if (pair? strings)
				(let* ((s (car strings))
				       (slen (string-length s)))
				  (##string-copy! ans i s 0 slen)
				  (lp (cdr strings) (+ i slen)))))
			  ans))))
	  (error "Not every element of strings is a string: (string-concatenate/shared " strings ")"))
      (error "strings is not a list: (string-concatenate/shared " strings ")")))
			

; Alas, Scheme 48's APPLY blows up if you have many, many arguments.
;(define (string-concatenate strings) (apply string-append strings))

;;; Here it is written out. I avoid using REDUCE to add up string lengths
;;; to avoid non-R5RS dependencies.

(define (##string-concatenate strings)
  (let* ((total (do ((strings strings (cdr strings))
		     (i 0 (+ i (string-length (car strings)))))
		    ((not (pair? strings)) i)))
	 (ans (make-string total)))
    (let lp ((i 0) (strings strings))
      (if (pair? strings)
	  (let* ((s (car strings))
		 (slen (string-length s)))
	    (##string-copy! ans i s 0 slen)
	    (lp (+ i slen) (cdr strings)))))
    ans))

(define (string-concatenate strings)
  (if (list? strings)
      (if (##every? string? strings)
	  (##string-concatenate strings)
	  (error "Not every element of strings is a string: (string-concatenate " strings ")"))
      (error "strings is not a list: (string-concatenate/shared " strings ")")))

;;; string-replace s1 s2 start1 end1 [start2 end2] -> string
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Replace S1[START1,END1) with S2[START2,END2).

(define (string-replace s1 s2 start1 end1 #!optional (start2 0) end2)
  (##check-str-spec (s1 start1 end1) string-replace)
  (##check-str-spec (s2 start2 end2) string-replace)
  (let* ((slen1 (string-length s1))
         (sublen2 (- end2 start2))
         (alen (+ (- slen1 (- end1 start1)) sublen2))
         (ans (make-string alen)))
    (##string-copy! ans 0 s1 0 start1)
    (##string-copy! ans start1 s2 start2 end2)
    (##string-copy! ans (+ start1 sublen2) s1 end1 slen1)
    ans))

;;; string-tokenize s [token-set start end] -> list
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Break S up into a list of token strings, where a token is a maximal
;;; non-empty contiguous sequence of chars belonging to TOKEN-SET.
;;; (string-tokenize "hello, world") => ("hello," "world")

(define (string-tokenize s #!optional (token-chars char-set:graphic) (start 0) end)
  (##check-str-spec (s start end) string-tokenize)
  (let lp ((i end) (ans '()))
    (cond ((and (< start i) (##string-index-right s token-chars start i)) =>
           (lambda (tend-1)
             (let ((tend (+ 1 tend-1)))
               (cond ((##string-skip-right s token-chars start tend-1) =>
                      (lambda (tstart-1)
                        (lp tstart-1
                            (cons (substring s (+ 1 tstart-1) tend)
                                  ans))))
                     (else (cons (substring s start tend) ans))))))
          (else ans))))

;;; xsubstring s from [to start end] -> string
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; S is a string; START and END are optional arguments that demarcate
;;; a substring of S, defaulting to 0 and the length of S (e.g., the whole
;;; string). Replicate this substring up and down index space, in both the
;;  positive and negative directions. For example, if S = "abcdefg", START=3, 
;;; and END=6, then we have the conceptual bidirectionally-infinite string
;;;     ...  d  e  f  d  e  f  d  e  f  d  e  f  d  e  f  d  e  f  d  e  f ...
;;;     ... -9 -8 -7 -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6  7  8  9 ...
;;; XSUBSTRING returns the substring of this string beginning at index FROM,
;;; and ending at TO (which defaults to FROM+(END-START)).
;;; 
;;; You can use XSUBSTRING in many ways:
;;; - To rotate a string left:  (xsubstring "abcdef" 2)  => "cdefab"
;;; - To rotate a string right: (xsubstring "abcdef" -2) => "efabcd"
;;; - To replicate a string:    (xsubstring "abc" 0 7) => "abcabca"
;;;
;;; Note that 
;;;   - The FROM/TO indices give a half-open range -- the characters from
;;;     index FROM up to, but not including index TO.
;;;   - The FROM/TO indices are not in terms of the index space for string S.
;;;     They are in terms of the replicated index space of the substring
;;;     defined by S, START, and END.
;;;
;;; It is an error if START=END -- although this is allowed by special
;;; dispensation when FROM=TO.

(define (xsubstring s from #!optional to (start 0) end)
  (##check-str-spec (s start end) xsubstring)
  (##check-arg integer? from xsubstring)
  (if to
      (##check-arg integer? to xsubstring)
      (set! to (+ from (- end start))))
  (let ((slen   (- end start))
        (anslen (- to  from)))
    (cond ((zero? anslen) "")
          ((zero? slen) (error "Cannot replicate empty (sub)string"
                               xsubstring s from to start end))
	       
          ((= 1 slen)              ; Fast path for 1-char replication.
           (make-string anslen (string-ref s start)))
	       
          ;; Selected text falls entirely within one span.
          ((= (floor (/ from slen)) (floor (/ to slen)))
           (substring s (+ start (modulo from slen)) (+ start (modulo to   slen))))
	       
          ;; Selected text requires multiple spans.
          (else (let ((ans (make-string anslen)))
                  (##multispan-repcopy! ans 0 s from to start end)
                  ans)))))


;;; string-xcopy! target tstart s sfrom [sto start end] -> unspecific
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Exactly the same as xsubstring, but the extracted text is written
;;; into the string TARGET starting at index TSTART.
;;; This operation is not defined if (EQ? TARGET S) -- you cannot copy
;;; a string on top of itself.

(define (string-xcopy! target tstart s sfrom #!optional sto (start 0) end)
  (##check-str-spec (s start end) string-xcopy!)
  (##check-arg integer? sfrom)
  (if sto
      (##check-arg integer? sto string-xcopy!)
      (set! sto (+ sfrom (- end start))))
  (let ((tocopy (- sto sfrom)))
    (let ((tend (+ tstart tocopy))
          (slen (- end start)))
      (##check-str-spec (target tstart tend) string-xcopy!)
      (cond ((zero? tocopy))
            ((zero? slen) (error "Cannot replicate empty (sub)string"
                                 string-xcopy!
                                 target tstart s sfrom sto start end))
		  
            ((= 1 slen)            ; Fast path for 1-char replication.
             (string-fill! target (string-ref s start) tstart tend))
		  
            ;; Selected text falls entirely within one span.
            ((= (floor (/ sfrom slen)) (floor (/ sto slen)))
             (##string-copy! target tstart s 
                             (+ start (modulo sfrom slen))
                             (+ start (modulo sto   slen))))
		  
            ;; Multi-span copy.
            (else (##multispan-repcopy! target tstart s sfrom sto start end))))))


;;; This is the core copying loop for XSUBSTRING and STRING-XCOPY!
;;; Internal -- not exported, no careful arg checking.
(define (##multispan-repcopy! target tstart s sfrom sto start end)
  (let* ((slen (- end start))
	 (i0 (+ start (modulo sfrom slen)))
	 (total-chars (- sto sfrom)))
    
    ;; Copy the partial span @ the beginning
    (##string-copy! target tstart s i0 end)
    
    (let* ((ncopied (- end i0))			; We've copied this many.
	   (nleft (- total-chars ncopied))	; # chars left to copy.
	   (nspans (quotient nleft slen)))	; # whole spans to copy
      
      ;; Copy the whole spans in the middle.
      (do ((i (+ tstart ncopied) (+ i slen))	; Current target index.
	   (nspans nspans (- nspans 1)))	; # spans to copy
	  ((zero? nspans)
	   ;; Copy the partial-span @ the end & we're done.
	   (##string-copy! target i s start (+ start (- total-chars (- i tstart)))))

	(##string-copy! target i s start end))))); Copy a whole span.

;;; (string-join string-list [delimiter grammar]) => string
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Paste strings together using the delimiter string.
;;;
;;; (string-join '("foo" "bar" "baz") ":") => "foo:bar:baz"
;;;
;;; DELIMITER defaults to a single space " "
;;; GRAMMAR is one of the symbols {prefix, infix, strict-infix, suffix} 
;;; and defaults to 'infix.
;;;
;;; I could rewrite this more efficiently -- precompute the length of the
;;; answer string, then allocate & fill it in iteratively. Using 
;;; STRING-CONCATENATE is less efficient.

(define (string-join strings #!optional (delim " ") (grammar 'infix))
  (if (list? strings)
      (if (##every? string? strings)
          (begin
            (##check-arg string? delim string-join)
            (let ((buildit (lambda (lis final)
                             (let recur ((lis lis))
                               (if (pair? lis)
                                   (cons delim (cons (car lis) (recur (cdr lis))))
                                   final)))))
            
              (cond ((pair? strings)
                     (##string-concatenate
                      (case grammar
                      
                        ((infix strict-infix)
                         (cons (car strings) (buildit (cdr strings) '())))
		       
                        ((prefix) (buildit strings '()))
		       
                        ((suffix)
                         (cons (car strings) (buildit (cdr strings) (list delim))))
		       
                        (else (error "Illegal join grammar"
                                     grammar string-join)))))
		   
                    ((eq? grammar 'strict-infix)
                     (error "Empty list cannot be joined with STRICT-INFIX grammar."
                            string-join))
		   
                    (else ""))))    ; Special-cased for infix grammar.
          (error "Not every element of strings is a string: (string-join " strings ")"))
      (error "strings is not a list: (string-join " strings delim grammar ")")))

(declare (generic) (safe))
