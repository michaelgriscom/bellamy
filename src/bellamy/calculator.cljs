(ns bellamy.calculator)

;; (def budget-categories [:groceries :shopping])

;; (def budget-category-tree {:costco :groceries,
;;                            :groceries :all,
;;                            :shopping :all})

(defn get-rewards [card expense]
  [(first expense) 
   (* (card (first expense))
      (last expense))])

(defn calculate-rewards [budget card]
  (into {} 
        (map 
         (partial get-rewards card) 
         (seq budget))))