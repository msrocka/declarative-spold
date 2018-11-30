# declarative-spold
This is a small Clojure library for writing EcoSpold 1 data sets.


 EcoSpold 1 is
outdated, was replaced by EcoSpold 2, is ugly, and misses a lot of features.

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
      :type   "product"  :name   "Electricity"
      :amount 42.0       :unit   "kWh")
    (output
      :name          "CO2"
      :category      "Emissions to air"
      :sub-category  "unspecified"
      :amount  2.0   :unit "kg")))
```


## Usage

FIXME

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
