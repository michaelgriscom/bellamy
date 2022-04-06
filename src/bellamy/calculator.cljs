(ns bellamy.calculator)

;; (def budget-categories [:groceries :shopping])

(def budget-category-tree {:costco :groceries,
                           :groceries :all,
                           :shopping :all,
                           :travel :all})

;; (defn get-rewards [card expense]
;;   [(first expense)
;;    (* (card (first expense))
;;       (last expense))])

;; (defn calculate-rewards [budget card]
;;   (into {}
;;         (map
;;          (partial get-rewards card)
;;          (seq budget))))

;; TODO: add dates to budget
(def budget
  {:travel 100.50,
   :costco 200.00,
   :groceries 300.00})
    ;; (def budget
    ;;   [{:category :travel, :amount :100.50},
    ;;    {:category :costco, :amount :100.50},
    ;;    {:category :dining, :amount :100.50}])

(def fidelity-visa
  {:name "Fidelity Visa",
   :bank "Fidelity",
   :card-network :visa,
   :type :personal
   :reward-type :cash-back,
   :signup-bonus nil,
   :rewards {:all {:cash-back (fn [expense] (* 0.02 expense))}}})

(def super-visa
  {:name "Super Visa",
   :bank "Super",
   :card-network :visa,
   :type :personal
   :reward-type :cash-back,
   :signup-bonus nil,
   :rewards {:all {:cash-back (fn [expense] (* 0.5 expense))}}})

(defn get-rewards-category
  "Gets the most narrow rewards category that a given expense matches for a given set of rewards"
  [rewards expense-category]
  (cond
    (nil? expense-category) nil
    (rewards expense-category) [expense-category (rewards expense-category)]
    :else (recur rewards (budget-category-tree expense-category))))

(defn get-card-reward
  "Gets the card reward for a given expense type"
  [card expense-type]
  (get-rewards-category (card :rewards) expense-type))

(defn calculate-reward-value
  "Calculates the reward value for a given expense"
  [reward expense]
  (cond (reward :cash-back) ((reward :cash-back) expense)
        :else nil))

(defn get-expense-category [expense]
  (first expense))

(defn get-expense-amount [expense]
  (last expense))

(defn get-rewards-value [card expense]
  (calculate-reward-value (last
                           (get-card-reward card (get-expense-category expense)))
                          (get-expense-amount expense)))

(defn get-category-from-card-reward [card-reward]
  (first card-reward))

(defn get-reward-function-from-card-reward [card-reward]
  (last card-reward))

;; (defn get-rewards-info [card expense]
;;   )
;; (def expense (first budget))
;; (def card fidelity-visa)
(defn get-card-reward-info [expense card]
  (let [[card-reward-category reward-function]
        (->> (get-expense-category expense)
             (get-card-reward card))
        expense-amount (get-expense-amount expense)]
    [card-reward-category (calculate-reward-value reward-function expense-amount)]))

(get-card-reward-info (first budget) fidelity-visa)

(rest '(2))

;; (defn card-name [card] (card ))
(comment
  ;; invalid, can't recur there
  (defn get-best-reward [expense cards]
    (let [first-reward-info (get-card-reward-info expense (first cards))
          first-card-name ((first cards) :name)]
      (cond (= 1 (count cards)) (conj first-reward-info first-card-name)
            :else (max-key second (conj first-reward-info first-card-name) (recur expense (rest cards))))))
  
  (defn get-best-reward-2 [expense cards]
    (let [card-reward-pairs (map (partial get-card-reward-info) cards)]))
  )




  ;; (->> (map (partial get-card-reward-info expense) cards)
  ;;      (max last)))

(def expense (first budget))
(def cards [fidelity-visa super-visa])
(get-card-reward-info expense fidelity-visa)
(map (partial get-card-reward-info expense) cards)

(get-best-reward (first budget) [fidelity-visa])

;; Goal: (defn get-total-rewards [cards budget])

(comment
  (get-expense-category (first budget))

  (first budget)
  (get-rewards-value fidelity-visa (first budget))

  ;; (defn get-reward-value-full [card expense]
  ;;   {:spend expense,
  ;;    :rewards})

  (get-card-reward fidelity-visa :travel)

  (last (get-card-reward fidelity-visa :travel))

  ((last (get-card-reward fidelity-visa :travel)) :cash-back)

  (calculate-reward-value (last (get-card-reward fidelity-visa :travel)) 100)

  (get-expense-amount (first budget))
  (last (first budget))

  (get-rewards-value fidelity-visa (first budget)))