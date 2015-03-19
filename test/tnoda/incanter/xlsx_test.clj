(ns tnoda.incanter.xlsx-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [tnoda.incanter.xlsx :refer :all]
            [incanter.core :as core]))

(def xlsx (delay (io/resource "example.xlsx")))

(deftest test-read-xlsx
  (testing "Tidy excel reading"
    (let [ds (read-xlsx @xlsx 0 :header true)]
      (is (instance? incanter.core.Dataset ds))
      (is (= ["Alice" "Bob"] (core/col-names ds)))
      (is (= '(3.0 7.0 1.0 2.0 4.0) (core/$ "Alice" ds)))))
  (testing "Tidy excel reading by sheet name"
    (let [ds (read-xlsx @xlsx :sheet-name "tidy" :header true)]
      (is (instance? incanter.core.Dataset ds))
      (is (= ["Alice" "Bob"] (core/col-names ds)))))
  (testing "header-keywords option"
    (let [ds (read-xlsx @xlsx 0 :header true :header-keywords true)]
      (is (instance? incanter.core.Dataset ds))
      (is (= [:Alice :Bob] (core/col-names ds)))))
  (testing "header option"
    (let [ds (read-xlsx @xlsx 0)]
      (is (instance? incanter.core.Dataset ds))
      (is (= (range 2) (core/col-names ds)))))
  (testing "as-dataset option"
    (let [ds (read-xlsx @xlsx 0 :as-dataset false)]
      (is (not (instance? incanter.core.Dataset ds)))
      (is (seq? ds))
      (is (every? seq? ds)))))

