(ns flare.midje
  (:require [flare.core :refer [diff generate-reports]]
            [midje.emission.plugins.default :as default]
            [midje.emission.plugins.util :as util]
            [midje.emission.state :as state]))

(defn install! []

  (defn emit-reports
    [reports]
    (when (seq reports)
      (util/emit-one-line "")
      (doseq [report reports]
        (util/emit-one-line report))))

  (def default-fail-emissioner (:fail default/emission-map))

  (defn flare-fail-emissioner
    [failure]
    (default-fail-emissioner failure)
    (when (= (:type failure) :actual-result-did-not-match-expected-value)
      (some-> (diff (:expected-result failure) (:actual failure))
              generate-reports
              emit-reports)))

  (def emission-map (assoc default/emission-map
                      :fail flare-fail-emissioner))

  (state/install-emission-map-wildly emission-map))