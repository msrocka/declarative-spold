(ns spold.core
  (:gen-class)
  (:require [clojure.data.xml :as xml]
            [clojure.string :as string]))


(defn- qn
  "Returns the qualified name of the given tag."
  [tag]
  (xml/qname "http://www.EcoInvent.org/EcoSpold01" (name tag)))


(defn- elem
  "Just binds the name 'elem' to the 'xml/element function."
  ([tag] (xml/element (qn tag)))
  ([tag attrs] (xml/element (qn tag) attrs))
  ([tag attrs & content] (xml/element (qn tag) attrs content)))


(defn as-string
  [e]
  (xml/indent-str e))


(defn pretty-print
  [e]
  (println (as-string e)))


(defn first-letter
  "Get the first lower case letter from the given string
   or symbol."
  [v]
  (letfn [(firstl
            [s]
            (string/lower-case (subs (string/trim s) 0 1)))]
    (cond
      (string? v) (firstl v)
      (keyword? v) (firstl (name v))
      :else nil)))


(defn flow-type
  "Infer the flow type from the given value."
  [v]
  (let [s (first-letter v)]
    (cond
      (= s "p") :product
      (= s "c") :co-product
      (= s "w") :waste
      :else     :elementary)))


(defn exchange-direction
  "Infer the exchange direction from the given value"
  [v]
  (let [s (first-letter v)]
    (if (= s "i") :input :output)))


(defn eco-spold
  [& data-sets]
  (elem
    :ecoSpold {:xmlns "http://www.EcoInvent.org/EcoSpold01"}
    data-sets))


(defn data-set
  []
  (elem :dataset))


(defn qref
  [& {:keys [name amount unit category sub-category comment]}]
  (elem
    :referenceFunction
    {:name name
     :amount amount
     :unit unit
     :category category
     :subCategory sub-category
     :generalComment comment}))


(defn exchange-group
  [flow-type- direction-]
  (let [ft (flow-type flow-type-)
        dr (exchange-direction direction-)]
    ;; not all combination make sense but we try our best to
    ;; answer with something that is useful...
    (cond
      (= [ft dr] [:product    :input])  (elem :inputGroup {} 5)
      (= [ft dr] [:co-product :input])  (elem :inputGroup {} 5)
      (= [ft dr] [:waste      :input])  (elem :inputGroup {} 5)
      (= dr :input)                     (elem :inputGroup {} 4)
      (= [ft dr] [:product    :output]) (elem :outputGroup {} 0)
      (= [ft dr] [:co-product :output]) (elem :outputGroup {} 2)
      (= [ft dr] [:waste      :output]) (elem :outputGroup {} 3)
      :else                             (elem :outputGroup {} 4))))


(defn exchange [& {:keys [type name category sub-category
                          amount unit comment direction]
                    :as attrs}]
  nil)


(defn input
  [& {:keys []}])
