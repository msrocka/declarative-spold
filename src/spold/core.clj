(ns spold.core
  (:gen-class)
  (:require [clojure.data.xml :as xml]))


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


(defn- exchange-direction
  "")


(defn- exchange [& {:keys [name amount unit category
                           sub-category comment type
                           direction]
                    :as attrs}]
  (let))

(defn input
  [& {:keys []}])
