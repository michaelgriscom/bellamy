(ns bellamy.calculator)

(def budget-categories [:groceries :shopping])

(def budget-category-tree {:costco :groceries,
                           :groceries :all,
                           :shopping :all,
                           :travel :all})

(defn get-rewards [card expense]
  [(first expense)
   (* (card (first expense))
      (last expense))])

(defn calculate-rewards [budget card]
  (into {}
        (map
         (partial get-rewards card)
         (seq budget))))

(comment
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
  )