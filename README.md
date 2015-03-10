# tnoda.incanter.xlsx

This library provides a Incanter function to read Excel 2007 and Excel 97/2000/xp/2003 file formats.

## Usage


```clojure
(require '[tnoda.incanter.xlsx :as xlsx])

(xlsx/read-xls "path/to/your/xlsx/file" zero-based-sheet-index)
```

## License

Copyright © 2015 Takahiro Noda

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
