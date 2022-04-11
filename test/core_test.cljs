(ns core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bellamy.calculator :as calc]
            [clojure.spec.alpha :as s]))

;; Credit card specs
(s/def :bellamy/card-network #{:amex :visa :mastercard})
(s/def :bellamy/card-type #{:business :personal})
(s/def :bellamy/name string?)
(s/def :bellamy/bank string?)

(def url-regex #"(?i)^(?:(?:https?|ftp)://)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,}))\.?)(?::\d{2,5})?(?:[/?#]\S*)?$")
(s/def :bellamy/url (s/and string? #(re-matches url-regex %)))


(s/def :bellamy/credit-card (s/keys :req-un
                                    [:bellamy/name
                                     :bellamy/bank
                                     :bellamy/card-network
                                     :bellamy/card-type
                                     :bellamy/url]))

(s/explain (s/coll-of :bellamy/credit-card) calc/cards)

(deftest cards-spec
  (testing "ensure that the default cards match spec"
    (is (s/valid? (s/coll-of :bellamy/credit-card) calc/cards))))


;; Expense specs
(s/def :bellamy/expense-category #{:groceries :shopping :travel :costco :restaraunts})
(s/def :bellamy/amount number?)
(s/def :bellamy/expense (s/keys :req-un
                                [:bellamy/expense-category
                                 :bellamy/amount]))

(deftest budget-spec
  (testing "ensure that the default budget matches spec"
    (is (s/valid? (s/coll-of :bellamy/expense) calc/sample-budget))))

(def test-card {:name "Fidelity Visa",
                :bank "Fidelity",
                :url "https://www.fidelityrewards.com/",
                :card-network :visa,
                :card-type :personal
                :reward-type :cash-back,
                :signup-bonus nil,
                :rewards {:all {:cash-back (fn [expense] (* 0.02 expense))}}})

(def test-card-2 {:name "Super Visa",
                  :bank "Super",
                  :card-network :visa,
                  :card-type :personal
                  :reward-type :cash-back,
                  :signup-bonus nil,
                  :rewards {:all {:cash-back (fn [expense] (* 0.5 expense))}}})

(def test-expense {:expense-category :groceries, :amount 100})

(deftest get-card-reward-info
  (testing "ensure that the correct reward info is returned"
    (is (= (calc/get-card-reward-info test-expense test-card)
           {:card-reward-category :all, :reward-amount 2}))))

(deftest get-best-reward
  (testing "ensure that the best reward is correct"
    (let [best-reward (calc/get-best-reward test-expense [test-card test-card-2])]
      (is (= best-reward 
             {:card-reward-category :all, :reward-amount 50, :card test-card-2})))))