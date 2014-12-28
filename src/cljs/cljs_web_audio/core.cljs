(ns cljs-web-audio.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :refer-macros [html]]
            [om-tools.core :refer-macros [defcomponent]]
            [dommy.core :as dommy :refer-macros [sel sel1]]))


;;; INFO: some browsers (safari) need js/window.AudioContext
;;;       webkit browsers need js/window.webkitAudioContext
(defonce ctx (js/AudioContext.))

(defonce app-state (atom {:context ctx
                          :oscillators {}
                          :n-oscs 0}))


(defn context
  []
  (js/AudioContext.))


(defn oscillator
  [ctx type & {:keys [freq detune] :or {:freq 440 :detune 0}}]
  (let [osc (.createOscillator ctx)]
    (set! (.-type osc) type)
    (set! (.. osc -frequency -value) freq)
    (set! (.. osc -detune -value) detune)
    osc))


(defcomponent osc-controller
  [data owner {:keys [type id render?]}]
  (init-state [_]
    (let [{:keys [context]} (om/get-props owner)]
      {:osc (oscillator context type)
       :gain (.createGain context)}))

  (will-mount [_]
    (let [{:keys [context]} (om/get-props owner)
          {:keys [osc gain]} (om/get-state owner)]
      (set! (.. gain -gain -value) 0)
      (.connect osc gain)
      (.connect gain (.-destination context))
      (.start osc)))
  
  (render-state [_ {:keys [osc gain]}]
    (html [:div.controller
           [:div [:h2 (str "Oscellator: " type)]]

           [:div#frequency
            [:input {:id (str "freq-" id) :type "text"}]
            "hertz"
            [:input {:type "submit" :value "Update frequency"
                     :on-click (fn [e]
                                 (set!
                                   (.. osc -frequency -value)
                                   (dommy/value (sel1 (str "#freq-" id)))))}]]

           [:div#volume
            [:input {:id (str "volume-" id) :type "text"}]
            [:input {:type "submit" :value "volume"
                     :on-click (fn [_]
                                 (set!
                                   (.. gain -gain -value)
                                   (dommy/value (sel1 (str "#volume-" id)))))}]
            "Note: Volume ranges from 0 to 1"]

           [:div#delete
            [:input {:type "submit" :value "Delete"
                     :on-click (fn [_]
                                 (.stop osc)
                                 #_(om/update! data
                                   [:oscillators (keyword id)]
                                   dissoc)

                                 (om/update! data
                                     [:oscillators (keyword id) :render?]
                                     false))}]]])))


(defcomponent osc-creator
  [data owner]
  (render [_]
    (let [_ nil]
      (html [:div#music

             [:div#oscillator
              [:h1 "Create Oscillator"]

              [:select#osc-type
               [:option {:value "sine"} "sin"]
               [:option {:value "sawtooth"} "saw"]
               [:option {:value "square"} "square"]
               [:option {:value "triangle"} "triangle"]
               [:option {:value "custom"} "custom"]]

              [:input {:type "submit" :value "New"
                       :on-click
                       (fn [e]
                         (let [id (str "osc" (:n-oscs @data))]
                           (om/transact! data [:oscillators]
                             #(assoc % (keyword id)
                                {:type (dommy/value
                                         (sel1 :#osc-type))
                                 :id id
                                 :render? true})))
                         
                         (om/transact! data [:n-oscs] inc))}]]]))))


;;; FIXME: go through the calls to build and check if data needs to be passed
;;;        to all of them, or if they should be passed different data.
(defcomponent music-interface
  [data owner]
  (render [_]
    (html [:div#interface
           (om/build osc-creator data)
           (for [[k osc] (:oscillators data)
                 :let [{:keys [render?]} osc]
                 :when render?]
             (om/build osc-controller data
               {:opts osc}))])))


(defn main []
  (om/root
    music-interface
    app-state
    {:target (. js/document (getElementById "app"))}))
