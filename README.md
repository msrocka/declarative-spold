# declarative-spold
This is a small Clojure library for writing
[EcoSpold 1](https://www.ecoinvent.org/database/older-versions/ecoinvent-version-2/methodology-of-ecoinvent-2/ecospold1/ecospold1.html)
data sets. While EcoSpold 1 is maybe outdated and was replaced by
[EcoSpold 2](https://www.ecoinvent.org/data-provider/data-provider-toolkit/ecospold2/ecospold2.html);
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


## Usage

## Format Details

### The Root Element
An EcoSpold data set can contain multiple process data sets 

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
