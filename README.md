# declarative-spold
This is a small Clojure library for writing
[EcoSpold 1](https://www.ecoinvent.org/database/older-versions/ecoinvent-version-2/methodology-of-ecoinvent-2/ecospold1/ecospold1.html)
data sets in a declarative style. While EcoSpold 1 is maybe outdated and was
replaced by [EcoSpold 2](https://www.ecoinvent.org/data-provider/data-provider-toolkit/ecospold2/ecospold2.html)
it is still a beautiful simple LCA data format when you replace the XML with
[s-expressions](https://en.wikipedia.org/wiki/S-expression):

```clojure
(eco-spold
  (data-set
    (qref
      :name    "Steel production"
      :comment "A simple process data set ...")
    (output
      :type    "product"  :name "Steel"
      :amount  1.0        :unit "kg")
    (input
      :type   "product"  :name  "Electricity"
      :amount 42.0       :unit  "kWh")
    (output
      :name          "CO2"
      :category      "Emissions to air"
      :sub-category  "unspecified"
      :amount  2.0   :unit "kg")))
```

This example may looks like the declaration of a data structure but these are
function calls where the result of an inner function call is passed as an
argument to the outer call. The result of this call tree can be then bound to
a variable, written to an XML file, or just printed in a pretty format:

```clojure
(def data
  (eco-spold
    (data-set
      (qref :name "Steel production")
      (output :type :p :name "Steel" :amount 1.0 :unit "kg"))))

;; write the data to a file
(write data "path/to/file.xml")
;; you can also write them in a pretty format
(write-pretty data "path/to/file.xml")
;; or just print the XML ...
(print-pretty data)
```

The four lines above will produce the following XML which you can directly
import into openLCA (note that this is not valid against the EcoSpold 1 schema
definition but openLCA happily accepts this):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<ecoSpold xmlns="http://www.EcoInvent.org/EcoSpold01">
  <dataset>
    <metaInformation>
      <processInformation>
        <referenceFunction name="Steel production"/>
      </processInformation>
    </metaInformation>
    <flowData>
      <exchange name="Steel" meanValue="1.0" unit="kg">
        <outputGroup>0</outputGroup>
      </exchange>
    </flowData>
  </dataset>
</ecoSpold>
```

## Usage
You could directly create data sets in an interactive REPL session: install
[Leiningen](https://leiningen.org/), checkout and navigate to this project, and
start a REPL:

```bash
cd ../path/to/declarative-spold
lein repl
```

Then you can use it like this:

```clojure
user=> (use 'spold.core)
user=> (def data (eco-spold (data-set)))  ;; etc.
user=> (print-pretty data)
```

If you want to use it in your clojure projects you can add it to your
dependencies with the following coordinates:

```clojure
;; TODO
```

## Format Details
In the following

### Data sets

An EcoSpold data set can contain multiple process data sets which you just pass
into the `eco-spold` function:

```clojure
(eco-spold
  (data-set
    (qref :name "Steel production"))
  (data-set
    (qref :name "Electricity production")))
```

### The reference function

* `:name` - the name of the output product; or process name
* `:category` - the process or product category
* `:sub-category` - the sub-category of the process or product
* `:amount` - the amount of the reference product
* `:unit` - the unit of the reference product
* `:comment` - a general comment

```clojure
(qref
  :name "Steel production"
  :amount 1.0
  :unit "kg")
```

### Geography

* `:location` - the location code
* `:comment` - description of the location



### Inputs and Outputs
A process can have inputs and outputs of flows which are called
_exchanges_. You can call the `input` and `output` functions to
create such exchanges with the following attributes:

* `:type` - the flow type
* `:name` - the flow name
* `:category` - the flow category
* `:sub-category` - the sub-category of the flow
* `:amount` - the amount of the exchange
* `:unit` - the unit of the exchange
* `:location` - an optional location code for the flow
* `:comment` - some text to describe the exchange

The `:type` attribute indicates wether the flow is a _product_,
_co-product_, _waste_, or _elementary flow_. We identify this type by
the first letter of the string or symbol you pass into this field
(ignoring the case):

* `"p"` -> product
* `"c"` -> co-product (in EcoSpold 1 you should have only one
  (reference) product; the other product output should be tagged as
  co-products
* `"e"` -> elementary flow
* `"w"` -> waste flow

If nothing matches, the flow type is set to _elementary flow_ by
default. Thus all these values will result to _product_ as type:
`"p"`, `Product flow`, `:product`. Here is an example of a product
output:

```clojure
(output
  :type      :product
  :name      "Steel"
  :category  "metals"
  :amount    1.0
  :unit      "kg")
```

Note that we support different flow types but in general you should
only use products and elementary flows in EcoSpold 1 data sets to
be compatible with LCA tools.

Instead of the input and output function, you can also use the
`exchange` function which takes an additional `:direction`
attribute. This can be again a symbol and or string and we also take
here just the first letter to identify the direction:

* `"i"` -> input
* `"o"` -> output

. This is equivalent to the example above:

```clojure
(exchange
  :direction :output
  :type      :product
  :name      "Steel"
  :category  "metals"
  :amount    1.0
  :unit      "kg")
```
