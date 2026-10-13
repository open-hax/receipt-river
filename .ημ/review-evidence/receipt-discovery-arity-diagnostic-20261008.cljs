(ns diagnostic.arity)

;; A bounded representation probe, not the owning discovery suite or a repair.
(defn exec-at
  ([cwd args] (exec-at cwd args {}))
  ([_cwd _args _opts] :original))
(defn git-value [path args] (exec-at path args))
(def delayed-git (fn [_path _args] :replacement))

(defn -main []
  (let [control (git-value "unused" [])
        original-property? (fn? (aget exec-at "cljs$core$IFn$_invoke$arity$2"))
        replacement-property? (fn? (aget delayed-git "cljs$core$IFn$_invoke$arity$2"))
        observed (with-redefs [exec-at delayed-git]
                   (try
                     {:returned (git-value "unused" [])}
                     (catch :default error
                       {:error-name (.-name error)
                        :message (.-message error)})))
        restored (git-value "unused" [])
        reproduced? (and (= :original control)
                         original-property?
                         (not replacement-property?)
                         (= "TypeError" (:error-name observed))
                         (= :original restored))]
    (println (js/JSON.stringify
              (clj->js {:schema "codex.cljs-arity-representation-probe/v1"
                        :static-fns true :optimizations "none"
                        :node (.-version js/process)
                        :control (name control)
                        :original-arity-property original-property?
                        :replacement-arity-property replacement-property?
                        :replacement-observation observed
                        :original-restored (name restored)
                        :mechanism-reproduced reproduced?
                        :production-test-suite-executed false
                        :production-git-processes-started 0
                        :source-or-test-repair false})))
    (when-not reproduced? (set! (.-exitCode js/process) 1))))
(set! *main-cli-fn* -main)
