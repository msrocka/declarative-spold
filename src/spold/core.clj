(ns spold.core
  (:gen-class)
  (:require [clojure.data.xml :as xml]
            [clojure.string :as string]
            [clojure.java.io :as io]))


(defn- qn
  "Returns the qualified name of the given tag."
  [tag]
  (xml/qname "http://www.EcoInvent.org/EcoSpold01" (name tag)))


(defn- elem
  "Just binds the name 'elem' to the 'xml/element function."
  ([tag] (xml/element (qn tag)))
  ([tag attrs] (xml/element (qn tag) attrs))
  ([tag attrs & content] (xml/element (qn tag) attrs content)))


(defn pretty-string
  [e]
  (xml/indent-str e))


(defn print-pretty
  [e]
  (println (pretty-string e)))


(defn write
  "Write the XML elements to the given file."
  [espold file]
  (with-open [writer (io/writer file :encoding "utf-8")]
    (xml/emit espold writer)))


(defn write-pretty
  "Write the XML elements to the given file in a pretty format.
   Note that this function is not intended for heavy production use."
  [espold file]
  (with-open [writer (io/writer file :encoding "utf-8")]
    (.write writer (pretty-string espold))))


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


(defn strip-nils
  "Remove the keys with nil-values from the attributes of the
   given element."
  [el]
  (if (nil? el)
    nil
    (assoc el :attrs
      (reduce
        (fn [next-m akey]
          (if (nil? (akey next-m)) (dissoc next-m akey) next-m))
        (:attrs el)
        (keys (:attrs el))))))


(defn filter-elems
  [tag-name elems]
  (filter #(= tag-name (xml/qname-local (:tag %1))) elems))


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
  [& content]
  (let [qref- (first (filter-elems "referenceFunction" content))
        proc-info (map strip-nils (filter some? [qref-]))
        exchanges (map strip-nils (filter-elems "exchange" content))]
    (elem
      :dataset
      {}
      (elem
        :metaInformation
        {}
        (elem
          :processInformation
          {}
          proc-info))
      (elem
       :flowData
       {}
       exchanges))))


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


(defn exchange
  [& {:keys [type name category sub-category location
             amount unit comment direction number]
      :as atts}]
  (elem
   :exchange
   {:number         number
    :name           name
    :category       category
    :subCategory    sub-category
    :meanValue      amount
    :unit           unit
    :location       location
    :generalComment comment}
   (exchange-group type direction)))



(defn input
  [& {:as atts}]
  (apply exchange (flatten (vec (assoc atts :direction :input)))))


(defn output
  [& {:as atts}]
  (apply exchange (flatten (vec (assoc atts :direction :output)))))
