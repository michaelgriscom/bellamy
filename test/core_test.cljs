(ns core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bellamy.calculator :as calc]
            [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [bellamy.cards :as cards]
            [bellamy.samples :as samples]))

;; Credit card specs
(s/def :bellamy/card-network #{:amex :visa :mastercard})
(s/def :bellamy/card-type #{:business :personal})
(s/def :bellamy/name string?)
(s/def :bellamy/bank string?)

(s/def :bellamy/min-spend number?)
(s/def :bellamy/bonus number?)
(s/def :bellamy/months number?)

(s/def :bellamy/signup-bonus (s/keys :req-un [:bellamy/min-spend :bellamy/bonus :bellamy/months]))

(def url-regex #"(?i)^(?:(?:https?|ftp)://)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,}))\.?)(?::\d{2,5})?(?:[/?#]\S*)?$")
(s/def :bellamy/url (s/and string? #(re-matches url-regex %)))


(def expense-category? #{:groceries :shopping :travel :costco :restaraunts :gas})
(s/def :bellamy/expense-category expense-category?)

(s/def :bellamy/reward-category (set/union  expense-category? #{:all}))
(s/def :bellamy/rewards (s/map-of :bellamy/reward-category any?))


(s/def :bellamy/credit-card (s/keys :req-un
                                    [:bellamy/name
                                     :bellamy/bank
                                     :bellamy/card-network
                                     :bellamy/card-type
                                     :bellamy/url
                                     :bellamy/rewards]
                                    :opt-un
                                    [:bellamy/signup-bonus]
                                    ))

(s/explain :bellamy/reward-category :all)
(s/explain :bellamy/expense-category :gas)

(comment
  (s/explain (s/coll-of :bellamy/credit-card) cards/cards)
  )

(deftest cards-spec
  (testing "ensure that the default cards match spec"
    (is (s/valid? (s/coll-of :bellamy/credit-card) cards/cards))))


;; Expense specs
(s/def :bellamy/amount number?)
(s/def :bellamy/expense (s/keys :req-un
                                [:bellamy/expense-category
                                 :bellamy/amount]))

(deftest budget-spec
  (testing "ensure that the default budget matches spec"
    (is (s/valid? (s/coll-of :bellamy/expense) samples/sample-budget))))

(def test-card {:name "Fidelity Visa",
                :bank "Fidelity",
                :url "https://www.fidelityrewards.com/",
                :card-network :visa,
                :card-type :personal
                :reward-type :cash-back,
                :signup-bonus nil,
                :rewards {:all (cards/percent-back :cash-back 2)}})

(def test-card-2 {:name "Super Visa",
                  :bank "Super",
                  :card-network :visa,
                  :card-type :personal
                  :reward-type :cash-back,
                  :annual-fee 100,
                  :rewards {:groceries (cards/percent-back :cash-back 50),
                            :all (cards/percent-back :cash-back 1)}})

(def test-expense {:expense-category :groceries, :amount 100})
(def test-expense-2 {:expense-category :shopping, :amount 200})

(def test-budget [test-expense test-expense-2])

(deftest get-card-reward-info
  (testing "ensure that the correct reward info is returned"
    (is (= (calc/get-card-reward-info test-expense test-card)
           {:card-reward-category :all, :reward-value 2}))))

(deftest get-best-reward
  (testing "ensure that the best reward is correct"
    (let [best-reward (calc/get-best-reward test-expense [test-card test-card-2])]
      (is (= best-reward
             {:card-reward-category :groceries, :reward-value 50, :card test-card-2})))))

(deftest get-total-rewards
  (testing "ensure that the total rewards are correct"
    (let [rewards (calc/get-total-rewards [test-card test-card-2] test-budget)]
      (is (= rewards [{:card-reward-category :groceries,
                       :reward-value 50, :card test-card-2, :expense test-expense}
                      {:card-reward-category :all,
                       :reward-value 4, :card test-card, :expense test-expense-2}])))))

(deftest get-annual-rewards
  (testing "ensure that the net rewards are correct"
    (let [annual-rewards (calc/get-annual-rewards [test-card test-card-2] test-budget)]
      (is (= (- (annual-rewards :total-rewards) (annual-rewards :annual-fees)) -46)))))

(s/def :bellamy/friendly-name string?)
(s/def :bellamy/valuation (s/map-of :bellamy/point-system number?))
(s/def :bellamy/points-valuation (s/keys :req-un
                                         [:bellamy/friendly-name
                                          :bellamy/url
                                          :bellamy/valuation]))

(deftest points-valuation-spec
  (testing "ensure that the default point valuation matches spec"
    (is (s/valid? :bellamy/points-valuation calc/points-guy-valuation))))

(comment
  (s/explain :bellamy/points-valuation calc/points-guy-valuation)
  )

;; Points specs
(s/def :bellamy/point-system #{:accor-le-club
                               :aeroplan-loyalty-program
                               :alaska-mileage-plan
                               :american-aadvantage
                               :american-express-membership-rewards
                               :amtrak-guest-rewards
                               :ana-mileage-club
                               :asia-miles
                               :avianca-lifemiles
                               :avios
                               :bank-of-america-premium-rewards
                               :barclaycard-arrival-miles
                               :best-western-rewards
                               :bilt-rewards
                               :brex-exclusive-rewards
                               :capital-one-rewards
                               :chase-ultimate-rewards
                               :choice-privileges
                               :citi-thank-you-points
                               :delta-skymiles
                               :diners-club-rewards
                               :discover-rewards
                               :emirates-skywards
                               :etihad-guest
                               :flying-blue
                               :frontier-miles
                               :hawaiian-miles
                               :hilton-honors
                               :ihg-rewards-club
                               :jetblue-trueblue-rewards-program
                               :korean-air-skypass
                               :marriott-bonvoy
                               :radisson-rewards
                               :singapore-krisflyer
                               :southwest-rapid-rewards
                               :spirit-airlines-free-spirit
                               :turkish-airlines-miles-and-smiles
                               :us-bank-flexperks
                               :united-mileage-plus
                               :virgin-atlantic-flying-club
                               :world-of-hyatt-loyalty-program
                               :wyndham-rewards})
