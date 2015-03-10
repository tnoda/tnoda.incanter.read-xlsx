(ns tnoda.incanter.xlsx
  (:import (java.io InputStream)
           (org.apache.poi.ss.usermodel WorkbookFactory Row Cell))
  (:require [clojure.java.io :as io]
            [incanter.core :as core]))

(defn read-xlsx
  [uri & more]
  (let [sheet-index (first (take-while number? more))
        options (->> more
                     (drop-while #(= % sheet-index))
                     (partition 2)
                     (map vec)
                     (into {:as-dataset true}))
        _ (assert (or sheet-index (:sheet-name options))
                  "Please provide a sheet name OR a sheet index.")
        wb (-> uri io/input-stream WorkbookFactory/create)
        sheet (if sheet-index
                (.getSheetAt wb sheet-index)
                (.getSheet wb (:sheet-name options)))
        ;; Throw exception if the sheet requested has not been found
        _ (or sheet
              (throw (ex-info "Cannot find the sheet you requested in the file!"
                              {:uri uri
                               :sheet-index sheet-index
                               :sheet-name (:sheet-name options)})))
        ;; row-index should be zero-based
        row-index (if-let [row-index' (:row-index options)]
                    (map dec row-index')
                    (range (or (:start-row options)
                               (.getFirstRowNum sheet))
                           (or (:end-row options)
                               (inc (.getLastRowNum sheet)))))
        rows (keep #(.getRow sheet %) row-index)
        cell->val (fn
                    [^Cell c]
                    (and c
                         (case (.getCellType c)
                           4 (.getBooleanCellValue c)
                           0 (.getNumericCellValue c)
                           1 (.getStringCellValue c)
                           nil)))
        row->vals (fn
                    [^Row row]
                    (map (comp cell->val #(.getCell row %))
                         (or (:col-index options)
                             (range (.getFirstCellNum row)
                                    (.getLastCellNum row)))))
        matrix (map row->vals rows)
        header (->> (if (:header options)
                      (first matrix)
                      (range (apply max (map count matrix))))
                    (map (if (:header-keywords options)
                           keyword
                           identity)))
        body (if (:header options)
               (next matrix)
               matrix)]
    (if (:as-dataset options)
      (core/dataset header body)
      matrix)))
