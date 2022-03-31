(ns core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bellamy.calculator :as calc]))

(deftest basic-rewards
  (testing "basic rewards"
    (let [budget {:groceries 100, :shopping 200}
          card {:groceries 0.02, :shopping 0.02}]
      (is (= {:groceries 2, :shopping 4} (calc/calculate-rewards budget card))))))