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

FIXME

## License

Copyright © 2018 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
