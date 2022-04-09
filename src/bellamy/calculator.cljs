(ns bellamy.calculator
  (:require [clojure.spec.alpha :as s]))

;; (def budget-categories [:groceries :shopping])

(def budget-category-tree {:costco :groceries,
                           :groceries :all,
                           :shopping :all,
                           :travel :all})

(def card-networks #{:amex :visa :mastercard})
(def card-types #{:business :personal})

(def sample-budget
  [{:category :travel, :amount 100.50},
   {:category :costco, :amount 100.50},
   {:category :dining, :amount 100.50}])

(def cards
  [{:bellamy/credit-card.name "Fidelity Visa",
    :bank "Fidelity",
    :url "https://www.fidelityrewards.com/",
    :card-network :visa,
    :type :personal
    :reward-type :cash-back,
    :signup-bonus nil,
    :rewards {:all {:cash-back (fn [expense] (* 0.02 expense))}}},
   {:bellamy/credit-card.name "Capital One Quicksilver",
    :bank "Capital One",
    :url "https://www.capitalone.com/credit-cards/cash-back/quicksilver/,"
    :card-network :visa,
    :type :personal,
    :reward-type :cash-back,
    :signup-bonus {:min-spend 500.00, :bonus {:cash-back 200}, :time {:months 3}},
    :rewards {:all {:cash-back (fn [expense] (* 0.015 expense))}}}])


(s/valid? :bellamy/credit-card {:bellamy/credit-card.name "Fidelity Visa"})

(s/valid? (s/coll-of :bellamy/credit-card) cards)

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
  (expense :category))

(defn get-expense-amount [expense]
  (expense :amount))

;; (defn get-expense-category [expense]
;;   (first expense))

;; (defn get-expense-amount [expense]
;;   (second expense))

(defn get-rewards-value [card expense]
  (calculate-reward-value (last
                           (get-card-reward card (get-expense-category expense)))
                          (get-expense-amount expense)))

(defn get-card-reward-info [expense card]
  (let [[card-reward-category reward-function]
        (->> (get-expense-category expense)
             (get-card-reward card))
        expense-amount (get-expense-amount expense)]
    {:card-reward-category card-reward-category,
     :reward-amount (calculate-reward-value reward-function expense-amount)}))

(defn get-cards-reward-info [expense cards]
  (map #(assoc (get-card-reward-info expense %) :card %) cards))

(defn get-best-reward [expense cards]
  (->> (get-cards-reward-info expense cards)
       (apply max-key :reward-amount)))

(defn get-total-rewards [cards budget]
  (map #(assoc (get-best-reward % cards) :expense %) budget))


(comment
  ;; (def budget
  ;;   {:travel 100.50,
  ;;    :costco 200.00,
  ;;    :groceries 300.00})
  (def budget
    [{:category :travel, :amount 100.50},
     {:category :costco, :amount 100.50},
     {:category :dining, :amount 100.50}])

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

  (get-best-reward (first budget) [fidelity-visa super-visa])

  (get-total-rewards [fidelity-visa, super-visa] budget)

;; (map (partial get-card-reward-info) cards)

  (get-card-reward-info (first budget) fidelity-visa)

  ((partial get-card-reward-info (first budget)) fidelity-visa)

;; #(assoc (partial get-card-reward-info expense) :card %)

  (def test-list ({:amount 0} {:amount 2}))
  (def test-vec [{:amount 0} {:amount 2}])
  (max-key :amount test-list)
  (apply max-key :amount test-vec)

  (:amount (first (get-cards-reward-info (first budget) [fidelity-visa, super-visa])))

  (max-key :amount (get-cards-reward-info (first budget) [fidelity-visa, super-visa]))


  (def expense (first budget))
  (def cards [fidelity-visa super-visa])
  (get-card-reward-info expense fidelity-visa)
  (map (partial get-card-reward-info expense) cards))



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