(ns app.db)

(def bracket
  [[[{:name "Team A" :seed 1 :id "idA"}
     {:name "Team B" :seed 2 :id "idB"}]
    [{:name "Team C" :seed 1 :id "idC"}
     {:name "Team D" :seed 2 :id "idD"}]
    [{:name "Team E" :seed 1 :id "idE"}
     {:name "Team F" :seed 2 :id "idF"}]
    [{:name "Team G" :seed 1 :id "idG"}
     {:name "Team H" :seed 2 :id "idH"}]
    [{:name "Team I" :seed 1 :id "idI"}
     {:name "Team J" :seed 2 :id "idJ"}]
    [{:name "Team K" :seed 1 :id "idK"}
     {:name "Team L" :seed 2 :id "idL"}]
    [{:name "Team M" :seed 1 :id "idM"}
     {:name "Team N" :seed 2 :id "idN"}]
    [{:name "Team O" :seed 1 :id "idO"}
     {:name "Team P" :seed 2 :id "idP"}]]
   [[]
    []
    []
    []]
   [[]
    []]
   [[]]])

(def default-db
  {:bracket bracket})
