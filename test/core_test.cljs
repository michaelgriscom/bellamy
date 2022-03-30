(ns core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
           ))

(deftest sample-test
  (testing "sample test"
    (is (= 1 2))))