(ns bellamy.calculator
  (:require [clojure.math.combinatorics :as c]))

(def budget-category-tree {:costco :groceries,
                           :groceries :all,
                           :shopping :all,
                           :travel :all,
                           :restaraunts :all,})

(def sample-budget
  [{:expense-category :travel, :amount 100.50},
   {:expense-category :costco, :amount 100.50},
   {:expense-category :restaraunts, :amount 100.50}])

(defn percent-back [reward-type percent]
  {:reward-type reward-type,
   :reward-amount-fn (fn [expense-amount] (* 0.01 percent expense-amount))})

(def cards
  [{:name "Fidelity Visa",
    :bank "Fidelity",
    :url "https://www.fidelityrewards.com/",
    :card-network :visa,
    :card-type :personal
    :reward-type :cash-back,
    :rewards {:all (percent-back :cash-back 2)}},
   {:name "Capital One Quicksilver",
    :bank "Capital One",
    :url "https://www.capitalone.com/credit-cards/cash-back/quicksilver/",
    :card-network :visa,
    :card-type :personal,
    :reward-type :cash-back,
    :signup-bonus {:min-spend 500.00, :bonus 200.00, :months 3},
    :rewards {:all {:cash-back (fn [expense] (* 0.015 expense))}}}
   {:name "Costco anywhere Visa",
    :bank "Citi",
    :url "https://www.citi.com/credit-cards/citi-costco-anywhere-visa-credit-card",
    :card-network :visa,
    :card-type :personal,
    :rewards {:gas (percent-back :cash-back 4),
              :restaraunts (percent-back :cash-back 3),
              :travel (percent-back :cash-back 3),
              :costco (percent-back :cash-back 2),
              :all (percent-back :cash-back 1)}}])


(defn get-rewards-category
  "Gets the most narrow rewards category that a given expense matches for a given set of rewards"
  [rewards expense-category]
  (cond
    (nil? expense-category) nil
    (rewards expense-category) [expense-category (rewards expense-category)]
    :else (recur rewards (budget-category-tree expense-category))))

(defn get-card-reward
  "Gets the card reward for a given expense type"
  [card expense-category]
  (get-rewards-category (card :rewards) expense-category))

(defn calculate-reward-amount
  "Calculates the reward value for a given expense"
  [reward expense-amount]
  (cond (= (reward :reward-type) :cash-back) ((reward :reward-amount-fn) expense-amount)
        :else nil))

(defn get-card-reward-info [expense card]
  (let [[card-reward-category reward-function]
        (->> (expense :expense-category)
             (get-card-reward card))
        expense-amount (expense :amount)]
    {:card-reward-category card-reward-category,
     :reward-value (calculate-reward-amount reward-function expense-amount)}))

(defn get-cards-reward-info [expense cards]
  (map #(assoc (get-card-reward-info expense %) :card %) cards))

(defn get-best-reward [expense cards]
  (->> (get-cards-reward-info expense cards)
       (apply max-key :reward-value)))

(defn get-total-rewards [cards budget]
  (map #(assoc (get-best-reward % cards) :expense %) budget))


;; Need to handle cash-back
(defn get-reward-value [reward, valuation]
  (let [point-multiplier (valuation (reward :reward-type))]
    (* 0.01 point-multiplier (reward :reward-amount))))

(def points-guy-valuation {:friendly-name "The Points Guy April 2022 valuation",
                           :url "https://thepointsguy.com/guide/monthly-valuations/",
                           :valuation {:accor-le-club	2,
                                       :aeroplan-loyalty-program	1.5,
                                       :alaska-mileage-plan	1.8,
                                       :american-aadvantage	1.77,
                                       :american-express-membership-rewards	2,
                                       :amtrak-guest-rewards	2.5,
                                       :ana-mileage-club	1.4,
                                       :asia-miles	1.3,
                                       :avianca-lifemiles	1.7,
                                       :avios	1.5,
                                       :bank-of-america-premium-rewards	1,
                                       :barclaycard-arrival-miles	1,
                                       :best-western-rewards	0.7,
                                       :bilt-rewards	1.8,
                                       :brex-exclusive-rewards	1.7,
                                       :capital-one-rewards	1.85,
                                       :chase-ultimate-rewards	2,
                                       :choice-privileges	0.6,
                                       :citi-thank-you-points	1.8,
                                       :delta-skymiles	1.41,
                                       :diners-club-rewards	2.1,
                                       :discover-rewards	1,
                                       :emirates-skywards	1.2,
                                       :etihad-guest	1.4,
                                       :flying-blue	1.2,
                                       :frontier-miles	1.1,
                                       :hawaiian-miles	0.9,
                                       :hilton-honors	0.6,
                                       :ihg-rewards-club	0.5,
                                       :jetblue-trueblue-rewards-program	1.3,
                                       :korean-air-skypass	1.7,
                                       :marriott-bonvoy	0.8,
                                       :radisson-rewards	0.4,
                                       :singapore-krisflyer	1.3,
                                       :southwest-rapid-rewards	1.5,
                                       :spirit-airlines-free-spirit	1.1,
                                       :turkish-airlines-miles-and-smiles	1.3,
                                       :us-bank-flexperks	1.5,
                                       :united-mileage-plus	1.21,
                                       :virgin-atlantic-flying-club	1.5,
                                       :world-of-hyatt-loyalty-program	1.7,
                                       :wyndham-rewards	1.1}})

   (defn get-annual-fees [cards]
     (reduce #((fnil + 0) %1 (%2 :annual-fee)) 0 cards))

;; (defn get-annual-rewards [cards budget]
;;   (->> (get-total-rewards cards budget)
;;        (reduce #(+ %1 (%2 :reward-value)) 0)))   

    (defn get-annual-rewards [cards budget]
      (let [total-rewards (get-total-rewards cards budget)]
        {:rewards total-rewards,
         :total-rewards (reduce #(+ %1 (%2 :reward-value)) 0 total-rewards),
         :annual-fees (get-annual-fees cards)}))
   
;; (defn get-net-rewards [cards budget]
;;   (let [annual-rewards (get-annual-rewards cards budget)
;;         annual-fees (get-annual-fees cards)]
;;     (- annual-rewards annual-fees)))

   

(defn get-all-rewards [cards n budget]
  (map #(assoc (get-annual-rewards % budget) :cards %) (c/combinations cards n)))
   
  ;;  (defn get-all-rewards-2 [cards n budget]
  ;;    (map #(get-net-rewards % budget) (c/combinations cards n)))
   
;; (defn get-cards-reward-info [expense cards]
;;   (map #(assoc (get-card-reward-info expense %) :card %) cards))

(comment
  (def budget
    [{:expense-category :travel, :amount 100.50},
     {:expense-category :costco, :amount 100.50},
     {:expense-category :dining, :amount 100.50}])

  (def fidelity-visa
    {:name "Fidelity Visa",
     :bank "Fidelity",
     :card-network :visa,
     :card-type :personal
     :reward-type :cash-back,
     :signup-bonus nil,
     :rewards {:all {:cash-back (fn [expense] (* 0.02 expense))}}})

  (def super-visa
    {:name "Super Visa",
     :bank "Super",
     :card-network :visa,
     :card-type :personal
     :reward-type :cash-back,
     :signup-bonus nil,
     :rewards {:all {:cash-back (fn [expense] (* 0.5 expense))}}})
  
  (get-best-reward (first budget) [fidelity-visa super-visa])

  (get-total-rewards [fidelity-visa, super-visa] budget)

  (get-card-reward-info (first budget) fidelity-visa)

  ((partial get-card-reward-info (first budget)) fidelity-visa)

  (def test-list ({:amount 0} {:amount 2}))
  (def test-vec [{:amount 0} {:amount 2}])
  (max-key :amount test-list)
  (apply max-key :amount test-vec)

  (:amount (first (get-cards-reward-info (first budget) [fidelity-visa, super-visa])))

  (max-key :amount (get-cards-reward-info (first budget) [fidelity-visa, super-visa]))


  (def expense (first budget))
  (def cards [fidelity-visa super-visa])
  (get-card-reward-info expense fidelity-visa)
  (map (partial get-card-reward-info expense) cards)
  
  )
