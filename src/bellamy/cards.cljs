(ns bellamy.cards)

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