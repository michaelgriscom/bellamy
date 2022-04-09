(ns core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bellamy.calculator :as calc]
            [clojure.spec.alpha :as s]))

;; (deftest basic-rewards
;;   (testing "basic rewards"
;;     (let [budget {:groceries 100, :shopping 200}
;;           card {:groceries 0.02, :shopping 0.03}]
;;       (is (= {:groceries 2, :shopping 6}
;;              (calc/calculate-rewards budget card))))))

;; (deftest basic-rewards-2
;;   (testing "basic rewards"
;;     (let [budget {:groceries 100, :shopping 200}
;;           card {:shopping 0.01}]
;;       (is (= {:groceries 0, :shopping 2}
;;              (calc/calculate-rewards budget card))))))


(s/def :bellamy/credit-card.name string?)

(s/def :bellamy/credit-card (s/keys :req [:bellamy/credit-card.name]))

(deftest cards-spec
  (testing "ensure that the default cards match spec"
    (is (s/valid? (s/coll-of :bellamy/credit-card) calc/cards))))

(deftest failure
  (testing "failure"
    (is (= 1 1))))