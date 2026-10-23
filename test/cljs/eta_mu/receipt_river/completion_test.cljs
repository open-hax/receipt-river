(ns eta-mu.receipt-river.completion-test
  (:require [cljs.test :refer [deftest is]]
            [eta-mu.receipt-river.api :as api]
            [eta-mu.receipt-river.extern.crypto :as crypto]))

(def original
  {:ts "2026-10-23T02:00:00Z" :kind :decision :origin "shared-login" :owner "codex"
   :dod "shared login" :host "stealth" :manifest [] :refs []})

(def raw (pr-str original))

(defn correction [line digest fields]
  (pr-str
    (api/build-event
      {:event-id "metadata-completion-1" :recorded-at "2026-10-23T03:00:00Z"
       :component-manifest {:eta-mu/version "1.1.1"} :command "receipt completion"
       :producer {:actor "codex"} :subject {:repo "riatzukiza/axxium"}}
      (assoc original :repo "riatzukiza/axxium" :pi "identity implementation"
             :kind :adjudication
             :decisions {:receipt-river/metadata-completion
                         {:version 1 :line-number line :sha256 digest :fields fields}}))))

(def completion (correction 1 (crypto/short-sha256 raw 64)
                            {:repo "riatzukiza/axxium" :pi "identity implementation"}))

(deftest explicit-completion-retains-raw-invalidity
  (let [result (api/validate-ledger [raw completion])]
    (is (:ok result))
    (is (false? (:raw-ok result)))
    (is (= raw (get-in result [:rows 0 :line])))
    (is (false? (get-in result [:rows 0 :ok])))
    (is (= (assoc original :repo "riatzukiza/axxium" :pi "identity implementation")
           (get-in result [:completions 0 :completed-record])))))

(deftest wrong-target-and-semantic-rewrites-refuse
  (doseq [bad [(correction 1 (apply str (repeat 64 "0")) {:repo "r" :pi "p"})
               (correction 3 (crypto/short-sha256 raw 64) {:repo "r" :pi "p"})
               (correction 1 (crypto/short-sha256 raw 64) {:repo "r"})
               (correction 1 (crypto/short-sha256 raw 64) {:repo "r" :pi "p" :owner "other"})
               (correction 1 (crypto/short-sha256 raw 64) {:repo "" :pi "p"})]]
    (is (false? (:ok (api/validate-ledger [raw bad]))))))

(deftest corrections-must-be-later-unique-and-validated
  (is (false? (:ok (api/validate-ledger [completion raw]))))
  (is (false? (:ok (api/validate-ledger [raw completion completion]))))
  (is (false? (:ok (api/validate-ledger [raw (pr-str {:decisions {:receipt-river/metadata-completion {}}})])))))

(deftest valid-legacy-map-cannot-impersonate-a-versioned-adjudication
  (let [legacy (assoc original :repo "r" :pi "p"
                      :event/payload {:kind :adjudication
                                      :decisions {:receipt-river/metadata-completion
                                                  {:version 1 :line-number 1
                                                   :sha256 (crypto/short-sha256 raw 64)
                                                   :fields {:repo "r" :pi "p"}}}})]
    (is (:ok (api/validate-line (pr-str legacy) 2)))
    (is (false? (:ok (api/validate-ledger [raw (pr-str legacy)]))))))

(deftest malformed-semantic-records-cannot-be-completed
  (let [bad (pr-str (assoc original :kind :made-up))
        fix (correction 1 (crypto/short-sha256 bad 64) {:repo "r" :pi "p"})]
    (is (false? (:ok (api/validate-ledger [bad fix]))))))

(deftest independent-and-single-field-completions
  (let [one (pr-str (assoc original :repo "already-recorded"))
        two (pr-str (assoc original :pi "already-recorded"))
        first-fix (correction 1 (crypto/short-sha256 one 64) {:pi "explicit-pi"})
        second-fix (correction 2 (crypto/short-sha256 two 64) {:repo "explicit-repo"})
        result (api/validate-ledger [one two first-fix second-fix])]
    (is (:ok result))
    (is (false? (:raw-ok result)))
    (is (= 2 (count (:completions result))))
    (is (= "already-recorded" (get-in result [:completions 0 :completed-record :repo])))
    (is (= "already-recorded" (get-in result [:completions 1 :completed-record :pi])))))

(deftest malformed-declarations-refuse-without-throwing-or-disappearing
  (doseq [claim [nil false true "bad" []]]
    (let [event (:event (api/validate-line completion 2))
          malformed (pr-str (assoc-in event [:event/payload :decisions :receipt-river/metadata-completion] claim))
          result (api/validate-ledger [raw malformed])]
      (is (false? (:ok result)))
      (is (= 1 (count (:completions result))))
      (is (false? (get-in result [:completions 0 :ok]))))))

(deftest explicit-nil-metadata-is-never-overwritten
  (let [source (pr-str (assoc original :repo nil))
        fix (correction 1 (crypto/short-sha256 source 64) {:repo "replacement" :pi "p"})]
    (is (false? (:ok (api/validate-ledger [source fix]))))))

(deftest malformed-field-containers-refuse-without-throwing
  (doseq [fields [nil false true "bad" []]]
    (let [fix (correction 1 (crypto/short-sha256 raw 64) fields)]
      (is (false? (:ok (api/validate-ledger [raw fix])))))))
