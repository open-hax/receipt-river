(ns eta-mu.receipt-river.law.completion
  "Explicit missing-metadata completion; never rewrites or validates raw history."
  (:require [clojure.string :as str]))

(defn assess
  "Admit a later validated event supplying exactly absent repo/pi metadata.
   Rows and hashes come from the owner decoder, not caller-provided assertions.
   This is record admission, not authentication of the event's asserted producer."
  [rows correction-row]
  (let [record (:event correction-row)
        payload (:event/payload record)
        decisions (:decisions payload)
        malformed-decisions? (and (map? payload) (contains? payload :decisions)
                                  (not (map? decisions)))
        claim (get-in record [:event/payload :decisions :receipt-river/metadata-completion])
        declared? (and (map? decisions) (contains? decisions :receipt-river/metadata-completion))
        line-number (:line-number claim)
        target (when (and (integer? line-number) (pos? line-number)
                          (< line-number (:line-number correction-row)))
                 (get rows (dec line-number)))
        original (:event target)
        fields (:fields claim)
        missing (when (map? original)
                  (set (filter #(not (contains? original %)) [:repo :pi])))
        completed (when (and (map? original) (map? fields)) (merge original fields))]
    (when (or declared? malformed-decisions?)
      (if (and (not malformed-decisions?) (:ok correction-row)
               (contains? record :event/schema)
               (= :adjudication (get-in record [:event/payload :kind]))
               (map? claim)
               (map? fields)
               (= #{:version :line-number :sha256 :fields} (set (keys claim)))
               (= 1 (:version claim))
               target (not (:ok target)) (map? original)
               (not (contains? original :event/schema))
               (seq missing) (= missing (set (keys fields)))
               (= (:sha256 claim) (:sha256 target))
               (= (set (:errors target))
                  (set (map #(str "missing required key: " (name %)) missing)))
               (every? #(and (string? %) (not (str/blank? %))) (vals fields)))
        {:ok true :target-line line-number :correction-line (:line-number correction-row)
         :source-sha256 (:sha256 target) :completed-record completed}
        {:ok false :correction-line (:line-number correction-row)
         :error "Invalid metadata completion; source history remains invalid"}))))

(defn summarize
  "Keep raw validation and explicit completion admission as separate results."
  [rows]
  (let [completions (vec (keep #(assess rows %) rows))
        targets (map :target-line (filter :ok completions))
        duplicates? (not= (count targets) (count (set targets)))
        admitted (if duplicates? #{} (set targets))]
    {:ok (and (not duplicates?) (every? :ok completions)
              (every? #(or (:ok %) (contains? admitted (:line-number %))) rows))
     :raw-ok (every? :ok rows)
     :rows rows
     :completions completions
     :errors (cond-> [] duplicates? (conj "Multiple completions target the same source line"))}))
