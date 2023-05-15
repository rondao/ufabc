;; Extensao para capturar a data atual.

;; Epoch do Gambit.
(define *epoch-year* 1970)
;; Fuso-horario brasileiro
(define *fuse* -3)
;; Lista de dias e meses.
(define *month-days* (list (cons 31 "Jan")
                           (cons 28 "Fev")
                           (cons 31 "Mar")
                           (cons 30 "Abr")
                           (cons 31 "Mai")
                           (cons 30 "Jun")
                           (cons 31 "Jul")
                           (cons 31 "Ago")
                           (cons 30 "Set")
                           (cons 31 "Out")
                           (cons 30 "Nov")
                           (cons 31 "Dez")))
;; Lista de dias e meses num ano bixesto.
(define *l-month-days* (cons (car *month-days*)
                             (cons (cons 29 "Fev") (cddr *month-days*))))

;; Converte um dia de 1 a 365 para formato dia/mes.
(define day->d/m
  (lambda (day m/d)
    (let loop ((current-month (car m/d))
               (rest (cdr m/d))
               (days day))
      (if (<= days (car current-month))
          (cons days (cdr current-month))
          (loop (car rest) (cdr rest) (- days (car current-month)))))))

;; Conversao segundos para anos.
(define sec->years
  (lambda (sec)
    (/ sec 31536000)))

;; Conversao segundos para dias.
(define sec->days
  (lambda (sec)
    (/ (+ (sec->hour sec) *fuse*) 24)))

;; Conversao segundos para minutos.
(define sec->min
  (lambda (sec)
    (/ sec 60)))

;; Conversao segundos para horas.
(define sec->hour
  (lambda (sec)
    (/ sec 3600)))

;; Retorna a data atual no formato: ((hh. mm. ss.) (dd. mm) yy)
(define current-date
  (lambda ()
    (let* ((secs (time->seconds (current-time)))
           (years (flfloor (sec->years secs)))
           (leaps (flfloor (/ (- years 2) 4)))
           (days (- (flfloor (sec->days secs)) leaps))
           (time (list (modulo (+ (flfloor (sec->hour secs)) *fuse*) 24)
                       (modulo (flfloor (sec->min secs)) 60)
                       (modulo (flfloor secs) 60))))
      (if (= (modulo (- years 2) 4) 0)
          (list time (day->d/m (modulo days 365) *l-month-days*) (+ *epoch-year* years))
          (list time (day->d/m (modulo days 365) *month-days*) (+ *epoch-year* years))))))