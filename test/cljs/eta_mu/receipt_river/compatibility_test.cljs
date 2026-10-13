(ns eta-mu.receipt-river.compatibility-test
  (:require [clojure.string :as str]
            [cljs.test :refer [deftest is]]
            ["node:fs" :as fs]
            ["node:os" :as os]
            ["node:path" :as path]
            [eta-mu.receipt-river.api :as api]
            [eta-mu.receipt-river.domain.event :as event]
            [eta-mu.receipt-river.event-test :as fixtures]
            [eta-mu.receipt-river.extern.bus :as bus]
            [eta-mu.receipt-river.extern.crypto :as crypto]
            [eta-mu.receipt-river.extern.git :as git]
            [eta-mu.receipt-river.extern.runtime :as runtime]
            [eta-mu.receipt-river.infra.cli :as cli]
            [eta-mu.receipt-river.law.receipt :as law]
            [eta-mu.receipt-river.shape.edn :as edn]))

(def containing-path "/actual/containing/repository")
(def containing-context {:repository/path containing-path})
(def missing-repo (dissoc fixtures/valid-legacy-record :repo))
(def missing-repo-error "missing required key: repo")
(def derived-attribution
  {:path containing-path :basis :containing-repository :tier :derived})

(defn context-result [line number context]
  ;; Apply exercises the requested public arity without suppressing a compiler
  ;; warning about an arity that the baseline does not yet implement.
  (apply api/validate-line [line number context]))

(defn frozen-suffix []
  (.readFileSync fs "test/fixtures/receiver-repo-less-1c4c53.edn" "utf8"))

(deftest real-receiver-lines-retain-original-records-test
  (let [suffix (frozen-suffix)
        lines (str/split-lines suffix)]
    (is (= 10437 (.-length (js/Buffer.from suffix "utf8"))))
    (is (= "592b8c95c84c82d3c9df8d4291e7aa768f3ebe9242aa6971e81be21550ad6cfa"
           (crypto/short-sha256 suffix 64)))
    (is (= 3 (count lines)))
    (doseq [[index line] (map-indexed vector lines)]
      (let [original (edn/parse-line line)
            baseline (api/validate-line line (+ 256 index))
            contextual (context-result line (+ 256 index) containing-path)]
        (is (not (contains? original :repo)))
        (is (not (contains? original :event/schema)))
        (is (= [missing-repo-error] (:errors baseline)))
        (is (false? (:ok baseline)))
        (is (:ok contextual))
        (is (= [] (:errors contextual)))
        (is (= (:errors baseline) (:source/context-free-errors contextual)))
        (is (= derived-attribution (:source/repository contextual)))
        (is (= original (:event contextual)))
        (is (= line (:line contextual)))
        (is (= (+ 256 index) (:line-number contextual)))
        (is (= {:status :unversioned} (:source/schema contextual)))))
    (is (= suffix (frozen-suffix)))))

(deftest context-is-explicit-and-closed-test
  (let [line (pr-str missing-repo)
        baseline (api/validate-line line 7)]
    (doseq [context [nil "" " \n\t" false 42 []
                     {:repository/path containing-path}
                     #js {:repositoryPath containing-path}]]
      (is (= baseline (context-result line 7 context))))
    (doseq [context [nil {} "path" false []
                     {:repository/path nil}
                     {:repository/path " "}
                     {:repository/path 42}
                     {:repository/path containing-path :extra true}
                     #js {:repositoryPath containing-path}]]
      (is (= [missing-repo-error]
             (apply law/record-errors [missing-repo context]))))
    (is (= [] (apply law/record-errors [missing-repo containing-context])))))

(deftest explicit-repository-values-are-never-replaced-test
  (doseq [repo [nil false "/explicit/repository"]]
    (let [record (assoc missing-repo :repo repo)
          line (pr-str record)
          baseline (api/validate-line line 11)
          contextual (context-result line 11 containing-path)]
      (is (= (:ok baseline) (:ok contextual)))
      (is (= (:errors baseline) (:errors contextual)))
      (is (= record (:event contextual)))
      (is (= line (:line contextual)))
      (is (not (contains? contextual :source/repository)))
      (is (= (:errors baseline) (:source/context-free-errors contextual)))))
  (is (:ok (context-result (pr-str (assoc missing-repo :repo false))
                          11 containing-path))))

(deftest other-malformed-fields-preserve-ordered-errors-test
  (doseq [record [(dissoc missing-repo :ts)
                  (dissoc missing-repo :kind)
                  (dissoc missing-repo :owner)
                  (dissoc missing-repo :ts :kind :owner)
                  (assoc missing-repo :kind :not-a-receipt-kind)
                  (assoc missing-repo :ts "2026-99-99T00:00:00Z")]]
    (let [line (pr-str record)
          baseline (api/validate-line line 17)
          contextual (context-result line 17 containing-path)]
      (is (false? (:ok contextual)))
      (is (= (filterv #(not= missing-repo-error %) (:errors baseline))
             (:errors contextual)))
      (is (= (:errors baseline) (:source/context-free-errors contextual)))
      (is (= derived-attribution (:source/repository contextual)))
      (is (= record (:event contextual)))
      (is (= line (:line contextual))))))

(deftest declared-envelopes-stay-strict-test
  (doseq [record [(event/build-event fixtures/metadata missing-repo)
                  (assoc missing-repo :event/schema nil)
                  (assoc missing-repo :event/schema "malformed")]]
    (let [line (pr-str record)
          baseline (api/validate-line line 19)
          contextual (context-result line 19 containing-path)]
      (is (false? (:ok contextual)))
      (is (= (:errors baseline) (:errors contextual)))
      (is (= (:source/schema baseline) (:source/schema contextual)))
      (is (= (:errors baseline) (:source/context-free-errors contextual)))
      (is (= record (:event contextual)))
      (is (not (contains? contextual :source/repository))))))

(deftest unreadable-input-cannot-acquire-attribution-test
  (with-redefs [bus/emit-error! (fn [_type _details] nil)]
    (let [line "{not closed"
          baseline (api/validate-line line 23)
          contextual (context-result line 23 containing-path)]
      (is (false? (:ok contextual)))
      (is (= (:errors baseline) (:errors contextual)))
      (is (= {:status :unreadable} (:source/schema contextual)))
      (is (nil? (:event contextual)))
      (is (= line (:line contextual)))
      (is (not (contains? contextual :source/repository))))))

(deftest ^:async existing-cli-supplies-actual-containing-checkout-test
  (let [root (.mkdtempSync fs (path/join (.tmpdir os) "receipt-context-"))
        nested (path/join root "nested")
        observed (atom [])
        exit-code (atom nil)
        original api/validate-line
        recording-validator
        (fn recording-validator
          ([line number] (recording-validator line number nil))
          ([line number containing]
           (swap! observed conj {:line line :number number :containing containing})
           (apply original [line number containing])))]
    (try
      (.mkdirSync fs nested)
      (.writeFileSync fs (path/join root "receipts.edn") (frozen-suffix))
      (is (zero? (:exit (await (git/exec-at root ["init" "-q"])))))
      (with-redefs [runtime/current-directory (constantly nested)
                    runtime/exit! #(reset! exit-code %)
                    api/validate-line recording-validator]
        (await (cli/handle {:args ["validate" "3"]})))
      (is (= 0 @exit-code))
      (is (= [root root root] (mapv :containing @observed)))
      (is (= [1 2 3] (mapv :number @observed)))
      (is (= (str/split-lines (frozen-suffix)) (mapv :line @observed)))
      (finally
        (.rmSync fs root #js {:recursive true :force true})))))

(deftest ^:async existing-cli-keeps-fallback-directory-context-free-test
  (let [root (.mkdtempSync fs (path/join (.tmpdir os) "receipt-no-git-"))
        file (path/join root "receipts.edn")
        suffix (frozen-suffix)
        observed (atom [])
        exit-code (atom nil)
        original api/validate-line
        recording-validator
        (fn recording-validator
          ([line number] (recording-validator line number nil))
          ([line number containing]
           (let [result (apply original [line number containing])]
             (swap! observed conj {:containing containing :result result})
             result)))]
    (try
      (.writeFileSync fs file suffix)
      (let [resolution (await (git/exec-at root ["rev-parse" "--show-toplevel"]))]
        ;; Exercise actual Git failure, including failure to start Git. An
        ;; inherited Git redirect must not turn this fixture into a checkout.
        (when (zero? (:exit resolution))
          (throw (ex-info "Non-Git fixture unexpectedly resolved a repository"
                          {:root root :resolved (:stdout resolution)})))
        (is (not (zero? (:exit resolution)))))
      (with-redefs [runtime/current-directory (constantly root)
                    runtime/exit! #(reset! exit-code %)
                    api/validate-line recording-validator]
        (await (cli/handle {:args ["validate" "3"]})))
      (is (= 1 @exit-code))
      (is (= [nil nil nil] (mapv :containing @observed)))
      (is (= [1 2 3] (mapv #(get-in % [:result :line-number]) @observed)))
      (doseq [[index {:keys [result]}] (map-indexed vector @observed)]
        (let [line (nth (str/split-lines suffix) index)]
          (is (false? (:ok result)))
          (is (= [missing-repo-error] (:errors result)))
          (is (not (contains? result :source/repository)))
          (is (= (edn/parse-line line) (:event result)))
          (is (= line (:line result)))))
      (is (= suffix (.readFileSync fs file "utf8")))
      (finally
        (.rmSync fs root #js {:recursive true :force true})))))
