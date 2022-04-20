(ns bellamy.cards)

(defn percent-back [reward-type percent]
  {:reward-type reward-type,
   :reward-amount-fn (fn [expense-amount] (* 0.01 percent expense-amount))})

(def banks
  {:fidelity {:friendly-name "Fidelity"},
   :capital-one {:friendly-name "Capital One"},
   :chase {:friendly-name "Chase"},
   :citi {:friendly-name "Citi"}})

(def card-networks
  {:amex {:friendly-name "American Express"},
   :visa {:friendly-name "Visa"},
   :mastercard {:friendly-name "Mastercard"},
   :discover {:friendly-name "Discover"}})

(def cards
  [{:name "Fidelity Visa",
    :bank :fidelity,
    :url "https://www.fidelityrewards.com/",
    :card-network :visa,
    :card-type :personal
    :reward-type :cash-back,
    :rewards {:all (percent-back :cash-back 2)}},
   {:name "Capital One Quicksilver",
    :bank :capital-one,
    :url "https://www.capitalone.com/credit-cards/cash-back/quicksilver/",
    :card-network :visa,
    :card-type :personal,
    :reward-type :cash-back,
    :signup-bonus {:min-spend 500.00, :bonus 200.00, :months 3 :reward-type :cash-back},
    :rewards {:all {:cash-back (fn [expense] (* 0.015 expense))}}}
   {:name "Costco anywhere Visa",
    :bank :citi,
    :url "https://www.citi.com/credit-cards/citi-costco-anywhere-visa-credit-card",
    :card-network :visa,
    :card-type :personal,
    :rewards {:gas (percent-back :cash-back 4),
              :dining (percent-back :cash-back 3),
              :travel (percent-back :cash-back 3),
              :costco (percent-back :cash-back 2),
              :all (percent-back :cash-back 1)}},
   {:name "Chase Sapphire Preferred",
    :bank :chase,
    :url "https://creditcards.chase.com/rewards-credit-cards/sapphire/preferred",
    :card-network :visa,
    :card-type :personal,
    :reward-type :chase-ultimate-rewards,
    ;; TODO: Redeeming for travel gives more
    :rewards {:dining (percent-back :chase-ultimate-rewards 3),
              :travel (percent-back :chase-ultimate-rewards 2),
              :all (percent-back :chase-ultimate-rewards 1)},
    :signup-bonus {:min-spend 4000.00, :bonus 80000, :months 3 :reward-type :chase-ultimate-rewards},
    :annual-fee 95.00},
   {:name "Chase Sapphire Reserve",
    :bank :chase,
    :url "https://creditcards.chase.com/rewards-credit-cards/sapphire/reserve",
    :card-network :visa,
    :card-type :personal,
    :reward-type :chase-ultimate-rewards,
    ;; TODO: Redeeming for travel gives more
    ;; TODO: Travel credit
    :rewards {:dining (percent-back :chase-ultimate-rewards 3),
              :travel (percent-back :chase-ultimate-rewards 3),
              :all (percent-back :chase-ultimate-rewards 1)},
    :signup-bonus {:min-spend 4000.00, :bonus 50000, :months 3 :reward-type :chase-ultimate-rewards},
    :annual-fee 550.00},
   {:name "Chase Freedom Unlimited",
    :bank :chase,
    :url "https://creditcards.chase.com/cash-back-credit-cards/freedom/unlimited",
    :card-network :visa,
    :card-type :personal,
    :reward-type :cash-back,
    :rewards {:dining (percent-back :cash-back 3),
              :drugstore (percent-back :cash-back 3),
              :all (percent-back :cash-back 1.5)},
    :signup-bonus {:min-spend 500.00, :bonus 200.00, :months 3 :reward-type :cash-back},
    }])